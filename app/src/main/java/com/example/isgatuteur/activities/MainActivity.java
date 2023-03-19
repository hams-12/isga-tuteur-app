package com.example.isgatuteur.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.isgatuteur.AccueilActivity;
import com.example.isgatuteur.R;
import com.example.isgatuteur.databinding.ActivityMainBinding;
import com.example.isgatuteur.fragments.HomeFragment;
import com.example.isgatuteur.fragments.ProfilFragment;
import com.example.isgatuteur.fragments.PublierFragment;
import com.example.isgatuteur.fragments.SeancesFragment;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    public static final String KEY_CHOIX_PROFIL = "choixProfil";
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        userViewModel = new UserViewModel(new UserRepositoryImpl());
        preferences = getSharedPreferences(MY_PREFS,MODE_PRIVATE);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
        /*Toolbar toolbar = this.findViewById(R.id.myapptoolbar);*/
        //setSupportActionBar(toolbar);
        String valueChoixProfil = preferences.getString(KEY_CHOIX_PROFIL,null);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        if(valueChoixProfil.equals("etudiant")){
            binding.bottomNavigationView.getMenu().removeItem(R.id.publier);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homeEtudiant:
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_left);
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.profile:
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_left);
                    replaceFragment(new ProfilFragment());
                    break;
                /*case R.id.recherher:
                    replaceFragment(new RechercherFragment());
                    break;*/
                case R.id.publier:
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_left);
                    replaceFragment(new PublierFragment());
                    break;
                case R.id.seancesEtudiant:
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_left);
                    replaceFragment(new SeancesFragment());
                    break;
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
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
        SharedPreferences.Editor editor = preferences.edit();
        switch (item.getItemId()){
            case (R.id.itemDeconnexion):
                editor.clear();
                editor.commit();
                userViewModel.deconnecterUser();
                intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case (R.id.itemChoixRole):
                editor.clear();
                editor.commit();
                intent = new Intent(this, AccueilActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                return false;
        }
        return true;
    }

    /*public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }*/

}