package com.undlabs.caturandi.mynotesapp.db;

import android.provider.BaseColumns;

public class DatabaseContract {
    public static String TABLE_NOTE = "note";

    public static final class NoteColumns implements BaseColumns {
        static String TITLE = "title";
        static String DESCRIPTION = "description";
        static String DATE = "date";
    }

}
