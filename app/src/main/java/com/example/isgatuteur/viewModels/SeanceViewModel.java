package com.example.isgatuteur.viewModels;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.isgatuteur.models.Seance;
import com.example.isgatuteur.repositories.SeanceRepositoryImpl;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;


public class SeanceViewModel extends ViewModel {

    private final SeanceRepositoryImpl seanceRepository;
    public SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    public static final String KEY_CHOIX_PROFIL = "choixProfil";
    public String valueChoixProfil;

    public SeanceViewModel(SeanceRepositoryImpl seanceRepository) {
        this.seanceRepository = seanceRepository;
    }

    public LiveData<List<Seance>> getSeanceByUserRefLiveData(String userRefField, DocumentReference connectedUserRef){
        return seanceRepository.getSeanceByUserRefLiveData(userRefField,connectedUserRef);
    }

    public LiveData<List<String>> getAllEtatLiveData(){
        return seanceRepository.getAllEtatLiveData();
    }

    public LiveData<List<Seance>> getSeanceByEtatLiveData(String userRefField, DocumentReference connectedUserRef,String etat){
        return seanceRepository.getSeanceByEtatLiveData(userRefField,connectedUserRef,etat);
    }

    public LiveData<List<Seance>> getSeanceTerminePasNote(String userRefField, DocumentReference connectedUserRef){
        return seanceRepository.getSeanceTerminePasNote(userRefField,connectedUserRef);
    }

    public LiveData<Seance> getSeanceByDocId(String seanceId){
        return seanceRepository.getSeanceByDocId(seanceId);
    }

    public LiveData<Boolean> updateNoteSeance(String seanceId, int note){
        return seanceRepository.updateNoteSeance(seanceId,note);
    }

    /*public LiveData<Boolean> updateSeanceByFieldName(String fieldName, String seanceId, String valeur){
        return seanceRepository.updateSeanceByFieldName(fieldName,seanceId,valeur);
    }*/

    public void updateSeanceByFieldName(String fieldName, String seanceId, String valeur){
        seanceRepository.updateSeanceByFieldName(fieldName,seanceId,valeur);
    }

}
