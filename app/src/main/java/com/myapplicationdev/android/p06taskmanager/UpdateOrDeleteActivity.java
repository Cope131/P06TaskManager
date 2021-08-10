package com.myapplicationdev.android.p06taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateOrDeleteActivity extends AppCompatActivity {

    private Task task;
    private EditText nameTV, descTV;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_or_delete);
        loadTask();
    }

    private void loadTask() {
        db = new DBHelper(this);
        task = (Task) getIntent().getSerializableExtra("task");
        nameTV = findViewById(R.id.name_edit_text);
        descTV = findViewById(R.id.description_edit_text);
        nameTV.setText(task.getName());
        descTV.setText(task.getDesc());
    }

    public void onClickUpdateTask(View view) {
        String name = nameTV.getText().toString().trim();
        String desc = descTV.getText().toString().trim();

        if (!name.isEmpty() && !desc.isEmpty()) {
            task.setName(name);
            task.setDesc(desc);
            int result = db.updateTask(task);
            if (result == 1) {
                setResult(RESULT_OK);
                Toast.makeText(this, "Task Updated", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }
        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
    }

    public void onClickDeleteTask(View view) {
        int result = db.deleteTask(task.getId() + "");
        if (result == 1) {
            setResult(RESULT_OK);
            Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}