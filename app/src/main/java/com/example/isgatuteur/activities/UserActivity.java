package com.example.isgatuteur.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.isgatuteur.AccueilActivity;
import com.example.isgatuteur.R;
import com.example.isgatuteur.models.User;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.UserViewModel;

import java.text.DecimalFormat;

public class UserActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    TextView mNameTv, mDescTv;
    ImageView mImageTv;
    RatingBar mRatingTv;
    Button phone, sms;
    User user;
    TextView textViewNote2;
    SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        preferences = getSharedPreferences(MY_PREFS,MODE_PRIVATE);
        getSupportActionBar().setTitle("Profil du tuteur");
        user = getIntent().getParcelableExtra("us");

        mNameTv = findViewById(R.id.Name);
        mDescTv = findViewById(R.id.Desc);
        textViewNote2 = findViewById(R.id.NoteUserActivity);
        mRatingTv = findViewById(R.id.ratingBarUserActivity);
        mImageTv = findViewById(R.id.profilePic);

        userViewModel = new UserViewModel(new UserRepositoryImpl());

        if(!(user.getPhoto().isEmpty())){
            Glide.with(getApplicationContext())
                    .load(user.getPhoto())
                    .into(mImageTv);
        }
        phone = (Button) findViewById(R.id.phoneBtn);
        sms = (Button) findViewById(R.id.messBtn);
        //int greenColor = Color.green(20);
        int whiteColor = Color.WHITE;

        phone = (Button)findViewById(R.id.phoneBtn);
        mRatingTv.setNumStars(5);
        // phone.setBackgroundColor(greenColor);
        phone.setTextColor(whiteColor);

        Intent intent = getIntent();

        mRatingTv.setNumStars(5);
        mRatingTv.setMax(5);
        mNameTv.setText(user.getPrenom()+" "+user.getNom());
        mDescTv.setText(user.getEmail());
        mRatingTv.setRating(user.getNote());
        mRatingTv.setNumStars(5);

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        df.setMinimumFractionDigits(1);
        df.setDecimalSeparatorAlwaysShown(true);

        textViewNote2.setText("("+String.valueOf(df.format(user.getNote()))+" / 5)");
        mRatingTv.setRating(user.getNote());

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        sms.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                String smsNumber = user.getTelephone();
                Uri uri = Uri.parse("smsto:" + smsNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(intent);
            }});

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

    private void makePhoneCall() {
        String number = user.getTelephone();
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(UserActivity.this,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UserActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        } else {
            Toast.makeText(UserActivity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


}