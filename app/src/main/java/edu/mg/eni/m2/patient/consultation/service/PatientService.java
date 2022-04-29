package edu.mg.eni.m2.patient.consultation.service;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

import edu.mg.eni.m2.patient.consultation.helpers.AbstractDao;
import edu.mg.eni.m2.patient.consultation.helpers.DBConstants;
import edu.mg.eni.m2.patient.consultation.model.Patient;

public class PatientService extends AbstractDao implements IServiceBase<Patient, String>  {
    public PatientService(SQLiteDatabase sQLiteDatabase) {
        super(sQLiteDatabase);
    }

    public Patient fetchById(String str) {
        Patient patient = new Patient();
        Cursor rawQuery = super.rawQuery("SELECT * FROM "+ DBConstants.PATIENT_TABLE_NAME +" WHERE "+DBConstants.PATIENT_ID+" = ?", new String[]{str});
        while (rawQuery.moveToNext()) {
            patient.setIdPat(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.PATIENT_ID)));
            patient.setNom(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.PATIENT_TEXT_NOM)));
            patient.setAdresse(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.PATIENT_TEXT_ADRESSE)));
        }
        return patient;
    }

    public ArrayList<Patient> fetchAll() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = super.rawQuery("SELECT * FROM  "+ DBConstants.PATIENT_TABLE_NAME + " ORDER BY " + DBConstants.PATIENT_TEXT_NOM + " ASC ", (String[]) null);
        while (rawQuery.moveToNext()) {
            Patient patient = new Patient();
            patient.setIdPat(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.PATIENT_ID)));
            patient.setNom(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.PATIENT_TEXT_NOM)));
            patient.setAdresse(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.PATIENT_TEXT_ADRESSE)));
            arrayList.add(patient);
        }
        return arrayList;
    }

    public boolean add(Patient patient) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.PATIENT_ID, patient.getIdPat());
        contentValues.put(DBConstants.PATIENT_TEXT_NOM, patient.getNom());
        contentValues.put(DBConstants.PATIENT_TEXT_ADRESSE, patient.getAdresse());
        super.insert(DBConstants.PATIENT_TABLE_NAME, contentValues);
        return true;
    }

    public boolean deleteAll(String str) {
        super.delete(DBConstants.PATIENT_TABLE_NAME, DBConstants.PATIENT_ID + " = ?", new String[]{str});
        return true;
    }

    public boolean update(Patient patient) {
        String[] strArr = {patient.getIdPat()};
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.PATIENT_TEXT_NOM, patient.getNom());
        contentValues.put(DBConstants.PATIENT_TEXT_ADRESSE, patient.getAdresse());
        super.update(DBConstants.PATIENT_TABLE_NAME, contentValues, DBConstants.PATIENT_ID + " = ?", strArr);
        return true;
    }
}
