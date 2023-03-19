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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.isgatuteur.AccueilActivity;
import com.example.isgatuteur.R;
import com.example.isgatuteur.models.Seance;
import com.example.isgatuteur.repositories.SeanceRepositoryImpl;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.SeanceViewModel;
import com.example.isgatuteur.viewModels.UserViewModel;

public class NoterSeance extends AppCompatActivity {

    EditText champNoteSeance;
    TextView infoTextView;
    Button btnNoterSeance;
    String seanceId;
    SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    UserViewModel userViewModel;
    SeanceViewModel seanceViewModel;
    TextView textViewAlertDialogMsg;
    AlertDialog.Builder alertDialog;
    ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noter_seance);
        getSupportActionBar().setTitle("Seance");

        preferences = getSharedPreferences(MY_PREFS,MODE_PRIVATE);
        userViewModel = new UserViewModel(new UserRepositoryImpl());
        seanceViewModel = new SeanceViewModel(new SeanceRepositoryImpl());

        champNoteSeance = this.findViewById(R.id.noteSeanceEditText);
        btnNoterSeance = this.findViewById(R.id.buttonNoter);
        infoTextView = this.findViewById(R.id.textViewExplication);

        Bundle parametres = getIntent().getExtras();
        seanceId = parametres.getString("IdSeance");
        //DocumentReference seanceRef = db.collection("seances").document(seanceId);

        seanceViewModel.getSeanceByDocId(seanceId).observe(this, new Observer<Seance>() {
            @Override
            public void onChanged(Seance seance1) {
                if(seance1.getNote()!=0){
                    champNoteSeance.setText(String.valueOf(seance1.getNote()));
                    infoTextView.setText("Vous avez déjà noté cette séance, Merci !");
                    champNoteSeance.setEnabled(false);
                    btnNoterSeance.setEnabled(false);
                }
                else {
                    btnNoterSeance.setOnClickListener(v -> {

                        if(champNoteSeance.getText().toString().isEmpty()){
                            champNoteSeance.setError("Veuillez inserer une note obligatoirement !");
                        }
                        else {
                            int note = Integer.parseInt(champNoteSeance.getText().toString().trim());
                            if (note<1 || note>5) {
                                champNoteSeance.setError("La note doit être entre 1 et 5");
                            }else{

                                seanceViewModel.updateNoteSeance(seanceId,note).observe(NoterSeance.this, new Observer<Boolean>() {
                                    @Override
                                    public void onChanged(Boolean aBoolean) {
                                        if(aBoolean){
                                            //afficherAlertDialog("","La note de la seance ajouté avec succès");
                                            Toast.makeText(getApplicationContext(), "Note seance ajouté avec succès", Toast.LENGTH_SHORT).show();
                                            champNoteSeance.setEnabled(false);
                                            btnNoterSeance.setEnabled(false);
                                            userViewModel.updateUserNote(seance1).observe(NoterSeance.this, new Observer<Boolean>() {
                                                @Override
                                                public void onChanged(Boolean aBoolean2) {
                                                    if(aBoolean2){
                                                        afficherAlertDialog("","La note de la seance a été ajouté avec succès et la note du tuteur mis à jour");
                                                        //Toast.makeText(getApplicationContext(), "Note seance ajouté et note user mis à jour avec succès", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {
                                                        afficherAlertDialog("","Note seance ajouté mais erreur pour note user");
                                                        //Toast.makeText(getApplicationContext(), "Note seance ajouté mais erreur pour note user", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                        else {
                                            afficherAlertDialog("","Un problème lors l'ajout de la note seance");
                                            //Toast.makeText(getApplicationContext(), "Un problème lors l'ajout de la note seance", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }

                    });
                }
            }
        });

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