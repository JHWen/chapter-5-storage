package com.camp.bit.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    //create table sql
    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TodoEntry.TABLE_NAME +
            "(" + TodoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TodoEntry.COLUMN_NAME_DATE + " INTEGER," +
            TodoEntry.COLUMN_NAME_STATE + " INTEGER," +
            TodoEntry.COLUMN_NAME_CONTENT + " TEXT," +
            TodoEntry.COLUMN_NAME_PRIORITY + " INTEGER" + ")";

    public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME;


    private TodoContract() {
    }

    public static class TodoEntry implements BaseColumns {
        //默认自带_id属性
        public static final String TABLE_NAME = "note";

        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_CONTENT = "content";

        public static final String COLUMN_NAME_PRIORITY = "priority";

    }

}
