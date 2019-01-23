package com.camp.bit.todolist.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.camp.bit.todolist.NoteOperator;
import com.camp.bit.todolist.R;
import com.camp.bit.todolist.beans.Note;
import com.camp.bit.todolist.beans.Priority;
import com.camp.bit.todolist.beans.State;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created on 2019/1/23.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);

    private static final String TAG = NoteViewHolder.class.getName();

    private final NoteOperator operator;

    private CheckBox checkBox;
    private TextView contentText;
    private TextView dateText;
    private View deleteBtn;

    private View itemLayout;

    public NoteViewHolder(@NonNull View itemView, NoteOperator operator) {
        super(itemView);
        this.operator = operator;

        checkBox = itemView.findViewById(R.id.checkbox);
        contentText = itemView.findViewById(R.id.text_content);
        dateText = itemView.findViewById(R.id.text_date);
        deleteBtn = itemView.findViewById(R.id.btn_delete);
        itemLayout = itemView.findViewById(R.id.item_view);
    }

    public void bind(final Note note) {
        contentText.setText(note.getContent());
        dateText.setText(SIMPLE_DATE_FORMAT.format(note.getDate()));

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(note.getState() == State.DONE);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                note.setState(isChecked ? State.DONE : State.TODO);
                operator.updateNote(note);

                //update UI, add delete line
                if (isChecked) {
                    contentText.setTextColor(Color.GRAY);
                    contentText.setPaintFlags(contentText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    contentText.setTextColor(Color.BLACK);
                    contentText.setPaintFlags(contentText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                }

                Log.d(TAG, "onCheckedChanged");
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operator.deleteNote(note);

                Log.d(TAG, "deleteBtn onClick");
            }
        });

        if (note.getState() == State.DONE) {
            contentText.setTextColor(Color.GRAY);
            contentText.setPaintFlags(contentText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            contentText.setTextColor(Color.BLACK);
            contentText.setPaintFlags(contentText.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //设置优先级显示颜色
        //high -> red
        //medium -> blue
        //low -> white
        if (note.getPriority() == Priority.HIGH) {
            itemLayout.setBackgroundColor(Color.RED);
        } else if (note.getPriority() == Priority.MEDIUM) {
            itemLayout.setBackgroundColor(Color.BLUE);
        } else {
            itemLayout.setBackgroundColor(Color.WHITE);
        }

    }
}
