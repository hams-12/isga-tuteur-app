package com.example.isgatuteur.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.isgatuteur.R;
import com.example.isgatuteur.activities.NoterSeance;
import com.example.isgatuteur.adapters.SeanceAdapter;
import com.example.isgatuteur.models.Seance;
import com.example.isgatuteur.repositories.SeanceRepositoryImpl;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.SeanceViewModel;
import com.example.isgatuteur.viewModels.UserViewModel;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SeancesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeancesFragment extends Fragment {

    ListView listViewSeances;
    static List<Seance> listeseancesfb;
    String userRefField;
    List<String> etats = new ArrayList<String>();
    Spinner spinnerEtats;
    SeanceAdapter seanceAdapter;
    AutoCompleteTextView autoCompleteTextViewSeance;
    SeanceViewModel seanceViewModel;
    UserViewModel userViewModel;
    AlertDialog.Builder alertDialog;
    ImageView cancel;
    TextView textViewAlertDialogMsg;

    Adapter adapter;
    public SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    public static final String KEY_CHOIX_PROFIL = "choixProfil";
    public String valueChoixProfil;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SeancesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SeancesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SeancesFragment newInstance(String param1, String param2) {
        SeancesFragment fragment = new SeancesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /*@Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setActionBarTitle("Liste Séances");
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seances, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new UserViewModel(new UserRepositoryImpl());

        listViewSeances = view.findViewById(R.id.listViewSeance);
        autoCompleteTextViewSeance = view.findViewById(R.id.spinnerSeance2);
        listeseancesfb = new ArrayList<Seance>();
        seanceAdapter = new SeanceAdapter(listeseancesfb,view.getContext());
        seanceViewModel = new SeanceViewModel(new SeanceRepositoryImpl());
        preferences = view.getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        //On verifie si la préférence existe déjà
        valueChoixProfil = preferences.getString(KEY_CHOIX_PROFIL,null);

        if(valueChoixProfil.equals("tuteur"))
            userRefField = "tuteurRef";
        else {
            userRefField = "etudiantRef";
        }

        /////////*****/
        userViewModel.getMyCurrentUserRef().observe(getViewLifecycleOwner(), new Observer<DocumentReference>() {
            @Override
            public void onChanged(DocumentReference connectedUserRef) {

                if(userRefField != null){

                    seanceViewModel.getAllEtatLiveData().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                        @Override
                        public void onChanged(List<String> seancesList) {
                            if(valueChoixProfil.equals("tuteur")){
                                seancesList.remove("Terminée pas encore notée");
                            }
                            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), R.layout.auto_complete_view_item, seancesList);
                            autoCompleteTextViewSeance.setAdapter(adapter2);
                            autoCompleteTextViewSeance.setText(autoCompleteTextViewSeance.getAdapter().getItem(0).toString(),false);
                        }
                    });
                    //chargement de toutes séances pour la première fois
                    getAndShowAllSeanceInListView(userRefField,connectedUserRef);
                    autoCompleteTextViewSeance.setOnItemClickListener((parent, view1, position, id) -> {
                        String etat = parent.getItemAtPosition(position).toString();
                        if(etat.equalsIgnoreCase("Toutes les séances")){
                            getAndShowAllSeanceInListView(userRefField,connectedUserRef);
                        }
                        else if (etat.equalsIgnoreCase("Terminée pas encore notée")){
                            seanceViewModel.getSeanceTerminePasNote(userRefField,connectedUserRef).observe(getViewLifecycleOwner(), new Observer<List<Seance>>() {
                                @Override
                                public void onChanged(List<Seance> seances) {
                                    if(seances.isEmpty()){
                                        showSeanceInListVeiw(seances);
                                        afficherAlertDialog("","Aucune séance \"Terminée pas encore notée\" dans la base de données" );
                                        //Toast.makeText(getContext(), "Aucune séance \"Terminée pas encore notée\" dans la base de données", Toast.LENGTH_SHORT).show();
                                    }else{
                                        showSeanceInListVeiw(seances);
                                    }
                                }
                            });
                        }
                /*else if (etat.equalsIgnoreCase("Terminée et notée")){
                    seanceViewModel.getSeanceTerminePasNote(userRefField,connectedUserRef).observe(getViewLifecycleOwner(), new Observer<List<Seance>>() {
                        @Override
                        public void onChanged(List<Seance> seances) {
                            if(seances.isEmpty()){
                                Toast.makeText(getContext(), "Aucune \"Terminée et notée\" dans la base de données", Toast.LENGTH_SHORT).show();
                            }else{
                                showSeanceInListVeiw(seances);
                            }
                        }
                    });
                }*/
                        else{
                            seanceViewModel.getSeanceByEtatLiveData(userRefField,connectedUserRef,etat).observe(getViewLifecycleOwner(), new Observer<List<Seance>>() {
                                @Override
                                public void onChanged(List<Seance> seances) {
                                    if(seances.isEmpty()){
                                        showSeanceInListVeiw(seances);
                                        afficherAlertDialog("","Aucune séance \"en attente\" la base de données");
                                        //Toast.makeText(getContext(), "Aucune séance \"en attente\" la base de données", Toast.LENGTH_SHORT).show();
                                    }else{
                                        showSeanceInListVeiw(seances);
                                    }
                                }
                            });
                        }
                    });

                }

            }
        });
    }

    public void showSeanceInListVeiw(List<Seance> seanceslistv){
        seanceAdapter = new SeanceAdapter(seanceslistv,getContext());
        listViewSeances.setAdapter(seanceAdapter);
        listViewSeances.setOnItemClickListener(((adapterView, view1, i, l) -> {
            Seance sean = (Seance) listViewSeances.getAdapter().getItem(i);
            if(valueChoixProfil.equals("etudiant") && sean.getEtat().equals("terminé")){
                Intent intent = new Intent(getContext(), NoterSeance.class);
                if(sean.getId()!=null){
                    intent.putExtra("IdSeance",sean.getId());
                }
                startActivity(intent);
            }
            if(valueChoixProfil.equals("etudiant") && !(sean.getEtat().equals("terminé"))){
                afficherAlertDialog("","Veuillez attendre la fin de la séance pour pouvoir noter");
                //Toast.makeText(getContext(), "Veuillez attendre la fin de la séance pour pouvoir noter", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public void getAndShowAllSeanceInListView(String reffield, DocumentReference connecteduserref){

        seanceViewModel.getSeanceByUserRefLiveData(reffield,connecteduserref).observe(getViewLifecycleOwner(), new Observer<List<Seance>>() {
            @Override
            public void onChanged(List<Seance> seances) {
                if(seances!=null){
                    if(seances.isEmpty()){
                        showSeanceInListVeiw(seances);
                        afficherAlertDialog("","Aucune séance dans la base de données");
                        //Toast.makeText(getContext(), "Aucune séance dans la base de données", Toast.LENGTH_SHORT).show();
                    }else{
                        showSeanceInListVeiw(seances);
                    }
                }

            }
        });

    }

    public void afficherAlertDialog(String titre, String message){
        alertDialog = new AlertDialog.Builder(getContext());
        View altertePersonalise = LayoutInflater.from(getContext()).inflate(R.layout.altert_dialog_layout,null);
        cancel = (ImageView) altertePersonalise.findViewById(R.id.imageViewCancel);
        alertDialog.setView(altertePersonalise);
        AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        textViewAlertDialogMsg = altertePersonalise.findViewById(R.id.textViewAlertDialogMsg);
        textViewAlertDialogMsg.setText(message);
        //alertDialog.setMessage("Aucune séance \"Terminée pas encore notée\" dans la base de données");
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}