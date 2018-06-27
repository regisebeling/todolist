package com.example.reb.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by regis on 19/10/17.
 */

public class DBTask extends SQLiteOpenHelper {
    private static final String NOME_BANCO = "todo.db";
    private static final int VERSAO_SCHEMA = 1;

    public DBTask(Context context) {
        super(context, NOME_BANCO, null, VERSAO_SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE tasks (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " title TEXT, description TEXT, dateLimit TEXT, dateDone TEXT, done INTEGER);");



        //Insert the first task on the list
        ContentValues values = new ContentValues();

        values.put("_id", 0);
        values.put("title", "Terminar ToDo List");
        values.put("description", "Terminar a lista de tasks a fazer");
        values.put("dateLimit", "31/10/2017");
        values.put("dateDone", "0");
        values.put("done", 0);

        db.insert("tasks", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    public void insert(int _id, String title, String description, String dateLimit, String dateDone, int done) {
        ContentValues values = new ContentValues();
        values.put("_id", (byte[]) null);
        values.put("title", title);
        values.put("description", description);
        values.put("dateLimit", dateLimit);
        values.put("dateDone", dateDone);
        values.put("done", done);


        getWritableDatabase().insert("tasks", null, values);
    }


    //Update the task when it's checked to done
    public void updateToDone(int _id) {

        ContentValues values = new ContentValues();
        String date = DateFormat.getDateInstance().format(new Date());

        values.put("dateDone", date);
        values.put("done", 1);

        getWritableDatabase().update("tasks", values, "_id = '"+_id+"'", null);
    }

    public void updateTask(int _id, String title, String description, int done, String date) {


        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("dateLimit", date);
        values.put("done", done);


        getWritableDatabase().update("tasks", values, "_id = '"+_id+"'", null);
    }

    public Cursor getTasks() {
        return getReadableDatabase().rawQuery("select _id, title, description, dateLimit, dateDone, done FROM tasks ORDER BY _id", null);
    }

    public Cursor getDone() {
        return getReadableDatabase().rawQuery("select _id, title, description, dateLimit, dateDone, done FROM tasks  where done = 1 ORDER BY _id", null);
    }

    public Cursor getToDo() {
        return getReadableDatabase().rawQuery("select _id, title, description, dateLimit, dateDone, done FROM tasks  where done = 0 ORDER BY _id", null);
    }

    public String getId(Cursor c) {
        return c.getString(0);
    }
    public String getTitle(Cursor c) {
        return c.getString(1);
    }
    public String getDescription(Cursor c) {
        return c.getString(2);
    }
    public String getDateLimit(Cursor c) {
        return c.getString(3);
    }
    public String getDateDone(Cursor c) {
        return c.getString(4);
    }
    public String getDone(Cursor c) {
        return c.getString(5);
    }


}
