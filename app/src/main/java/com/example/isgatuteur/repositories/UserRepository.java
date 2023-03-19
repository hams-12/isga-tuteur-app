package com.example.isgatuteur.repositories;

import androidx.lifecycle.LiveData;

import com.example.isgatuteur.models.Seance;
import com.example.isgatuteur.models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public interface UserRepository {
    public LiveData<Boolean> ajouterUser(User u);
    public LiveData<Boolean> connecterUser(String email, String password);
    public LiveData<List<User>> getTuteursOfMatiere(String idMatiere);
    public LiveData<Integer> getCountTuteursOfMatiere(String idMatiere);
    public LiveData<DocumentReference> getMyCurrentUserRef();
    public LiveData<User> getMyCurrentUser();
    public FirebaseUser getUserConnecte();
    public void deconnecterUser();
    public void updateProfilePicture(String url);
    public LiveData<User> getUserByDocId(String userId);
    public LiveData<DocumentReference> getUserRefByDocId(String userId);
    public StorageReference getUserImageReference(String url);
    public LiveData<Boolean> updateUserNote(Seance seance);
    public LiveData<User> getUserByDocRef(DocumentReference userReference);

}
