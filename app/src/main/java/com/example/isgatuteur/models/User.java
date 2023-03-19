package com.example.isgatuteur.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class User implements Parcelable {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private float note;
    private String telephone;
    private String sexe;
    private String niveauEtude;
    private String specialite;
    private String password;
    private Boolean estTuteur=false;
    private String photo="";

    public User() {
    }

    public User(Parcel in) {
        this.id = in.readString();
        this.nom = in.readString();
        this.prenom = in.readString();
        this.email = in.readString();
        this.telephone = in.readString();
        this.note = in.readFloat();
        this.photo = in.readString();
    }

    public User(Map<String, Object> data) {
        try {
            this.id = data.get("id").toString();
        }catch (Exception e){
            this.id = "null";
        }
        this.nom = data.get("nom").toString();
        this.prenom = data.get("prenom").toString();
        this.email = data.get("email").toString();
        try {
            this.telephone = data.get("telephone").toString();
        }catch (Exception e){
            this.telephone = "Aucun numéro de telephone";
        }
        try {
            this.note = Float.parseFloat(data.get("note").toString());
        }catch (Exception e){
            this.note = 0;
        }
        this.password = data.get("password").toString();
        this.photo = data.get("photo").toString();
        //this.estTuteur = Boolean.parseBoolean(data.get("estTuteur").toString());
    }

    public User(String nom, String prenom, String email,String telephone, String password, float note, String photo) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.note = note;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getNote() {
        return note;
    }

    public void setNote(float note) {
        this.note = note;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    public static Creator<User> getCreator(){
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(nom);
        parcel.writeString(prenom);
        parcel.writeString(email);
        parcel.writeString(telephone);
        parcel.writeFloat(note);
        parcel.writeString(photo);
    }
}
