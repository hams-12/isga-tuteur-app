package com.example.isgatuteur.repositories;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.isgatuteur.activities.LoginActivity;
import com.example.isgatuteur.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.atomic.AtomicReference;


public class UserRepositoryCopy {

    private Context context;
    private static final String TAG = "EmailPassword";
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;

    public UserRepositoryCopy(){
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public UserRepositoryCopy(Context context){
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void ajouterUser(User u) {
        mAuth.createUserWithEmailAndPassword(u.getEmail(), u.getPassword())
                .addOnCompleteListener(context.getMainExecutor(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            u.setId(mAuth.getCurrentUser().getUid());
                            // Sign in success, update UI with the signed-in user's information
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .set(u)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context.getApplicationContext(), "Utilisateur enregistré avec succès",
                                                Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Custom fields save failed
                                    });
                            Log.d(TAG, "createUserWithEmail:success");
                            Intent i = new Intent(context.getApplicationContext(), LoginActivity.class);
                            context.startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context.getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void supprimerUser(User u) {

    }

    /*@RequiresApi(api = Build.VERSION_CODES.P)
    public void connecterUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(context.getMainExecutor(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {

                                    })
                                    .addOnFailureListener(e -> {

                                    });
                            context.startActivity(new Intent(context.getApplicationContext(), AccueilActivity.class));
                            //finish();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Authentication échouée !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }*/


    public void deconnecterUser() {
        this.mAuth.signOut();
    }

    public FirebaseUser getUserConnecte() {
        return this.mAuth.getCurrentUser();
    }

    public DocumentReference getUserRefById(String id) {
        return db.collection("users").document(id);
    }

    public DocumentReference getUserRefByEmail(String email) {
        AtomicReference<DocumentReference> userRef = null;
        db.collection("users").whereEqualTo("email",email).limit(1)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        if(task1.getResult().size()!=0){
                            for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {
                                userRef.set(documentSnapshot.getReference());
                            }
                            //Toast.makeText(context.getApplicationContext(),"Séance Ajoutée avec succès",Toast.LENGTH_LONG).show();
                        }else {
                            //Toast.makeText(getContext(),"Aucun utilisateur ne correspond avec cet email",Toast.LENGTH_LONG).show();
                        }
                    }
                });
        return userRef.get();
    }

    public void updateProfilePicture(String url) {
        DocumentReference userRef = db.collection("users").document(this.mAuth.getCurrentUser().getUid());
        userRef.update("photo",url);
    }

    public StorageReference getUserImageReference(String url){
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        return httpsReference;
    }

    /*public LiveData<List<User>> getAllUsersLiveData() {
        MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
        db.collection("users")
                *//*.orderBy("note", Query.Direction.DESCENDING)*//*
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    List<User> usersList = new ArrayList<User>();
                    for (QueryDocumentSnapshot us : value) {
                        //DocumentReference documentReference = value.getDocuments();
                        User user = us.toObject(User.class);
                        usersList.add(user);
                    }
                    usersLiveData.setValue(usersList);
                });
        return usersLiveData;
    }*/

    /*public List<DocumentReference> getAllUsersRefOrderByNote() {
        myUserRefList = new ArrayList<>();
        db.collection("users")
                .orderBy("note", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    for (QueryDocumentSnapshot us : value) {
                        myUserRefList.add(us.getReference());
                    }
                });
        return myUserRefList;
    }*/


    /*public LiveData<List<DocumentReference>> getTuteursRefOfMatiere(String matiere){
        MutableLiveData<List<DocumentReference>> tuteursOfMatiereLiveData = new MutableLiveData<>();
        db.collection("matieres")
                .document(matiere)
                .addSnapshotListener((value, error) -> {
                    if(error != null){
                        Log.d("Mes Données --------", "Error getting documents: ", error);
                        return;
                    }
                    Matiere mat = value.toObject(Matiere.class);
                    Log.d("voilaaa",""+mat.getTuteursRef());
                    List<DocumentReference> tuteursRefList = new ArrayList<>();
                    if(mat.getTuteursRef()!=null){
                        tuteursRefList = mat.getTuteursRef();
                    }
                    tuteursOfMatiereLiveData.setValue(tuteursRefList);
                });

        return tuteursOfMatiereLiveData;
    }*/


    /////****///

}
