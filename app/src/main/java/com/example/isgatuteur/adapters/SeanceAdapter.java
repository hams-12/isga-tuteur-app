package com.example.isgatuteur.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.isgatuteur.R;
import com.example.isgatuteur.models.Seance;
import com.example.isgatuteur.models.User;
import com.example.isgatuteur.repositories.SeanceRepositoryImpl;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.SeanceViewModel;
import com.example.isgatuteur.viewModels.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SeanceAdapter extends BaseAdapter {

    List<Seance> seancesList;
    Context c;
    LayoutInflater inflater;
    public SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    public static final String KEY_CHOIX_PROFIL = "choixProfil";
    UserViewModel userViewModel;
    SeanceViewModel seanceViewModel;

    public SeanceAdapter(List<Seance> seancesList, Context c) {
        this.seancesList = seancesList;
        this.c = c;
        this.inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return seancesList.size();
    }

    @Override
    public Object getItem(int i) {
        return seancesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.list_seance_item,null);

        userViewModel = new UserViewModel(new UserRepositoryImpl());
        seanceViewModel = new SeanceViewModel(new SeanceRepositoryImpl());

        Seance seance = (Seance) getItem(i);
        Date date = seance.getDate();
        SimpleDateFormat formater = new SimpleDateFormat("dd LLL yyyy à HH:mm");
        String etat = seance.getEtat();
        float note = seance.getNote();

        TextView mDate = view.findViewById(R.id.title);
        RatingBar mRate = view.findViewById(R.id.ratingBar);
        TextView mEtat = view.findViewById(R.id.etatitem);
        TextView mUserEmail = view.findViewById(R.id.userEmailitem);
        TextView mUserNom = view.findViewById(R.id.userNomitem);
        Switch mySwitch = view.findViewById(R.id.switchtermine);
        CardView myCardView = view.findViewById(R.id.carView);

        preferences = view.getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        //On verifie si la préférence existe déjà
        String valueChoixProfil = preferences.getString(KEY_CHOIX_PROFIL,null);
        if(valueChoixProfil.equals("etudiant")){
            mySwitch.setVisibility(View.INVISIBLE);

            userViewModel.getUserByDocRef(seance.getTuteurRef()).observe((LifecycleOwner) view.getContext(), new Observer<User>() {
                @Override
                public void onChanged(User tute1) {
                    mUserEmail.setText("Email du tuteur : "+tute1.getEmail());
                    mUserNom.setText("Tuteur : "+tute1.getPrenom().toUpperCase()+" "+tute1.getNom().toUpperCase());
                }
            });

            if(note!=0){
                myCardView.setCardBackgroundColor(Color.parseColor("#4F68F6"));
            }
        }else {

            userViewModel.getUserByDocRef(seance.getEtudiantRef()).observe((LifecycleOwner) view.getContext(), new Observer<User>() {
                @Override
                public void onChanged(User etd) {
                    mUserEmail.setText("Email de l'étudiant : "+etd.getEmail());
                    mUserNom.setText("Etudiant : "+etd.getPrenom().toUpperCase()+" "+etd.getNom().toUpperCase());
                }
            });
        }

        mDate.setText(formater.format(date));
        mRate.setRating(note);
        mEtat.setText("Etat : "+etat);


        if(seance.getEtat().equals("terminé") && valueChoixProfil.equals("tuteur")){
            mySwitch.setChecked(true);

            if(seance.getNote()!=0){
                mySwitch.setEnabled(false);
                myCardView.setCardBackgroundColor(Color.parseColor("#4F68F6"));
            }
             //#6479F0
        }

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && valueChoixProfil.equals("tuteur")){
                    //seanceRef.update("etat", "terminé");
                    seanceViewModel.updateSeanceByFieldName("etat", seance.getId(), "terminé");
                    mySwitch.setEnabled(false);
                    myCardView.setCardBackgroundColor(Color.parseColor("#4F68F6")); // #6BD193 vert #6479F0 bleu 2
                    //mEtat.setText("Etat : terminé");
                }
                /*if(isChecked && seance.getEtat().equals("terminé")){
                    mySwitch.setEnabled(false);
                }*/
                else {
                    //seanceRef.update("etat", "en attente");
                    seanceViewModel.updateSeanceByFieldName("etat", seance.getId(), "en attente");
                    mySwitch.setEnabled(true);
                    myCardView.setCardBackgroundColor(Color.parseColor("#7F90EF")); // #6BD193 vert #6479F0 bleu 2
                    //mEtat.setText("Etat : en attente");
                }
            }
        });

        return view;
    }
}
