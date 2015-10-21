package com.nutriapp.upeu.sqlite;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Kelvin on 28/09/2015.
 */
public class DbPersonas {

    //Campos
    public static final String US_id = "_id";
    public static final String US_idGoogle = "usuario_id";
    public static final String US_usuario = "usuario_usuario";
    public static final String US_imageProfile = "usuario_imageProfile";
    public static final String US_cover = "usuario_cover";
    public static final String US_correo= "usuario_correo";
    public static final String US_last_name = "usuario_last_name";
    public static final String US_estado = "usuario_estado";

    private DbHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String SQLITE_TABLE_USUARIOS = "PERSONAS";
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

    public DbPersonas(Context ctx) {
        this.mCtx = ctx;
    }

    public DbPersonas open() throws SQLException {
        mDbHelper = new DbHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

}
