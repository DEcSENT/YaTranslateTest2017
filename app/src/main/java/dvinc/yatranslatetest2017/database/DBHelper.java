package dvinc.yatranslatetest2017.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static dvinc.yatranslatetest2017.database.HistoryContract.HistoryEntry.*;

/**
 * Created by Space 5 on 01.04.2017.
 */

/**
 * Класс для создания базы данных.
 */
class DBHelper extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "translateHistory_database";

    DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEXT_INPUT + " TEXT, " +
                COLUMN_TEXT_TRANSLATED + " TEXT, " +
                COLUMN_LANGUAGES_FROM_TO + " TEXT, " +
                COLUMN_BOOKMARK + " TEXT);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DATABASE_VERSION < newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
