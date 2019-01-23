package com.camp.bit.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.camp.bit.todolist.beans.Priority;
import com.camp.bit.todolist.beans.State;
import com.camp.bit.todolist.db.TodoContract;
import com.camp.bit.todolist.db.TodoDbHelper;

import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private RadioGroup radioGroup;

    private static final String HIGH = "high";
    private static final String MEDIUM = "medium";
    private static final String LOW = "low";

    private TodoDbHelper todoDbHelper;
    private SQLiteDatabase database;

    private static final String TAG = NoteActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        todoDbHelper = new TodoDbHelper(this);
        database = todoDbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }

        addBtn = findViewById(R.id.btn_add);
        radioGroup = findViewById(R.id.radio_group);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取表单数据，content + priority
                Priority priority = null;
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                    if (radioButton.isChecked()) {
                        switch (radioButton.getText().toString()) {
                            case HIGH:
                                priority = Priority.HIGH;
                                break;
                            case MEDIUM:
                                priority = Priority.MEDIUM;
                                break;
                            case LOW:
                                priority = Priority.LOW;
                                break;
                            default:
                                priority = Priority.LOW;
                                break;
                        }
                        break;
                    }
                }
                boolean succeed = saveNote2Database(content.toString().trim(), priority);
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        database.close();
        todoDbHelper.close();
        super.onDestroy();
    }

    private boolean saveNote2Database(String content, Priority priority) {
        // TODO 插入一条新数据，返回是否插入成功

        ContentValues values = new ContentValues();
        values.put(TodoContract.TodoEntry.COLUMN_NAME_DATE, new Date().getTime());
        values.put(TodoContract.TodoEntry.COLUMN_NAME_STATE, State.TODO.intValue);
        values.put(TodoContract.TodoEntry.COLUMN_NAME_CONTENT, content);
        values.put(TodoContract.TodoEntry.COLUMN_NAME_PRIORITY, priority.intValue);

        long rowId = database.insert(
                TodoContract.TodoEntry.TABLE_NAME,
                null,
                values
        );
        Log.d(TAG, "saveNote2Database: " + rowId);
        return rowId != -1;
    }
}
