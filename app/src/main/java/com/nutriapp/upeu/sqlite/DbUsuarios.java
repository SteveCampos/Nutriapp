package com.nutriapp.upeu.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbUsuarios {

    //Campos
    public static final String US_id = "_id";
    public static final String US_idGoogle = "usuario_id";
    public static final String US_usuario = "usuario_usuario";
    public static final String US_imageProfile = "usuario_imageProfile";
    public static final String US_last_name = "usuario_last_name";
    public static final String US_cover = "usuario_cover";
    public static final String US_correo= "usuario_correo";
    public static final String US_estado = "usuario_estado";

    private DbHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String SQLITE_TABLE_USUARIOS = "USUARIOS";
    private final Context mCtx;

    public static final String CREATE_USUARIOS =
            "create table if not exists " + SQLITE_TABLE_USUARIOS + " ("
                    + US_id + " integer primary key autoincrement,"
                    + US_idGoogle + " text,"
                    + US_usuario + " text,"
                    + US_last_name + " text,"
                    + US_imageProfile + " blob,"
                    + US_cover + " blob,"
                    + US_correo + " text,"
                    + US_estado + " integer);";

    public static final String DELETE_USUARIOS = "DROP TABLE IF EXISTS " + SQLITE_TABLE_USUARIOS;

    public DbUsuarios(Context ctx) {
        this.mCtx = ctx;
    }

    public DbUsuarios open() throws SQLException {
        mDbHelper = new DbHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createUser(String googleId, byte[] imageProfile,byte[] imageCover,String correo,String familyName,String givenName, int estado) {
        ContentValues initialValues = new ContentValues();
        long state = -10;

        if(ifExistUser(googleId)){


            initialValues.put(US_idGoogle, googleId);
            initialValues.put(US_usuario, givenName);
            initialValues.put(US_imageProfile, imageProfile);
            initialValues.put(US_cover, imageCover);
            initialValues.put(US_correo, correo);
            initialValues.put(US_last_name, familyName);
            initialValues.put(US_estado, estado);
            state =mDb.update(SQLITE_TABLE_USUARIOS, initialValues,
                    US_idGoogle + "=?", new String[]{"" + googleId});

        }else {
            initialValues.put(US_idGoogle, googleId);
            initialValues.put(US_usuario, givenName);
            initialValues.put(US_imageProfile, imageProfile);
            initialValues.put(US_cover, imageCover);
            initialValues.put(US_correo, correo);
            initialValues.put(US_last_name, familyName);
            initialValues.put(US_estado, estado);
            state = mDb.insert(SQLITE_TABLE_USUARIOS, null, initialValues);        }
        return  state;


    }
    private boolean ifExistUser(String googleId){
        boolean state = false;
        Cursor cursor = mDb.rawQuery("select * from "+SQLITE_TABLE_USUARIOS+" where "+US_idGoogle+"='"+googleId+"';",null);
        if(cursor.moveToFirst()){
            state = true;
        }
        Log.e("TRAE DATOS",""+cursor.getCount()+"-"+googleId);
        return state;
    }
    public Cursor getUserById(String googleId){
        Cursor cursor = mDb.rawQuery("select * from "+SQLITE_TABLE_USUARIOS+" where "+US_idGoogle+"='"+googleId+"';",null);
        Log.e("TRAE DATOS",""+cursor.getCount());
        return  cursor;
    }
    public String getIdGoogleUser(){
        String s = "";
        Cursor cursor = mDb.rawQuery("select * from "+SQLITE_TABLE_USUARIOS+";",null);

        if (cursor.moveToFirst()){
            s = cursor.getString(cursor.getColumnIndexOrThrow(US_idGoogle));
        }
        Log.e("TRAE DATOS SI EXISTE",""+cursor.getCount()+"--"+s);
        return  s;
    }

}