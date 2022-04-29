package edu.mg.eni.m2.patient.consultation.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractDao {
    public SQLiteDatabase mDb;

    public int delete(String str, String str2, String[] strArr) {
        return this.mDb.delete(str, str2, strArr);
    }

    public long insert(String str, ContentValues contentValues) {
        return this.mDb.insert(str, (String) null, contentValues);
    }

    public AbstractDao(SQLiteDatabase sQLiteDatabase) {
        this.mDb = sQLiteDatabase;
    }

    public Cursor query(String str, String[] strArr, String str2, String[] strArr2, String str3) {
        return this.mDb.query(str, strArr, str2, strArr2, (String) null, (String) null, str3);
    }

    public Cursor query(String str, String[] strArr, String str2, String[] strArr2, String str3, String str4) {
        return this.mDb.query(str, strArr, str2, strArr2, (String) null, (String) null, str3, str4);
    }

    public Cursor query(String str, String[] strArr, String str2, String[] strArr2, String str3, String str4, String str5, String str6) {
        return this.mDb.query(str, strArr, str2, strArr2, str3, str4, str5, str6);
    }

    public int update(String str, ContentValues contentValues, String str2, String[] strArr) {
        return this.mDb.update(str, contentValues, str2, strArr);
    }

    public Cursor rawQuery(String str, String[] strArr) {
        return this.mDb.rawQuery(str, strArr);
    }
}
