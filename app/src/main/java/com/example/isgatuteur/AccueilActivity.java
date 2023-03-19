package com.example.isgatuteur;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.isgatuteur.activities.LoginActivity;
import com.example.isgatuteur.activities.MainActivity;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.UserViewModel;

public class AccueilActivity extends AppCompatActivity {

    Button btnTutor,btnStudent;
    //public String choixProfil;
    public SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    public static final String KEY_CHOIX_PROFIL = "choixProfil";
    /*public static final String KEY_CONNECTED_USER_EMAIL = "emailProfil";
    public static final String KEY_CONNECTED_USER_ID = "idProfil";
    public static final String KEY_CONNECTED_USER_NOM = "nomProfil";
    public static final String KEY_CONNECTED_USER_PRENOM = "prenomProfil";*/
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        userViewModel = new UserViewModel(new UserRepositoryImpl());

        if(userViewModel.getUserConnecte()==null){
            startActivity(new Intent(AccueilActivity.this, LoginActivity.class));
            finish();
        }


        btnStudent = this.findViewById(R.id.btnStudent);
        btnTutor = this.findViewById(R.id.btnTutor);

        preferences = getSharedPreferences(MY_PREFS,MODE_PRIVATE);

        //On verifie si la préférence existe déjà
        String checkValueChoixProfil = preferences.getString(KEY_CHOIX_PROFIL,null);
        if(checkValueChoixProfil != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }

        btnTutor.setOnClickListener(v -> {
            String choix = "tuteur";
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_CHOIX_PROFIL,choix);
            editor.apply();
            Log.d("MyTag+++",""+preferences.getString(KEY_CHOIX_PROFIL,null));
            startActivity(i);
            finish();
        });

        btnStudent.setOnClickListener(v -> {
            String choix = "etudiant";
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_CHOIX_PROFIL,choix);
            editor.apply();
            startActivity(i);
            finish();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_projet,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()){
            case (R.id.itemDeconnexion):
                userViewModel.deconnecterUser();
                intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                return false;
        }
        return true;
    }

}