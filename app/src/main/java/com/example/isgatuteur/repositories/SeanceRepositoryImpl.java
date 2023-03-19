package com.example.isgatuteur.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.isgatuteur.models.Seance;
import com.example.isgatuteur.singleton.MyFirebaseSingleton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeanceRepositoryImpl implements SeanceRepository {

    private final FirebaseFirestore db;

    public SeanceRepositoryImpl() {
        this.db = MyFirebaseSingleton.getIntance();
    }

    @Override
    public LiveData<List<Seance>> getSeanceByUserRefLiveData(String userRefField, DocumentReference connectedUserRef){
        MutableLiveData<List<Seance>> seancesLiveData = new MutableLiveData<>();
        db.collection("seances")
                .whereEqualTo(userRefField, connectedUserRef)
                .orderBy("date")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    List<Seance> seanceList = new ArrayList<Seance>();
                    for (QueryDocumentSnapshot sea : value) {

                        Seance seance = sea.toObject(Seance.class); //new Seance(sea.getData());
                        DocumentReference tuteurRef = (DocumentReference) sea.getData().get("tuteurRef");
                        DocumentReference etudiantRef = (DocumentReference) sea.getData().get("etudiantRef");
                        Timestamp timestamp = (Timestamp) sea.getData().get("date");
                        Date date = timestamp.toDate();
                        seance.setDate(date);
                        seance.setTuteurRef(tuteurRef);
                        seance.setEtudiantRef(etudiantRef);
                        /////****************************************///
                        //seance.setId(sea.getId());
                        seanceList.add(seance);
                    }
                    seancesLiveData.setValue(seanceList);
                });
        return seancesLiveData;
    }

    @Override
    public LiveData<List<String>> getAllEtatLiveData(){
        MutableLiveData<List<String>> etatsLiveData = new MutableLiveData<>();
        db.collection("seances")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    List<String> etats = new ArrayList<String>();
                    etats.add("Toutes les séances");
                    etats.add("Terminée pas encore notée");
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        String myEtat = documentSnapshot.get("etat").toString();
                        if(myEtat!= null && !(etats.contains(myEtat))){
                            etats.add(myEtat);
                        }
                    }
                    Log.d("esfs*******",""+etats);
                    etatsLiveData.setValue(etats);
                });
        return etatsLiveData;
    }

    @Override
    public LiveData<List<Seance>> getSeanceByEtatLiveData(String userRefField, DocumentReference connectedUserRef,String etat){
        MutableLiveData<List<Seance>> seanceByEtatLiveData = new MutableLiveData<>();
        db.collection("seances")
                .whereEqualTo(userRefField, connectedUserRef)
                .whereEqualTo("etat", etat)
                .orderBy("date")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    List<Seance> seanceList = new ArrayList<Seance>();
                    for (QueryDocumentSnapshot sea : value) {

                        Seance seance = sea.toObject(Seance.class); //new Seance(sea.getData());
                        DocumentReference tuteurRef = (DocumentReference) sea.getData().get("tuteurRef");
                        DocumentReference etudiantRef = (DocumentReference) sea.getData().get("etudiantRef");
                        Timestamp timestamp = (Timestamp) sea.getData().get("date");
                        Date date = timestamp.toDate();
                        seance.setDate(date);
                        seance.setTuteurRef(tuteurRef);
                        seance.setEtudiantRef(etudiantRef);
                        //seance.setId(sea.getId());
                        seanceList.add(seance);
                    }
                    seanceByEtatLiveData.setValue(seanceList);
                });
        return seanceByEtatLiveData;
    }

    @Override
    public LiveData<List<Seance>> getSeanceTerminePasNote(String userRefField, DocumentReference connectedUserRef){
        MutableLiveData<List<Seance>> seanceByEtatLiveData = new MutableLiveData<>();
        db.collection("seances")
                .whereEqualTo(userRefField, connectedUserRef)
                .whereEqualTo("etat", "terminé")
                .whereEqualTo("note",0)
                .orderBy("date")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    List<Seance> seanceList = new ArrayList<Seance>();
                    for (QueryDocumentSnapshot sea : value) {

                        Seance seance = sea.toObject(Seance.class); //new Seance(sea.getData());
                        DocumentReference tuteurRef = (DocumentReference) sea.getData().get("tuteurRef");
                        DocumentReference etudiantRef = (DocumentReference) sea.getData().get("etudiantRef");
                        Timestamp timestamp = (Timestamp) sea.getData().get("date");
                        Date date = timestamp.toDate();
                        seance.setDate(date);
                        seance.setTuteurRef(tuteurRef);
                        seance.setEtudiantRef(etudiantRef);
                        //seance.setId(sea.getId());
                        seanceList.add(seance);
                    }
                    seanceByEtatLiveData.setValue(seanceList);
                });
        return seanceByEtatLiveData;
    }

    @Override
    public LiveData<Seance> getSeanceByDocId(String seanceId){
        MutableLiveData<Seance> oneSeanceLiveData = new MutableLiveData<>();
        db.collection("seances")
                .document(seanceId)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    Seance seance = value.toObject(Seance.class);
                    oneSeanceLiveData.setValue(seance);
                });

        return oneSeanceLiveData;
    }

    @Override
    public LiveData<Boolean> updateNoteSeance(String seanceId, int note) {
        MutableLiveData<Boolean> isUpdatedLiveData = new MutableLiveData<>();

        db.collection("seances")
                .document(seanceId)
                .update("note",note)
                .addOnSuccessListener(aVoid -> {
                    isUpdatedLiveData.setValue(true);
                })
                .addOnFailureListener(e -> {
                    isUpdatedLiveData.setValue(false);
                });

        return isUpdatedLiveData;
    }

    /*public LiveData<Boolean> updateSeanceByFieldName(String fieldName, String seanceId, String valeur) {
        MutableLiveData<Boolean> isUpdatedLiveData = new MutableLiveData<>();

        db.collection("seances")
                .document(seanceId)
                .update(fieldName,valeur)
                .addOnSuccessListener(aVoid -> {
                    isUpdatedLiveData.setValue(true);
                })
                .addOnFailureListener(e -> {
                    isUpdatedLiveData.setValue(false);
                });

        return isUpdatedLiveData;
    }*/

    @Override
    public void updateSeanceByFieldName(String fieldName, String seanceId, String valeur) {
        db.collection("seances")
                .document(seanceId)
                .update(fieldName,valeur);
    }

}

