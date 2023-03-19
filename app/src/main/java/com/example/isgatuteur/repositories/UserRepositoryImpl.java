package com.example.isgatuteur.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.isgatuteur.models.Matiere;
import com.example.isgatuteur.models.Seance;
import com.example.isgatuteur.models.User;
import com.example.isgatuteur.singleton.MyFirebaseAuthSingetion;
import com.example.isgatuteur.singleton.MyFirebaseSingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    DocumentReference ref = null;
    List<DocumentReference> refTuteurs;

    public UserRepositoryImpl(){
        this.db = MyFirebaseSingleton.getIntance();
        this.mAuth = MyFirebaseAuthSingetion.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    @Override
    public LiveData<Boolean> ajouterUser(User u) {
        MutableLiveData<Boolean> isCreatedLiveData = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(u.getEmail(), u.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            u.setId(mAuth.getCurrentUser().getUid());
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .set(u)
                                    .addOnSuccessListener(aVoid -> {
                                        isCreatedLiveData.setValue(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        isCreatedLiveData.setValue(false);
                                    });
                        } else {
                            isCreatedLiveData.setValue(false);
                        }
                    }
                });
        return isCreatedLiveData;
    }

    @Override
    public LiveData<Boolean> connecterUser(String email, String password) {
        MutableLiveData<Boolean> isConnectedLiveData = new MutableLiveData<>();
        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        isConnectedLiveData.setValue(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        isConnectedLiveData.setValue(false);
                                        mAuth.signOut();
                                    });
                        } else {
                            isConnectedLiveData.setValue(false);
                        }
                    }
                });
        return isConnectedLiveData;
    }

    @Override
    public LiveData<List<User>> getTuteursOfMatiere(String idMatiere){
        MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
        db.collection("matieres")
                .document(idMatiere)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Matiere matiere = documentSnapshot.toObject(Matiere.class);

                    if(matiere.getTuteursRef() == null) {
                        refTuteurs = new ArrayList<DocumentReference>();
                    }else {
                        refTuteurs = matiere.getTuteursRef();
                    }

                    db.collection("users")
                            .orderBy("note", Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<User> usersList = new ArrayList<User>();
                                        if(refTuteurs.size() != 0){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                DocumentReference docRef = document.getReference();
                                                if(refTuteurs.contains(docRef)){
                                                    User us = document.toObject(User.class);
                                                    usersList.add(us);
                                                }
                                                Log.d("Mes Données --------", document.getId() + " => " + document.getData());
                                            }
                                        }
                                        usersLiveData.setValue(usersList);
                                    } else {
                                        Log.d("Mes Données --------", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                });
        return usersLiveData;
    }

    @Override
    public LiveData<Integer> getCountTuteursOfMatiere(String idMatiere){
        MutableLiveData<Integer> usersCountLiveData = new MutableLiveData<>();
        db.collection("matieres")
                .document(idMatiere)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Matiere matiere = documentSnapshot.toObject(Matiere.class);

                    if(matiere.getTuteursRef() == null) {
                        refTuteurs = new ArrayList<DocumentReference>();
                    }else {
                        refTuteurs = matiere.getTuteursRef();
                    }

                    db.collection("users")
                            .orderBy("note", Query.Direction.DESCENDING)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int compteur = 0;
                                        if(refTuteurs.size() != 0){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                DocumentReference docRef = document.getReference();
                                                if(refTuteurs.contains(docRef)){
                                                    compteur++;
                                                }
                                                Log.d("Mes Données --------", document.getId() + " => " + document.getData());
                                            }
                                        }
                                        usersCountLiveData.setValue(compteur);
                                    } else {
                                        Log.d("Mes Données --------", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                });
        return usersCountLiveData;
    }

    @Override
    public LiveData<DocumentReference> getMyCurrentUserRef(){
        MutableLiveData<DocumentReference> oneUserRefLiveData = new MutableLiveData<>();
        db.collection("users")
                .document(this.mAuth.getUid())
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    DocumentReference userRef = value.getReference();
                    oneUserRefLiveData.setValue(userRef);
                });

        return oneUserRefLiveData;
    }

    @Override
    public FirebaseUser getUserConnecte() {
        return this.mAuth.getCurrentUser();
    }


    @Override
    public LiveData<User> getMyCurrentUser(){
        MutableLiveData<User> oneUserLiveData = new MutableLiveData<>();
        db.collection("users")
                .document(this.mAuth.getUid())
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    User user = value.toObject(User.class);
                    oneUserLiveData.setValue(user);
                });

        return oneUserLiveData;
    }

    @Override
    public LiveData<User> getUserByDocId(String userId){
        MutableLiveData<User> oneUserLiveData = new MutableLiveData<>();
        db.collection("users")
                .document(userId)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    User user = value.toObject(User.class);
                    oneUserLiveData.setValue(user);
                });

        return oneUserLiveData;
    }

    @Override
    public LiveData<User> getUserByDocRef(DocumentReference userReference){
        MutableLiveData<User> oneUserLiveData = new MutableLiveData<>();
        userReference
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    User user = value.toObject(User.class);
                    oneUserLiveData.setValue(user);
                });

        return oneUserLiveData;
    }

    @Override
    public LiveData<DocumentReference> getUserRefByDocId(String userId){
        MutableLiveData<DocumentReference> oneUserRefLiveData = new MutableLiveData<>();
        db.collection("users")
                .document(userId)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    DocumentReference userRef = value.getReference();
                    oneUserRefLiveData.setValue(userRef);
                });

        return oneUserRefLiveData;
    }

    public DocumentReference getUserRefByDocIdWithoutObserble(String userId){
        return db.collection("users").document(userId);
    }

    @Override
    public void updateProfilePicture(String url) {
        DocumentReference userRef = db.collection("users").document(this.mAuth.getCurrentUser().getUid());
        userRef.update("photo",url);
    }

    @Override
    public StorageReference getUserImageReference(String url){
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        return httpsReference;
    }

    @Override
    public void deconnecterUser() {
        this.mAuth.signOut();
    }

    @Override
    public LiveData<Boolean> updateUserNote(Seance seance){
        MutableLiveData<Boolean> updateNoteLiveData = new MutableLiveData<>();
        db.collection("seances")
                .whereEqualTo("tuteurRef",seance.getTuteurRef())
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    float noteTuteur=0;
                    int total=0;
                    if(value==null || value.isEmpty()){
                        total=1; //pour éviter une division par 0
                    }
                    for (QueryDocumentSnapshot sea : value) {
                        Seance seance1 = sea.toObject(Seance.class);
                        //la séance peut être terminé mais pas encore noté c'est pour cela qu'on a seance1.getNote()!=0 car une seance noté est différent de 0
                        if(seance1.getNote()!=0 && seance1.getEtat().equals("terminé")){
                            noteTuteur += seance1.getNote();
                            total++;
                        }
                    }
                    noteTuteur = noteTuteur/total;
                    seance.getTuteurRef().update("note",noteTuteur)
                            .addOnSuccessListener(aVoid -> {
                                updateNoteLiveData.setValue(true);
                            })
                            .addOnFailureListener(e -> {
                                updateNoteLiveData.setValue(false);
                            });
                });
        return updateNoteLiveData;
    }

}
