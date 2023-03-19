package com.example.isgatuteur.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.isgatuteur.R;
import com.example.isgatuteur.models.Matiere;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatiereAdapter extends BaseAdapter {

    List<Matiere> listedesitems;
    Context c;
    LayoutInflater inflater;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    public SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    public static final String KEY_CHOIX_PROFIL = "choixProfil";
    //MatiereViewModel matiereViewModel;

    public MatiereAdapter(List<Matiere> listedesitems,Context c) {
        this.c = c;
        this.listedesitems = listedesitems;
        this.inflater = LayoutInflater.from(c);
    }

    public List<Matiere> getListedesitems() {
        return listedesitems;
    }

    public void setListedesitems(List<Matiere> listedesitems) {
        this.listedesitems = listedesitems;
    }

    @Override
    public int getCount() {
        return listedesitems.size();
    }

    @Override
    public Object getItem(int i) {
        return listedesitems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.list_matiere_item,null);

//        matiereViewModel = new MatiereViewModel(new MatiereRepositoryImpl());

        Matiere matiere = (Matiere) getItem(i);
        String nom = matiere.getNom();
        mAuth = FirebaseAuth.getInstance();

        TextView textView = view.findViewById(R.id.itemMatiereTextView);
        Switch mySwitch = view.findViewById(R.id.switchtuteur);
        preferences = view.getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        textView.setText(nom);

        String valueChoixProfil = preferences.getString(KEY_CHOIX_PROFIL,null);
        if(valueChoixProfil.equals("etudiant")){
            mySwitch.setVisibility(View.INVISIBLE);
        }
        else {
            db = FirebaseFirestore.getInstance();
            DocumentReference matiereRef = db.collection("matieres").document(matiere.getId());
            DocumentReference curTuteurRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

            //Verifie si le tuteur enseigne ou non une matière (si sa référence se trouve dans tuteursRef de la collection matière

            matiereRef.get().addOnSuccessListener(documentSnapshot -> {
                if(documentSnapshot.exists()){
                    List<DocumentReference> tuteursRefList = (List<DocumentReference>) documentSnapshot.get("tuteursRef");
                    if(tuteursRefList == null){
                        mySwitch.setChecked(false);
                    }else {
                        if(tuteursRefList.contains(curTuteurRef)){
                            mySwitch.setChecked(true);
                        }
                    }
                }
            });


            View finalView = view;
            mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //DocumentReference curTuteurRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                    //Log.d("current ref",""+curTuteurRef);
                    if(isChecked && valueChoixProfil.equals("tuteur")){

                        //récupération du tableau au complet
                        matiereRef.get().addOnSuccessListener(documentSnapshot -> {
                            if(documentSnapshot.exists()){
                                List<DocumentReference> tuteursRefList = (List<DocumentReference>) documentSnapshot.get("tuteursRef");
                                if(tuteursRefList == null){
                                    tuteursRefList = new ArrayList<>();
                                }
                                if(!(tuteursRefList.contains(curTuteurRef))){
                                    tuteursRefList.add(curTuteurRef); // Ajouter la référence du tuteur modifié

                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("tuteursRef", tuteursRefList);

                                    matiereRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(c, "Vous aidez maintenant les étudiants dans cette matière", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(c, "Une erreur est survenue...", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        });


                        //matiereRef.update("tuteursRef", "terminé");
                        //mySwitch.setEnabled(false);
                    }
                /*if(isChecked && seance.getEtat().equals("terminé")){
                    mySwitch.setEnabled(false);
                }*/
                    else {

                        matiereRef.get().addOnSuccessListener(documentSnapshot -> {
                            if(documentSnapshot.exists()){
                                List<DocumentReference> tuteursRefList = (List<DocumentReference>) documentSnapshot.get("tuteursRef");

                                tuteursRefList.remove(curTuteurRef); // Ajouter la référence du tuteur modifié

                                Map<String, Object> updates = new HashMap<>();
                                updates.put("tuteursRef", tuteursRefList);

                                matiereRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(c, "Vous n'aidez plus les étudiants dans cette matière", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(c, "Une erreur est survenue...", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }
                }
            });
        }

        return view;
    }
}
