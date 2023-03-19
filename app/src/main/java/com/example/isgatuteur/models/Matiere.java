package com.example.isgatuteur.models;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Matiere {
    private String id;
    private String nom;
    private String domaine;
    List<DocumentReference> tuteursRef;

    public Matiere() {
    }

    public Matiere(String nom,String domaine,Long nbre_users) {
        this.nom = nom;
        this.domaine = domaine;
    }

    /*public Matiere(Map<String, Object> data) {
        //this.id = data.get("uuid").toString();
        this.nom = data.get("nom").toString();
        this.domaine = data.get("domaine").toString();
    }*/

    public List<DocumentReference> getTuteursRef() {
        return tuteursRef;
    }

    public void setTuteursRef(List<DocumentReference> tuteursRef) {
        this.tuteursRef = tuteursRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }
}
