package com.example.isgatuteur.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.isgatuteur.models.Matiere;
import com.example.isgatuteur.singleton.MyFirebaseSingleton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MatiereRepositoryImpl implements MatiereRepository{
    private final FirebaseFirestore db;
    UserRepositoryImpl userRepository;
    boolean isUpdated;

    public MatiereRepositoryImpl() {
        this.db = MyFirebaseSingleton.getIntance();
        userRepository = new UserRepositoryImpl();
    }


    @Override
    public LiveData<List<Matiere>> getAllMatiereLiveData() {
        MutableLiveData<List<Matiere>> matieresLiveData = new MutableLiveData<>();
        db.collection("matieres")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    List<Matiere> matiereList = new ArrayList<Matiere>();
                    for (QueryDocumentSnapshot matie : value) {
                        Matiere mt = matie.toObject(Matiere.class);
                        mt.setId(matie.getId());
                        matiereList.add(mt);
                    }
                    matieresLiveData.setValue(matiereList);
                });
        return matieresLiveData;
    }

    @Override
    public LiveData<List<String>> getAllDomaineLiveData() {
        MutableLiveData<List<String>> domainesLiveData = new MutableLiveData<>();
        db.collection("matieres")
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    List<String> domaines = new ArrayList<String>();
                    domaines.add("Tous les domaines");
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        String dom = documentSnapshot.get("domaine").toString();
                        if(dom!= null && !(domaines.contains(dom))){
                            domaines.add(dom);
                        }
                    }
                    domainesLiveData.setValue(domaines);
                });
        return domainesLiveData;
    }

    @Override
    public LiveData<List<Matiere>> getMatiereByDomaineLiveData(String domaine) {
        MutableLiveData<List<Matiere>> matieresByDomaineLiveData = new MutableLiveData<>();
        db.collection("matieres")
                .whereEqualTo("domaine", domaine)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    List<Matiere> matiereList = new ArrayList<>();
                    for (QueryDocumentSnapshot matiere : value) {

                        Matiere mt = matiere.toObject(Matiere.class);
                        mt.setId(matiere.getId());
                        matiereList.add(mt);
                    }
                    matieresByDomaineLiveData.setValue(matiereList);
                });
        return matieresByDomaineLiveData;
    }

    public LiveData<Boolean> checkIfUserExistInMatiere(String matiereId, String userId) {
        MutableLiveData<Boolean> checkIfUserExistInMatiereLiveData = new MutableLiveData<>();

        db.collection("matieres")
                .document(matiereId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        List<DocumentReference> tuteursRefList = (List<DocumentReference>) documentSnapshot.get("tuteursRef");
                        DocumentReference curTuteurRef = userRepository.getUserRefByDocIdWithoutObserble(userId);
                        if(curTuteurRef!=null){
                            if(tuteursRefList.contains(curTuteurRef)){
                                checkIfUserExistInMatiereLiveData.setValue(true);
                            }
                            else {
                                checkIfUserExistInMatiereLiveData.setValue(false);
                            }
                        }
                    }
        });

        db.collection("matieres")
                .document(matiereId)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    List<DocumentReference> tuteursRefList = (List<DocumentReference>) value.get("tuteursRef");
                    DocumentReference curTuteurRef = userRepository.getUserRefByDocIdWithoutObserble(userId);
                    //DocumentReference curTuteurRef = db.collection("users").document(userId);
                    if(curTuteurRef!=null){
                        if(tuteursRefList.contains(curTuteurRef)){
                            checkIfUserExistInMatiereLiveData.setValue(true);
                        }
                        else {
                            checkIfUserExistInMatiereLiveData.setValue(false);
                        }
                    }

                });

        return checkIfUserExistInMatiereLiveData;
    }

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

}
