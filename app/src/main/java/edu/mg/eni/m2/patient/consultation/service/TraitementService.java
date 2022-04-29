package edu.mg.eni.m2.patient.consultation.service;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

import edu.mg.eni.m2.patient.consultation.helpers.AbstractDao;
import edu.mg.eni.m2.patient.consultation.helpers.DBConstants;
import edu.mg.eni.m2.patient.consultation.model.Medecin;
import edu.mg.eni.m2.patient.consultation.model.Patient;
import edu.mg.eni.m2.patient.consultation.model.Traitement;

public class TraitementService extends AbstractDao implements IServiceBase<Traitement, Integer> {

    public TraitementService(SQLiteDatabase sQLiteDatabase) {
        super(sQLiteDatabase);
    }

    public Traitement fetchById(Integer id) {
        Traitement traitement = new Traitement();
        Cursor rawQuery = super.rawQuery("SELECT * FROM "+ DBConstants.TRAITEMENT_TABLE_NAME+" WHERE "+ DBConstants._ID+" = ?", new String[]{String.valueOf(id)});
        while (rawQuery.moveToNext()) {
            traitement.getMedecin().setIdMed(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.TRAITEMENT_TEXT_IDMED)));
            traitement.getPatient().setIdPat(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.TRAITEMENT_TEXT_IDPATIENT)));
            traitement.setNbHour(rawQuery.getInt(rawQuery.getColumnIndex(DBConstants.TRAITEMENT_TEXT_NBHOUR)));
        }
        return traitement;
    }

    public ArrayList<Traitement> fetchAll() {
        ArrayList arrayList = new ArrayList();
        Cursor rawQuery = super.rawQuery("SELECT * FROM "+DBConstants.TRAITEMENT_TABLE_NAME+" LEFT JOIN "+DBConstants.MEDECIN_TABLE_NAME+" AS med ON med.idMed = traitement.idMed LEFT JOIN patient ON patient.idPat = traitement.idPatient ORDER BY med.nomMed ASC",null);
        while (rawQuery.moveToNext()) {
            Traitement traitement = new Traitement();

            Medecin medecin = new Medecin();
            medecin.setIdMed(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.MEDECIN_ID)));
            medecin.setNom(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.MEDECIN_TEXT_NOM)));
            medecin.setTaux(rawQuery.getInt(rawQuery.getColumnIndex(DBConstants.MEDECIN_TEXT_TAUX)));

            Patient patient = new Patient();
            patient.setIdPat(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.PATIENT_ID)));
            patient.setNom(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.PATIENT_TEXT_NOM)));
            patient.setAdresse(rawQuery.getString(rawQuery.getColumnIndex(DBConstants.PATIENT_TEXT_ADRESSE)));

            traitement.setMedecin(medecin);
            traitement.setPatient(patient);
            traitement.setId(rawQuery.getInt(rawQuery.getColumnIndex(DBConstants._ID)));
            traitement.setNbHour(rawQuery.getInt(rawQuery.getColumnIndex(DBConstants.TRAITEMENT_TEXT_NBHOUR)));
            arrayList.add(traitement);
        }
        return arrayList;
    }

    public boolean add(Traitement traitement) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.TRAITEMENT_TEXT_IDMED, traitement.getMedecin().getIdMed());
        contentValues.put(DBConstants.TRAITEMENT_TEXT_IDPATIENT, traitement.getPatient().getIdPat());
        contentValues.put(DBConstants.TRAITEMENT_TEXT_NBHOUR, Integer.valueOf(traitement.getNbHour()));
        super.insert(DBConstants.TRAITEMENT_TABLE_NAME, contentValues);
        return true;
    }

    public boolean deleteAll(Integer id) {
        super.delete(DBConstants.TRAITEMENT_TABLE_NAME, DBConstants._ID + " = ?", new String[]{String.valueOf(id)});
        return true;
    }

    public boolean update(Traitement traitement) {
        String[] strArr = {String.valueOf(traitement.getId())};
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.TRAITEMENT_TEXT_IDMED, traitement.getMedecin().getIdMed());
        contentValues.put(DBConstants.TRAITEMENT_TEXT_IDPATIENT, traitement.getPatient().getIdPat());
        contentValues.put(DBConstants.TRAITEMENT_TEXT_NBHOUR, traitement.getNbHour());
        super.update(DBConstants.TRAITEMENT_TABLE_NAME, contentValues, DBConstants._ID + " = ?", strArr);
        return true;
    }
}
