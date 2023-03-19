package com.example.isgatuteur.singleton;

import com.google.firebase.firestore.FirebaseFirestore;

public class MyFirebaseSingleton {
    private static FirebaseFirestore instance;
    //private FirebaseFirestore db;

    private MyFirebaseSingleton(){
        //db = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestore getIntance(){
        if(instance == null){
            instance = FirebaseFirestore.getInstance();
        }
        return instance;
    }
}
