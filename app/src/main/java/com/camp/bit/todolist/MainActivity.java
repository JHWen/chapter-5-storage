package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.camp.bit.todolist.beans.Note;
import com.camp.bit.todolist.beans.Priority;
import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract.TodoEntry;
import com.camp.bit.todolist.db.TodoDbHelper;
import com.camp.bit.todolist.debug.DebugActivity;
import com.camp.bit.todolist.ui.NoteListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;
    private static final String TAG = MainActivity.class.getName();

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    private TodoDbHelper todoDbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        //construct dbHelper
        todoDbHelper = new TodoDbHelper(this);
        database = todoDbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        database.close();
        todoDbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        Log.d(TAG, "loadNotesFromDatabase");
        if (database == null) {
            return null;
        }

        String[] projection = {
                BaseColumns._ID,
                TodoEntry.COLUMN_NAME_DATE,
                TodoEntry.COLUMN_NAME_STATE,
                TodoEntry.COLUMN_NAME_CONTENT,
                TodoEntry.COLUMN_NAME_PRIORITY
        };

        String sortOrder = TodoEntry.COLUMN_NAME_PRIORITY + " DESC," +
                TodoEntry.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = null;
        List<Note> notes = new ArrayList<>();
        try {
            cursor = database.query(
                    TodoEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(TodoEntry._ID));
                long date = cursor.getLong(cursor.getColumnIndex(TodoEntry.COLUMN_NAME_DATE));
                int state = cursor.getInt(cursor.getColumnIndex(TodoEntry.COLUMN_NAME_STATE));
                String content = cursor.getString(cursor.getColumnIndex(TodoEntry.COLUMN_NAME_CONTENT));
                int priority = cursor.getInt(cursor.getColumnIndex(TodoEntry.COLUMN_NAME_PRIORITY));

                Note note = new Note(id);
                note.setDate(new Date(date));
                note.setState(State.from(state));
                note.setContent(content);
                note.setPriority(Priority.from(priority));

                notes.add(note);
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return notes;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        if (database == null) {
            return;
        }
        String selection = TodoEntry._ID + "= ?";

        String[] selectionArgs = {String.valueOf(note.id)};

        int deletedRows = database.delete(
                TodoEntry.TABLE_NAME,
                selection, selectionArgs
        );

        Log.d(TAG, "deleteNote: " + deletedRows);
        Log.d(TAG, "deleteNote: " + note.toString());
        //update UI RecycleView
        notesAdapter.refresh(loadNotesFromDatabase());
    }

    private void updateNode(Note note) {
        // 更新数据
        if (database == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(TodoEntry.COLUMN_NAME_STATE, note.getState().intValue);
        values.put(TodoEntry.COLUMN_NAME_DATE, note.getDate().getTime());
        values.put(TodoEntry.COLUMN_NAME_CONTENT, note.getContent());

        String selection = TodoEntry._ID + "= ?";
        String[] selectionArgs = {String.valueOf(note.id)};

        int count = database.update(
                TodoEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        Log.d(TAG, "updateNode:" + count);
        Log.d(TAG, "updateNode:" + note.toString());
    }

}
