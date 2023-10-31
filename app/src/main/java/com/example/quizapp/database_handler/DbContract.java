package com.example.quizapp.database_handler;

import android.provider.BaseColumns;

public class DbContract {
    public static class QuestionEntry implements BaseColumns {
        public static final String TABLE = "Question";
        public static final String ID = "id";
        public static final String CONTENT = "content";
        public static final String ANSWER_ID = "answer_id";
    }

    public static class AnswerEntry implements BaseColumns {
        public static final String TABLE = "Answer";
        public static final String ID = "id";
        public static final String CONTENT = "content";
        public static final String QUESTION_ID = "question_id";
    }

    public static class TurnEntry implements BaseColumns {
        public static final String TABLE = "Turn";
        public static final String ID = "id";
        public static final String CORRECT = "correct";
        public static final String TIME = "time";
    }
}
