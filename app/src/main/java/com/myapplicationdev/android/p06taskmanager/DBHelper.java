package com.myapplicationdev.android.p06taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DEBUG_TAG = DBHelper.class.getSimpleName();

    // Database
    private static final String DATABASE_NAME = "task.db";
    private static final int DATABASE_VERSION = 1;
    // Tables
    private static final String TABLE_TASK = "task";
    // Task Columns
    private static final String COLUMN_TASK_ID             = "_id";
    private static final String COLUMN_TASK_NAME           = "name";
    private static final String COLUMN_TASK_DESCRIPTION    = "description";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTaskTable = String.format(
                "CREATE TABLE %s ( " +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT , " +
                        "%s TEXT , " +
                        "%s TEXT )",
                TABLE_TASK,
                COLUMN_TASK_ID,
                COLUMN_TASK_NAME,
                COLUMN_TASK_DESCRIPTION );
        db.execSQL(createTaskTable);
        Log.d(DEBUG_TAG, "Task Table Created");
        Log.d(DEBUG_TAG, "Create Task Query: " + createTaskTable);
        insertDummyRecords(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_TASK));
        onCreate(db);
    }

    private void insertDummyRecords(SQLiteDatabase db) {
        for (int i = 0; i < 2; i++){
            insertTask(db, new Task(i, "Task " + i, "Task " + i + " desc"));
        }
    }

    public long insertTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, task.getName());
        values.put(COLUMN_TASK_DESCRIPTION, task.getDesc());
        long result = db.insert(TABLE_TASK, null, values);
        Log.d(DEBUG_TAG, "Insert Task Record Result: " + result) ;
        return result;
    }

    public void insertTask(SQLiteDatabase db, Task task) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, task.getName());
        values.put(COLUMN_TASK_DESCRIPTION, task.getDesc());
        long result = db.insert(TABLE_TASK, null, values);
        Log.d(DEBUG_TAG, "Insert Task Record Result: " + result) ;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        // Query All Tasks
        Cursor cursor = db.query(
                TABLE_TASK, null, null, null,
                null, null, null);
        Log.d(DEBUG_TAG, "Task Record Count: " + cursor.getColumnCount());
        // Add Task
        while (cursor.moveToNext()) {
            // Get Task Record
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String desc = cursor.getString(2);
            // Add Task
            Task task = new Task(id, name, desc);
            tasks.add(task);
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public int deleteTask(String id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_TASK, COLUMN_TASK_ID + " = ?", new String[]{id});
        db.close();
        Log.d(DEBUG_TAG, "Delete result: " + id);
        return result;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, task.getName());
        values.put(COLUMN_TASK_DESCRIPTION, task.getDesc());
        String condition = COLUMN_TASK_ID + " = ?";
        String[] args = {String.valueOf(task.getId())};
        int result = db.update(TABLE_TASK, values, condition, args);
        Log.d(DEBUG_TAG, "Update Task Record Result: " + result);
        db.close();
        return result;
    }
}
