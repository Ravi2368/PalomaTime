package com.arn.gab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by arn on 10/19/2017.
 */

public class UserDBRequests extends SQLiteOpenHelper {

    private static final String DATABASENAME = "REQUESTERINFO.DB";
    private static final int DATABASEVERSION = 1;
    private static final String CREATEQUERY = "CREATE TABLE "+ UserRequests.NewRequestsInfo.TABLENAME+"("+ UserRequests.NewRequestsInfo.SECURITYCODE+" TEXT,"+ UserRequests.NewRequestsInfo.SECURITYSET+" TEXT,"+ UserRequests.NewRequestsInfo.SECURITYHINT+" TEXT);";



    public UserDBRequests(Context context) {

        super(context,DATABASENAME,null,DATABASEVERSION);
        Log.e("DATABASE OPERATIONS","Database created / opened...");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATEQUERY);
        Log.e("DATABASE OPERATIONS","Table created....");

    }


    public void addInformations(String set,String code,String hint,SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(UserRequests.NewRequestsInfo.SECURITYSET,set);
        contentValues.put(UserRequests.NewRequestsInfo.SECURITYCODE,code);
        contentValues.put(UserRequests.NewRequestsInfo.SECURITYHINT,hint);
        db.insert(UserRequests.NewRequestsInfo.TABLENAME,null,contentValues);
        Log.e("DATABASE OPERATIONS","One row inserted ");

    }


    public Cursor getInformation(SQLiteDatabase db){

        Cursor cursor;
        String[] projections = {UserRequests.NewRequestsInfo.SECURITYSET,UserRequests.NewRequestsInfo.SECURITYCODE,UserRequests.NewRequestsInfo.SECURITYHINT};
        cursor = db.query(UserRequests.NewRequestsInfo.TABLENAME,projections,null,null,null,null,null,null);
        return cursor;

    }

    /*

    public void deleteInformation(String name,SQLiteDatabase sqLiteDatabase){

        String selection = UserRequests.NewRequestsInfo.REQUESTERNAME+ " LIKE ?";
        String[] selection_args = {name};
        sqLiteDatabase.delete(UserRequests.NewRequestsInfo.TABLENAME,selection,selection_args);

    }
    */

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
