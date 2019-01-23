package com.camp.bit.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    private static final String TAG = TodoDbHelper.class.getName();

    // TODO 定义数据库名、版本；创建数据库
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "todo.db";

    public TodoDbHelper(Context context) {
        //define database
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //define table
        db.execSQL(TodoContract.SQL_CREATE_TABLE);
        Log.d(TAG, "onCreate: " + TodoContract.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            switch (i) {
                case 1:
                    Log.d(TAG, "onUpgrade");
                    try {
                        db.execSQL("ALTER TABLE " + TodoContract.TodoEntry.TABLE_NAME +
                                " ADD " + TodoContract.TodoEntry.COLUMN_NAME_PRIORITY + " INTEGER DEFAULT 0");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
