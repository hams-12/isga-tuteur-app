package com.example.isgatuteur.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.isgatuteur.AccueilActivity;
import com.example.isgatuteur.R;
import com.example.isgatuteur.adapters.UserAdapter;
import com.example.isgatuteur.models.User;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.singleton.MyFirebaseAuthSingetion;
import com.example.isgatuteur.viewModels.UserViewModel;

import java.util.List;

public class ListTuteurActivity extends AppCompatActivity {
    ListView listViewUser;
    SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    UserAdapter userAdapter;
    UserViewModel userViewModel;
    TextView textViewAlertDialogMsg;
    AlertDialog.Builder alertDialog;
    ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tuteur);
        preferences = getSharedPreferences(MY_PREFS,MODE_PRIVATE);
        userViewModel = new UserViewModel(new UserRepositoryImpl());
        //getSupportActionBar().setTitle("Tuteurs");
        /*androidx.appcompat.widget.Toolbar toolbar2 = findViewById(R.id.myapptoolbar);
        if(toolbar2==null){
            Toast.makeText(this, "nullllllll", Toast.LENGTH_SHORT).show();
        }
        setSupportActionBar(toolbar2);*/
        //toolbar2.setTitle("Mon titre");
        


        listViewUser = this.findViewById(R.id.listViewTuteur);
        //List<User> listeusersfb = new ArrayList<User>();

        Bundle parametres = getIntent().getExtras();
        String idMatiere = parametres.getString("idMatiere");

        //Récupération des utilisateurs (tuteurs/etudiants de la matière)
        //FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*LiveData<List<DocumentReference>> tuteursLiveData = userRepository.getTuteursRefOfMatiere(idMatiere);
        tuteursLiveData.observe(this,tuteurs -> {
            Toast.makeText(this, ""+tuteurs, Toast.LENGTH_SHORT).show();
        });*/

        userViewModel.getTuteursOfMatiere(idMatiere).observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> tuteurs) {
                showUserInListVeiw(tuteurs);
                /*if(tuteurs.isEmpty()){
                    afficherAlertDialog("","Aucun tuteur disponible pour cette matière");
                    showUserInListVeiw(tuteurs);
                }else{
                    showUserInListVeiw(tuteurs);
                }*/
            }
        });
        /*tuteursLiveData.observe(this,tuteurs -> {
            Log.d("blalalsfseo****","taille"+tuteurs.size());
            if(tuteurs.isEmpty()){
                Log.d("jesuis","je suis ici oh liste vide");
                Toast.makeText(this, "Aucun tuteur disponible pour cette matière", Toast.LENGTH_SHORT).show();
                showUserInListVeiw(tuteurs);
            }else{
                showUserInListVeiw(tuteurs);
            }
        });*/


        /*db.collection("matieres")
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
                                        if(refTuteurs.size() != 0){
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                DocumentReference docRef = document.getReference();
                                                if(refTuteurs.contains(docRef)){
                                                    User us = document.toObject(User.class);
                                                    listeusersfb.add(us);
                                                }
                                                Log.d("Mes Données --------", document.getId() + " => " + document.getData());
                                            }
                                            showUserInListVeiw(listeusersfb);
                                            *//*listViewUser.setAdapter(new UserAdapter(listeusersfb,ListTuteurActivity.this));
                                            listViewUser.setOnItemClickListener(((adapterView, view1, i, l) -> {
                                                User user = (User) listViewUser.getAdapter().getItem(i);
                                                Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                                                intent.putExtra("us",user);
                                                //intent.putExtra("idMatiere",matiere.getId()); on peut faire parcelable pour ne pas refaire la requete
                                                startActivity(intent);
                                            }));*//*
                                        }
                                        else {
                                            Toast.makeText(ListTuteurActivity.this, "Aucun tuteur disponible pour cette matière", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Log.d("Mes Données --------", "Error getting documents: ", task.getException());
                                    }
                                }
                            });

                });*/

        /*********************************/
        /*db.collection("matieres")
                .document(idMatiere)
                .collection("users")
                .whereEqualTo("estTuteur", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User us = new User(document.getData());
                                us.setId(document.getId());
                                listeusersfb.add(us);
                                Log.d("Mes Users --------", document.getId() + " => " + document.getData());
                            }
                            listViewUser.setAdapter(new UserAdapter(listeusersfb,ListTuteurActivity.this));
                            listViewUser.setOnItemClickListener(((adapterView, view1, i, l) -> {
                                User user = (User) listViewUser.getAdapter().getItem(i);
                                Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                                intent.putExtra("us",user);
                                //intent.putExtra("idMatiere",matiere.getId()); on peut faire parcelable pour ne pas refaire la requete
                                startActivity(intent);
                            }));
                        } else {
                            Log.d("Mon erreur --------", "Error getting documents: ", task.getException());
                        }
                    }
                });*/
    }

    public void afficherAlertDialog(String titre, String message){
        alertDialog = new AlertDialog.Builder(this);
        View altertePersonalise = LayoutInflater.from(this).inflate(R.layout.altert_dialog_layout,null);
        cancel = (ImageView) altertePersonalise.findViewById(R.id.imageViewCancel);
        alertDialog.setView(altertePersonalise);
        AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        textViewAlertDialogMsg = altertePersonalise.findViewById(R.id.textViewAlertDialogMsg);
        textViewAlertDialogMsg.setText(message);
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showUserInListVeiw(List<User> userslistv){
        userAdapter = new UserAdapter(userslistv,getApplicationContext());
        listViewUser.setAdapter(userAdapter);
        listViewUser.setOnItemClickListener(((adapterView, view1, i, l) -> {
            User user = (User) listViewUser.getAdapter().getItem(i);
            if(user.getId().equals(MyFirebaseAuthSingetion.getInstance().getCurrentUser().getUid())){
                afficherAlertDialog("","Vous ne pouvez pas vous contacter vous même !");
            }
            else{
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("us",user);
                startActivity(intent);
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_projet,menu);
        //return true;
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
                intent = new Intent(this, LoginActivity.class);
                finishAffinity();
                startActivity(intent);
                //finish();
                break;
            case (R.id.itemChoixRole):
                editor.clear();
                editor.commit();
                intent = new Intent(this, AccueilActivity.class);
                finishAffinity();
                startActivity(intent);
                //finish();
                break;
            //return true;
            default:
                return false;
        }
        //startActivity(intent);
        return true;
    }
}