package com.example.isgatuteur.singleton;

import com.google.firebase.auth.FirebaseAuth;

public class MyFirebaseAuthSingetion {
    public static FirebaseAuth instance;

    private MyFirebaseAuthSingetion(){}

    public static FirebaseAuth getInstance() {
        if(instance==null){
            instance = FirebaseAuth.getInstance();
        }
        return instance;
    }
}
