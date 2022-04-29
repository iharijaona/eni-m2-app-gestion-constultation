package edu.mg.eni.m2.patient.consultation.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelpers extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "dbgestionConsultation";
    private static int DATABASE_VERSION = 4;
    private Context ctx;

    /* renamed from: db */
    private SQLiteDatabase f56db = getWritableDatabase();

    public DBHelpers(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
        this.ctx = context;
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(DBConstants.sqlMedecin);
        sQLiteDatabase.execSQL(DBConstants.sqlPatient);
        sQLiteDatabase.execSQL(DBConstants.sqlTraitement);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS medecin");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS patient");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS traitement");
        onCreate(sQLiteDatabase);
    }

    public void close() {
        this.f56db.close();
    }

    public SQLiteDatabase getDb() {
        return this.f56db;
    }
}
