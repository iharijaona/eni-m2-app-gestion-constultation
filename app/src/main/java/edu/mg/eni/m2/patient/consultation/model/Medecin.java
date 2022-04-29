package edu.mg.eni.m2.patient.consultation.model;

public class Medecin {
    private String idMed;
    private String nom;
    private int taux;

    public String getIdMed() {
        return this.idMed;
    }

    public void setIdMed(String str) {
        this.idMed = str;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String str) {
        this.nom = str;
    }

    public int getTaux() {
        return this.taux;
    }

    public void setTaux(int i) {
        this.taux = i;
    }
}

