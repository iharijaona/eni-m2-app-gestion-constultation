package edu.mg.eni.m2.patient.consultation.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

import edu.mg.eni.m2.patient.consultation.helpers.AbstractDao;
import edu.mg.eni.m2.patient.consultation.helpers.DBConstants;
import edu.mg.eni.m2.patient.consultation.model.Medecin;

public class MedecinService extends AbstractDao implements IServiceBase<Medecin, String> {

    public MedecinService(SQLiteDatabase sQLiteDatabase) {
        super(sQLiteDatabase);
    }


    public Medecin fetchById(String str) {
        Medecin medecin = new Medecin();
        Cursor rawQuery = super.rawQuery("SELECT * FROM "+DBConstants.MEDECIN_TABLE_NAME+" WHERE " + DBConstants.MEDECIN_ID + " = ?", new  String[]{str});
        while (rawQuery.moveToNext()) {
            medecin.setIdMed(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.MEDECIN_ID)));
            medecin.setNom(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.MEDECIN_TEXT_NOM)));
            medecin.setTaux(rawQuery.getInt(rawQuery.getColumnIndex(DBConstants.MEDECIN_TEXT_TAUX)));
        }
        return medecin;
    }

    public ArrayList<Medecin> fetchAll() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = super.rawQuery("SELECT * FROM "+DBConstants.MEDECIN_TABLE_NAME + " ORDER BY " + DBConstants.MEDECIN_TEXT_NOM + " ASC ",  null);
        while (rawQuery.moveToNext()) {
            Medecin medecin = new Medecin();
            medecin.setIdMed(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.MEDECIN_ID)));
            medecin.setNom(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.MEDECIN_TEXT_NOM)));
            medecin.setTaux(rawQuery.getInt(rawQuery.getColumnIndex(DBConstants.MEDECIN_TEXT_TAUX)));
            arrayList.add(medecin);
        }
        return arrayList;
    }

    public boolean add(Medecin medecin) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.MEDECIN_ID, medecin.getIdMed());
        contentValues.put(DBConstants.MEDECIN_TEXT_NOM, medecin.getNom());
        contentValues.put(DBConstants.MEDECIN_TEXT_TAUX, Integer.valueOf(medecin.getTaux()));
        super.insert(DBConstants.MEDECIN_TABLE_NAME, contentValues);
        return true;
    }

    public boolean deleteAll(String str) {
        super.delete(DBConstants.MEDECIN_TABLE_NAME, DBConstants.MEDECIN_ID + " = ?", new String[]{str});
        return true;
    }

    public boolean update(Medecin medecin) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.MEDECIN_TEXT_NOM, medecin.getNom());
        contentValues.put(DBConstants.MEDECIN_TEXT_TAUX, Integer.valueOf(medecin.getTaux()));
        super.update(DBConstants.MEDECIN_TABLE_NAME, contentValues, DBConstants.MEDECIN_ID+" = ?", new String[]{medecin.getIdMed()});
        return true;
    }
}
