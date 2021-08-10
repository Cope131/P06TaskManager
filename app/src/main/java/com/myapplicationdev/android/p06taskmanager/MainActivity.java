package com.myapplicationdev.android.p06taskmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.RemoteInput;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final int ADD = 201;
    private final int ADD_OR_UPDATE = 301;

    // List View Components
    private ListView tasksListView;
    private ArrayAdapter<Task> arrayAdapter;
    private ArrayList<Task> tasks;

    // DB
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initListComps();
        loadTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();

        CharSequence reply = null;
        Intent intent = getIntent();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null){
            reply = remoteInput.getCharSequence("status");
        }

        if(reply != null){
            reply = reply + "";
            Toast.makeText(MainActivity.this, reply, Toast.LENGTH_SHORT).show();

            long id = getIntent().getLongExtra("id", 0);
            if (reply.equals("Completed")) {
                Toast.makeText(this, "id: " + id, Toast.LENGTH_SHORT).show();
                int result = db.deleteTask(id + "");
                if (result == 1) {
                    Toast.makeText(this, "Task Deleted with id: " + id, Toast.LENGTH_SHORT).show();
                    loadTasks();
                }
            } else if (reply.equals("Not yet")) {
                Toast.makeText(this, "id: " + id, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initViews() {
        Button addTaskBtn = findViewById(R.id.add_task_button);
        addTaskBtn.setOnClickListener(this);
    }

    private void initListComps() {
        tasksListView = findViewById(R.id.tasks_list_view);
        tasks = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, tasks);
        tasksListView.setAdapter(arrayAdapter);
        tasksListView.setOnItemClickListener(this);
    }

    private void loadTasks() {
        db = new DBHelper(this);
        tasks.clear();
        tasks.addAll(db.getAllTasks());
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        // TODO: go to add activity
        Intent intentAdd = new Intent(MainActivity.this, AddActivity.class);
        startActivityForResult(intentAdd, ADD);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, UpdateOrDeleteActivity.class);
        intent.putExtra("task", tasks.get(position));
        startActivityForResult(intent, ADD_OR_UPDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD:
                case ADD_OR_UPDATE:
                    loadTasks();
            }
        }
    }
}