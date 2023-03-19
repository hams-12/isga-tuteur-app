package com.example.isgatuteur.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class Seance {
    //private String titre;
    //private String duree;
    private String Id;
    private Date date;
    private float note;
    private String etat;
    //private User user;
    private DocumentReference tuteurRef;
    private DocumentReference etudiantRef;
    public Seance(){}

    /*public Seance(Map<String, Object> data) {
        this.etat = data.get("etat").toString();
        this.note = Float.parseFloat(data.get("note").toString());
        //this.date = data.get("date").toString();
    }*/

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getNote() {
        return note;
    }

    public void setNote(float note) {
        this.note = note;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public DocumentReference getTuteurRef() {
        return tuteurRef;
    }

    public void setTuteurRef(DocumentReference tuteurRef) {
        this.tuteurRef = tuteurRef;
    }

    public DocumentReference getEtudiantRef() {
        return etudiantRef;
    }

    public void setEtudiantRef(DocumentReference etudiantRef) {
        this.etudiantRef = etudiantRef;
    }

    /*public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }*/
}
