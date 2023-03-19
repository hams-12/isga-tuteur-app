package com.example.isgatuteur.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.isgatuteur.AccueilActivity;
import com.example.isgatuteur.R;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.UserViewModel;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText champEmail,champPass;
    TextView tvInscrire;
    Button btnLogin;
    UserViewModel userViewModel;
    TextView textViewAlertDialogMsg;
    AlertDialog.Builder alertDialog;
    ImageView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        champEmail = this.findViewById(R.id.champEmail);
        champPass = this.findViewById(R.id.champPass);
        btnLogin = this.findViewById(R.id.btnLogin);
        tvInscrire = this.findViewById(R.id.insc);

        userViewModel = new UserViewModel(new UserRepositoryImpl());

        tvInscrire.setOnClickListener(view -> {
            Intent i1=new Intent(LoginActivity.this, InscriptionActivity.class);
            startActivity(i1);
            finish();
        });

        btnLogin.setOnClickListener(view -> {
            String email = champEmail.getText().toString().trim();
            String password = champPass.getText().toString().trim();

            if(email.isEmpty()){
                champEmail.setError("Le champ email ne peut pas être vide");
            }
            else if(password.isEmpty()){
                champPass.setError("Le champ mot de passe ne peut pas être vide");
            }
            else {
                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("En cours de connexion...");
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog_item);
                //progressDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    userViewModel.connecterUser(email,password).observe(this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if(aBoolean==true){
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), AccueilActivity.class));
                                finish();
                            }
                            else {
                                progressDialog.dismiss();
                                afficherAlertDialog("","Authentification échouée , Verifiez que vous êtes connecté à internet et que vos identifiants sont corrects !");
                                //Toast.makeText(LoginActivity.this, "Authentification échouée , Verifiez que vous êtes connecté à internet !", Toast.LENGTH_SHORT).show();
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
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = userViewModel.getUserConnecte();
        if(currentUser != null){
        }
    }

}