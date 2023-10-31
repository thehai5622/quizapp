package com.example.quizapp.database_handler;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.quizapp.model.Answer;
import com.example.quizapp.model.Question;
import com.example.quizapp.model.Turn;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Quiz";
    SQLiteDatabase db;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table Question
        String queryQuestion = "CREATE TABLE IF NOT EXISTS " + DbContract.QuestionEntry.TABLE + " (" +
                DbContract.QuestionEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.QuestionEntry.CONTENT + " TEXT, " +
                DbContract.QuestionEntry.ANSWER_ID + " INTEGER" + " )";
        db.execSQL(queryQuestion);

        // Create table Answer
        String queryAnswer = "CREATE TABLE IF NOT EXISTS " + DbContract.AnswerEntry.TABLE + " (" +
                DbContract.AnswerEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.AnswerEntry.CONTENT + " TEXT, " +
                DbContract.AnswerEntry.QUESTION_ID + " INTEGER REFERENCES " +
                DbContract.QuestionEntry.TABLE + "(" + DbContract.QuestionEntry.ID + ")" + " )";
        db.execSQL(queryAnswer);

        // Create table Turn
        String queryTurn = "CREATE TABLE IF NOT EXISTS " + DbContract.TurnEntry.TABLE + " (" +
                DbContract.TurnEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.TurnEntry.CORRECT + " INTEGER, " +
                DbContract.TurnEntry.TIME + " TEXT" + " )";
        db.execSQL(queryTurn);

        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.QuestionEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.AnswerEntry.TABLE);
        onCreate(db);
    }

    public int getQuestionCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DbContract.QuestionEntry.TABLE;
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;
        try {
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    public void insertQuestionAndAnswers(String questionContent, String[] answerContents, int correctAnswerIndex) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            // Create Question and get id question
            ContentValues questionValues = new ContentValues();
            questionValues.put(DbContract.QuestionEntry.CONTENT, questionContent);
            long questionId = db.insert(DbContract.QuestionEntry.TABLE, null, questionValues);

            // Create Answer and set question_id
            for (int i = 0; i < answerContents.length; i++) {
                ContentValues answerValues = new ContentValues();
                answerValues.put(DbContract.AnswerEntry.CONTENT, answerContents[i]);
                answerValues.put(DbContract.AnswerEntry.QUESTION_ID, questionId);
                long answerId = db.insert(DbContract.AnswerEntry.TABLE, null, answerValues);

                // Set answer_id of Question
                if (i == correctAnswerIndex - 1) {
                    ContentValues updateQuestionValues = new ContentValues();
                    updateQuestionValues.put(DbContract.QuestionEntry.ANSWER_ID, answerId);
                    db.update(DbContract.QuestionEntry.TABLE, updateQuestionValues, DbContract.QuestionEntry.ID + " = ?", new String[]{String.valueOf(questionId)});
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();

        // Select all rows from Question table
        String selectQuery = "SELECT * FROM " + DbContract.QuestionEntry.TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add each question to the list
        if (cursor.moveToFirst()) {
            do {
                // Retrieve question details
                @SuppressLint("Range") int questionId = cursor.getInt(cursor.getColumnIndex(DbContract.QuestionEntry.ID));
                @SuppressLint("Range") String questionContent = cursor.getString(cursor.getColumnIndex(DbContract.QuestionEntry.CONTENT));
                @SuppressLint("Range") int answerId = cursor.getInt(cursor.getColumnIndex(DbContract.QuestionEntry.ANSWER_ID));

                // Create a Question object
                Question question = new Question(questionId, questionContent, answerId);

                // Add the question to the list
                questionList.add(question);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return questionList;
    }

    public List<Answer> getAnswersForQuestion(int questionId) {
        List<Answer> answerList = new ArrayList<>();

        // Select all rows from Answer table for the given questionId
        String selectQuery = "SELECT * FROM " + DbContract.AnswerEntry.TABLE +
                " WHERE " + DbContract.AnswerEntry.QUESTION_ID + " = " + questionId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add each answer to the list
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int answerId = cursor.getInt(cursor.getColumnIndex(DbContract.AnswerEntry.ID));
                @SuppressLint("Range") String answerContent = cursor.getString(cursor.getColumnIndex(DbContract.AnswerEntry.CONTENT));
                answerList.add(new Answer(answerId, answerContent, questionId));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return answerList;
    }

    public void insertTurn(int numberCorrect, String time) {
        SQLiteDatabase db = getWritableDatabase();

        // Check count of table Turn
        String countQuery = "SELECT COUNT(*) FROM " + DbContract.TurnEntry.TABLE;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int rowCount = cursor.getInt(0);
        cursor.close();

        if (rowCount >= 5) {
            // Get the record with the lowest correct
            String minNumberCorrectQuery = "SELECT MIN(" + DbContract.TurnEntry.CORRECT + ") FROM " + DbContract.TurnEntry.TABLE;
            Cursor minCursor = db.rawQuery(minNumberCorrectQuery, null);
            minCursor.moveToFirst();
            int minNumberCorrect = minCursor.getInt(0);
            minCursor.close();

            if (numberCorrect > minNumberCorrect) {
                String deleteMinQuery = "DELETE FROM " + DbContract.TurnEntry.TABLE +
                        " WHERE " + DbContract.TurnEntry.CORRECT + " = " + minNumberCorrect;
                db.execSQL(deleteMinQuery);
            } else {
                return;
            }
        }

        // Add new record
        ContentValues values = new ContentValues();
        values.put(DbContract.TurnEntry.CORRECT, numberCorrect);
        values.put(DbContract.TurnEntry.TIME, time);
        db.insert(DbContract.TurnEntry.TABLE, null, values);

        db.close();
    }

    public List<Turn> getAllTurns() {
        List<Turn> turnList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DbContract.TurnEntry.TABLE +
                " ORDER BY " + DbContract.TurnEntry.CORRECT + " DESC, " + DbContract.TurnEntry.TIME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Loop through all rows and add each turn to the list
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int turnId = cursor.getInt(cursor.getColumnIndex(DbContract.TurnEntry.ID));
                @SuppressLint("Range") int correctAnswer = cursor.getInt(cursor.getColumnIndex(DbContract.TurnEntry.CORRECT));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(DbContract.TurnEntry.TIME));

                Turn turn = new Turn(turnId, correctAnswer, time);
                turnList.add(turn);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return turnList;
    }

    public int getMaxCorrectAnswer() {
        int maxCorrect = 0;

        // Select the maximum value of 'correct' column from Turn table
        String selectQuery = "SELECT MAX(" + DbContract.TurnEntry.CORRECT + ") FROM " + DbContract.TurnEntry.TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            maxCorrect = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return maxCorrect;
    }

    public void initQuestionAndAnswer() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check exists data
        String checkQuery = "SELECT * FROM " + DbContract.QuestionEntry.TABLE;
        Cursor cursor = db.rawQuery(checkQuery, null);
        if (cursor.getCount() == 0) {
            String questionContent;
            String[] answerContents;

            questionContent = "Nơi nào có đường xá, nhưng không có xe cộ, có nhà ở, nhưng không có người, có siêu thị, công ty... nhưng không có hàng hóa... Đó là nơi nào vậy?";
            answerContents = new String[]{"Ở ngoài hành tinh à", "Trên bản đồ", "Không coá đâu", "Bị chìm rồi"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = "Con đường dài nhất là đường gì?";
            answerContents = new String[]{"Đường tình", "Đường quyền", "Đường đời", "Đường đột"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);

            questionContent = "Quần rộng nhất là quần gì?";
            answerContents = new String[]{"Quần đảo", "Quần đùi", "Quần áo", "Quần hoa"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Môn gì càng thắng càng thua?";
            answerContents = new String[]{"Môn đua xe", "Môn học", "Môn bài", "Làm gì có"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Con gì đầu dê mình ốc?";
            answerContents = new String[]{"Con dê nhưng có đuôi ốc", "Con ốc cơ mà có đầu con dê", "Con dốc", "Con quái vật"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);

            questionContent = "Con gì đập thì sống, không đập thì chết?";
            answerContents = new String[]{"Con ma", "Con hâm", "Con rồng", "Con tim"};
            insertQuestionAndAnswers(questionContent, answerContents, 4);

            questionContent = "Cái gì mà đi thì nằm, đứng cũng nằm, nhưng nằm lại đứng?";
            answerContents = new String[]{"Cái dùi cui", "Bàn chân", "Cái bụng", "không coá đâu"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = "Sở thú bị cháy, con gì chạy ra đầu tiên?";
            answerContents = new String[]{"Con chim", "Con người", "Con ma", "Con sư tử"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = "Vừa bằng một thước, mà bước không qua\nLà cái gì?";
            answerContents = new String[]{"Cái thước dài hơn", "Cái thước dài", "Cái thìa", "Cái bóng"};
            insertQuestionAndAnswers(questionContent, answerContents, 4);

            questionContent = "Vô thủ, vô vỉ, vô nhĩ, vô tâm\n" +
                    "Gốc ở sơn lâm\n" +
                    "Hay ăn thịt sống\n" +
                    "Là cái gì?";
            answerContents = new String[]{"cái quần", "Cái áo", "Cái thớt", "Cái bàn"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);

            questionContent = "Da trắng muốt\n" +
                    "Ruột trắng tinh\n" +
                    "Bạn với học sinh\n" +
                    "Thích cọ đầu vào bảng?\n" +
                    "Là cái gì?";
            answerContents = new String[]{"Viên phấn", "Cái bảng", "Cái rẻ lau", "Cái chổi"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Mắt gì cách gối hai gang,\n" +
                    "Đem ra trình làng, chẳng biết chuyện chi. Là cái gì";
            answerContents = new String[]{"Mắt giả", "Mắt cá chân", "Mắt mèo", "Mắt chột"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = "Lưng nằm đằng trước, bụng thì phía sau?\n" +
                    "Là cái gì?";
            answerContents = new String[]{"Cái cẳng chân", "Cái đù đì 3 đuôi", "Cái bụng", "Con gái"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = " Có cây mà chẳng có cành\n" +
                    "Có hai ông cụ dập dềnh hai bên\n" +
                    "Là gì?";
            answerContents = new String[]{"Con chó", "Cây ngô", "Con cá", "Cây bưởi"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = " Da cóc mà bọc bột lọc\n" +
                    "Bột lọc mà bọc trứng gà\n" +
                    "Bổ ra thơm phức cả nhà muốn ăn\n" +
                    "Là gì?";
            answerContents = new String[]{"Quả sung", "Quả chuối", "Quả bưởi", "Quả mít"};
            insertQuestionAndAnswers(questionContent, answerContents, 4);

            questionContent = "Ruột chấm vừng đen\n" +
                    "Ăn vào mà xem\n" +
                    "Vừa mát vừa bổ\n" +
                    "Là gì?";
            answerContents = new String[]{"Quả cam", "Qủa táo", "Quả sung", "Quả thanh long"};
            insertQuestionAndAnswers(questionContent, answerContents, 4);

            questionContent = "Cây gì nho nhỏ\n" +
                    "Hạt nó nuôi người\n" +
                    "Chín vàng nơi nơi\n" +
                    "Dân làng đi hái";
            answerContents = new String[]{"Cây lúa", "Cây sung", "Cây gạo", "Cây trứng cá"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Ao tròn vành vạnh\n" +
                    "Nước lạnh như tiền\n" +
                    "Con gái như tiên\n" +
                    "Trần mình xuống lội";
            answerContents = new String[]{"Không coá đâu", "Nấu bánh trôi nước", "Tiên đi tắm ao", "Cái ao tắm tiên"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = " Có răng mà chẳng có mồm\n" +
                    "Nhai cỏ nhồm nhoàm, cơm chẳng chịu ăn";
            answerContents = new String[]{"Đại bác", "Cái súng", "Cái liềm", "Cái quốc"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);

            questionContent = "Con gì vỗ cánh bay nhanh\n" +
                    "Không đẻ trứng lại đẻ thành con ngay?";
            answerContents = new String[]{"Con chuồn chuồn", "Con bướm", "Con dơi", "Con chim yến"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);

            questionContent = "Hoa gì quả quyện với trầu\n" +
                    "Để cho câu chuyện mở đầu nên duyên?";
            answerContents = new String[]{"Hoa đà", "Hoa hậu", "Hoa dâm bụt", "Hoa cau"};
            insertQuestionAndAnswers(questionContent, answerContents, 4);

            questionContent = "Khi nhỏ, em mặc áo xanh\n" +
                    "Khi lớn bằng anh, em thay áo đỏ";
            answerContents = new String[]{"Quả ớt", "Quả máo", "Quả chanh", "Quả tắc"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Vừa bằng hạt đỗ, ăn giỗ cả làng. Là con gì?";
            answerContents = new String[]{"Con ruồi", "Con chó", "Con người", "Con mèo"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Bàn gì xe ngựa sớm chiều giơ ra";
            answerContents = new String[]{"Bàn cờ tướng", "Bàn đạp", "Bàn làm việc", "Bàn ăn"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Con gì có mũi có lưỡi hẳn hoi. Có sống không chết người đời cầm luôn?";
            answerContents = new String[]{"Con dao", "Con cái", "Con rắn", "Con dế"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Mặt gì để doạ người ta,\n" +
                    "Đeo vào trẻ sợ như ma hiện hồn?";
            answerContents = new String[]{"Mặt nạ", "Mặt hổ", "Mặt quỷ", "Mặt mộc"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Quả gì gang sắt đúc nên,\n" +
                    "Hễ nghe tiếng rú người liền núp mau?";
            answerContents = new String[]{"Quả bom", "Quả cam", "Quả chuông", "Quả dừa"};
            insertQuestionAndAnswers(questionContent, answerContents, 1);

            questionContent = "Bốn chân đạp đất từ bi\n" +
                    "Đã ăn chén sứ, ngại chi chén sành\n" +
                    "Là gì?";
            answerContents = new String[]{"Con ngựa", "Tủ đựng chén bát", "Con chó", "Cái chậu"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = "Trên hang đá, dưới hang đá\n" +
                    "Giữa có con cá thờn bơn Là gì?";
            answerContents = new String[]{"Con cầu thang", "Cái miệng", "Con đường", "Con gấu"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = "Hai cô nằm nghỉ hai phòng\n" +
                    "Ngày thì mở cửa ra trông\n" +
                    "Ðêm thì đóng cửa lấp trong ra ngoài\n" +
                    "Là gì?";
            answerContents = new String[]{"Cửa sổ", "Đôi mắt", "Mắt kính", "Cửa nhà"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = "Vừa bằng cái lá đa, đi xa về gần\n" +
                    "Là gì?";
            answerContents = new String[]{"Cánh tay", "Bàn chân", "Cây cầu", "Con đường"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = "Vừa bằng quả bí, nhi nhí những hột\n" +
                    "Là gì?";
            answerContents = new String[]{"Quả bí", "Nồi cơm", "Quả dưa hấu", "Quả bưởi"};
            insertQuestionAndAnswers(questionContent, answerContents, 2);

            questionContent = "Vừa bằng quả mướp, ăn cướp cà làng\n" +
                    "Là gì?";
            answerContents = new String[]{"Con thỏ", "Con ếch", "Con chuột", "Con chim sẻ"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);

            questionContent = "Vốn dòng ái quốc sưa nay\n" +
                    "Mà lòng giữ nước khi đầy khi vơi\n" +
                    "Là gì?";
            answerContents = new String[]{"Quốc kỳ", "Cờ", "Bình nước", "Trái tim"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);

            questionContent = "Thân dài thượt\n" +
                    "Ruột thẳng băng\n" +
                    "Khi thịt bị cắt khỏi chân\n" +
                    "Thì ruột lòi dần vẫn thẳng như rươi?\n" +
                    "Là cái gì?";
            answerContents = new String[]{"Bàn chải đánh răng", "Cây viết", "Cái bút chì", "Ống nghiệm"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);

            questionContent = "Cày trên đồng ruộng trắng phau\n" +
                    "Khát xuống uốmg nước giếng sâu đen ngòm?\n" +
                    "Là cái gì?";
            answerContents = new String[]{"Cái áo mưa", "Cái gậy", "Cái bút mực", "Cái chày"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);

            questionContent = "Bằng cái hạt cây\n" +
                    "Ba gian nhà đầy còn tràn ra sân?\n" +
                    "Là cái gì?";
            answerContents = new String[]{"Đèn dầu", "Quả cà phê", "Đèn dầu", "Quả đậu"};
            insertQuestionAndAnswers(questionContent, answerContents, 3);
        }

        cursor.close();
        db.close();
    }
}
