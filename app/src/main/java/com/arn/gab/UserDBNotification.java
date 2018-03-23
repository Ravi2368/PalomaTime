package com.arn.gab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by arn on 11/13/2017.
 */

public class UserDBNotification extends SQLiteOpenHelper {

    private static final String DATABASENAME = "NOTIFINFO.DB";
    private static final int DATABASEVERSION = 1;
    private static final String CREATEQUERY = "CREATE TABLE "+ UserNotification.NewNotificationInfo.TABLENAME+"("+ UserNotification.NewNotificationInfo.NOTIFID+" TEXT,"+ UserNotification.NewNotificationInfo.NOTIFSTATUS+" TEXT);";

    public UserDBNotification(Context context) {

        super(context,DATABASENAME,null,DATABASEVERSION);
        Log.e("DATABASE OPERATIONS","Database created / opened...");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATEQUERY);
        Log.e("DATABASE OPERATIONS","Table created....");

    }

    public void addInformations(String id,String status,SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserNotification.NewNotificationInfo.NOTIFID,id);
        contentValues.put(UserNotification.NewNotificationInfo.NOTIFSTATUS,status);
        db.insert(UserNotification.NewNotificationInfo.TABLENAME,null,contentValues);
        Log.e("DATABASE OPERATIONS","One row inserted ");

    }

    public Cursor getInformation(SQLiteDatabase db){

        Cursor cursor;
        String[] projections = {UserNotification.NewNotificationInfo.NOTIFID,UserNotification.NewNotificationInfo.NOTIFSTATUS};
        cursor = db.query(UserNotification.NewNotificationInfo.TABLENAME,projections,null,null,null,null,null,null);
        return cursor;

    }

    public boolean updateData(String id,String status,SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserNotification.NewNotificationInfo.NOTIFID,id);
        contentValues.put(UserNotification.NewNotificationInfo.NOTIFSTATUS,status);
        db.update(UserNotification.NewNotificationInfo.TABLENAME, contentValues, UserNotification.NewNotificationInfo.NOTIFID + "=" + id, null);
        return true;

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}