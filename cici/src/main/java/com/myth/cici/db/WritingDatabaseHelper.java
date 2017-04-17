package com.myth.cici.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.myth.cici.entity.Writing;

public class WritingDatabaseHelper
{
    private static final String TABLE_NAME = "writing";

    public static ArrayList<Writing> getAllWriting(Context context)
    {
        SQLiteDatabase db = DBManager.getDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + "  order by update_dt ", null);
        return getWritingFromCursor(cursor);
    }

    public static synchronized void saveWriting(Context context, Writing writing)
    {
        deleteWriting(context, writing);

        SQLiteDatabase db = DBManager.getDatabase();
        String sqlStr = "insert into " + TABLE_NAME + " ( id,bgimg,ci_id,create_dt,text,update_dt) values ( "
                + "?,?,?,?,?,?)";
        db.execSQL(sqlStr,
                new String[] {writing.getId() + "", writing.getBgimg(), writing.getCi_id() + "",
                        writing.getCreate_dt() + "", writing.getText(), System.currentTimeMillis() + ""});
        BackupTask.needBackup = true;
    }

    public static void deleteWriting(Context context, Writing writing)
    {
        SQLiteDatabase db = DBManager.getDatabase();

        db.execSQL("delete from " + TABLE_NAME + " where " + "id" + " = " + writing.getId());
        BackupTask.needBackup = true;
    }

    private static ArrayList<Writing> getWritingFromCursor(Cursor cursor)
    {
        ArrayList<Writing> list = new ArrayList<Writing>();
        while (cursor.moveToNext())
        {
            Writing data = new Writing();
            data.setId(cursor.getInt(cursor.getColumnIndex("id")));
            data.setBgimg(cursor.getString(cursor.getColumnIndex("bgimg")));
            data.setCi_id(cursor.getInt(cursor.getColumnIndex("ci_id")));
            data.setCreate_dt(cursor.getLong(cursor.getColumnIndex("create_dt")));
            data.setText(cursor.getString(cursor.getColumnIndex("text")));
            data.setUpdate_dt(cursor.getLong(cursor.getColumnIndex("update_dt")));
            list.add(data);
        }
        return list;
    }

}
