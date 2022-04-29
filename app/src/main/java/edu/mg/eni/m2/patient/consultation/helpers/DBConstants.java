package edu.mg.eni.m2.patient.consultation.helpers;

public class DBConstants {
    public static final String MEDECIN_ID = "idMed";
    public static final String MEDECIN_TABLE_NAME = "medecin";
    public static final String MEDECIN_TEXT_NOM = "nomMed";
    public static final String MEDECIN_TEXT_TAUX = "tauxMed";
    public static final String PATIENT_ID = "idPat";
    public static final String PATIENT_TABLE_NAME = "patient";
    public static final String PATIENT_TEXT_ADRESSE = "adressePat";
    public static final String PATIENT_TEXT_NOM = "nomPat";
    public static final String TRAITEMENT_TABLE_NAME = "traitement";
    public static final String TRAITEMENT_TEXT_IDMED = "idMed";
    public static final String TRAITEMENT_TEXT_IDPATIENT = "idPatient";
    public static final String TRAITEMENT_TEXT_NBHOUR = "nbHour";
    public static final String _ID = "id";
    public static final String sqlMedecin = "CREATE TABLE medecin ( idMed TEXT PRIMARY KEY, nomMed TEXT , tauxMed INT )";
    public static final String sqlPatient = "CREATE TABLE patient ( idPat TEXT PRIMARY KEY, nomPat TEXT , adressePat TEXT )";
    public static final String sqlTraitement = "CREATE TABLE traitement (id INTEGER PRIMARY KEY   AUTOINCREMENT, idMed TEXT NOT NULL, idPatient TEXT NOT NULL, nbHour INTEGER, FOREIGN KEY (idMed) REFERENCES medecin(idMed), FOREIGN KEY (idPatient) REFERENCES patient(idPat));";
}
