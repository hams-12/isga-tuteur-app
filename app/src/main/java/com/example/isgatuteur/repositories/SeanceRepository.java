package com.example.isgatuteur.repositories;

import androidx.lifecycle.LiveData;

import com.example.isgatuteur.models.Seance;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public interface SeanceRepository {
    public LiveData<List<Seance>> getSeanceByUserRefLiveData(String userRefField, DocumentReference connectedUserRef);
    public LiveData<List<String>> getAllEtatLiveData();
    public LiveData<List<Seance>> getSeanceByEtatLiveData(String userRefField, DocumentReference connectedUserRef,String etat);
    public LiveData<List<Seance>> getSeanceTerminePasNote(String userRefField, DocumentReference connectedUserRef);
    public LiveData<Boolean> updateNoteSeance(String seanceId, int note);
    public LiveData<Seance> getSeanceByDocId(String seanceId);
    public void updateSeanceByFieldName(String fieldName, String seanceId, String valeur);
}
