package com.example.isgatuteur.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.isgatuteur.R;
import com.example.isgatuteur.models.User;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.UserViewModel;

public class InscriptionActivity extends AppCompatActivity {
    EditText champNom,champPrenom,champEmail,champNumTel,champMdp,champMdpConfirm;
    TextView tvConnect;
    Button btnInscription;
    UserViewModel userViewModel;
    TextView textViewAlertDialogMsg;
    AlertDialog.Builder alertDialog;
    ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        champNom = this.findViewById(R.id.inscriptionNom);
        champPrenom = this.findViewById(R.id.inscriptionPrenom);
        champEmail = this.findViewById(R.id.inscriptionEmail);
        champNumTel = this.findViewById(R.id.inscriptionTelephone);
        champMdp = this.findViewById(R.id.inscriptionMdp);
        champMdpConfirm = this.findViewById(R.id.inscriptionConfirmMdp);
        btnInscription = this.findViewById(R.id.btnInscrire);

        tvConnect=this.findViewById(R.id.tvConnect);
        userViewModel = new UserViewModel(new UserRepositoryImpl());

        tvConnect.setOnClickListener(view -> {
            Intent i1=new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i1);
            finish();
        });

        btnInscription.setOnClickListener(view -> {
            String nom = champNom.getText().toString().trim();
            String prenom = champPrenom.getText().toString().trim();
            String email = champEmail.getText().toString().trim();
            String numTel = champNumTel.getText().toString().trim();
            String password = champMdp.getText().toString().trim();
            String passwordConfirm = champMdpConfirm.getText().toString().trim();

            if(nom.isEmpty()){
                champNom.setError("Le champ Nom ne peut pas être vide");
            }
            else if(prenom.isEmpty()){
                champPrenom.setError("Le champ Prénom ne peut pas être vide");
            }
            else if(email.isEmpty()){
                champEmail.setError("Le champ email ne peut pas être vide");
            }
            else if(numTel.isEmpty()){
                champNumTel.setError("Le numéro de téléphone est obligatoire");
            }
            else if(password.isEmpty()){
                champMdp.setError("Le mot de passe ne peut pas être vide");
            }
            else if(!(passwordConfirm.equals(password))){
                champMdpConfirm.setError("Le mot de passe de confirmation ne correspond pas avec le mot de passe");
            }
            else{
                ProgressDialog progressDialog = new ProgressDialog(InscriptionActivity.this);
                progressDialog.setTitle("En cours de création...");
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog_item);
                //progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                User user = new User(nom,prenom,email,numTel,password,0,null);
                userViewModel.ajouterUser(user).observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if(aBoolean==true){
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }else {
                            progressDialog.dismiss();
                            afficherAlertDialog("","L'inscription a échoué , Verifiez que vous êtes connecté à internet !");
                            //Toast.makeText(InscriptionActivity.this, "L'inscription a échoué , Verifiez que vous êtes connecté à internet !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
}