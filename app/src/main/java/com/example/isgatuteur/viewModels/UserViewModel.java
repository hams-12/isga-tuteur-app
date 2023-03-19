package com.example.isgatuteur.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.isgatuteur.models.Seance;
import com.example.isgatuteur.models.User;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UserViewModel extends ViewModel {

    private final UserRepositoryImpl userRepository;

    public UserViewModel(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    /*public void ajouterUser(User user){
        userRepository.ajouterUser(user);
    }

    public void connecterUser(String email, String password){
        userRepository.connecterUser(email, password);
    }

    public void deconnecterUser(){
        userRepository.deconnecterUser();
    }*/

    public LiveData<Boolean> ajouterUser(User u){
        return userRepository.ajouterUser(u);
    }
    public LiveData<Boolean> connecterUser(String email, String password) {
        return userRepository.connecterUser(email,password);
    }

    public LiveData<List<User>> getTuteursOfMatiere(String idMatiere){
        return userRepository.getTuteursOfMatiere(idMatiere);
    }

    public LiveData<Integer> getCountTuteursOfMatiere(String idMatiere){
        return userRepository.getCountTuteursOfMatiere(idMatiere);
    }

    public LiveData<Boolean> updateUserNote(Seance seance){
        return userRepository.updateUserNote(seance);
    }

    public LiveData<User> getUserByDocRef(DocumentReference userReference){
        return userRepository.getUserByDocRef(userReference);
    }

    public void updateProfilePicture(String url) {
        userRepository.updateProfilePicture(url);
    }

    public StorageReference getUserImageReference(String url){
        return userRepository.getUserImageReference(url);
    }

    public FirebaseUser getUserConnecte(){
        return userRepository.getUserConnecte();
    }

    public void deconnecterUser(){
        userRepository.deconnecterUser();
    }

    public LiveData<User> getUserByDocId(String userId){
        return userRepository.getUserByDocId(userId);
    }

    public LiveData<User> getMyCurrentUser(){
        return userRepository.getMyCurrentUser();
    }

    public LiveData<DocumentReference> getMyCurrentUserRef(){
        return userRepository.getMyCurrentUserRef();
    }
}
