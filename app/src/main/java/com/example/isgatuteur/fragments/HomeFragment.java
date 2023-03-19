package com.example.isgatuteur.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.isgatuteur.R;
import com.example.isgatuteur.activities.ListTuteurActivity;
import com.example.isgatuteur.adapters.MatiereAdapter;
import com.example.isgatuteur.models.Matiere;
import com.example.isgatuteur.repositories.MatiereRepositoryImpl;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.MatiereViewModel;
import com.example.isgatuteur.viewModels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    ListView listViewMatiere;
    static List<Matiere> listematieresfb;
    MatiereViewModel matiereViewModel;
    UserViewModel userViewModel;
    MatiereAdapter matiereAdapter;
    AutoCompleteTextView autoCompleteTextView;
    TextView textViewAlertDialogMsg;
    AlertDialog.Builder alertDialog;
    ImageView cancel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        ((MainActivity)getActivity()).setActionBarTitle("Accueil - Liste des matières");
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewMatiere = view.findViewById(R.id.listViewMatiere);
        autoCompleteTextView = view.findViewById(R.id.spinnerDomaines2);
        listematieresfb = new ArrayList<Matiere>();
        matiereAdapter = new MatiereAdapter(listematieresfb,view.getContext());
        matiereViewModel = new MatiereViewModel(new MatiereRepositoryImpl());
        userViewModel = new UserViewModel(new UserRepositoryImpl());

        //Afficher la liste matières pour la premiere fois
        this.getAndShowAllMatiereInListView();


        matiereViewModel.getAllDomaineLiveData().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> domainesList) {
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(), R.layout.auto_complete_view_item, domainesList);
                autoCompleteTextView.setAdapter(adapter2);
                autoCompleteTextView.setText(autoCompleteTextView.getAdapter().getItem(0).toString(),false);
            }
        });


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String domaine = parent.getItemAtPosition(position).toString();
                if(!domaine.equalsIgnoreCase("Tous les domaines")){
                    matiereViewModel.getMatiereByDomaineLiveData(domaine).observe(getViewLifecycleOwner(), new Observer<List<Matiere>>() {
                        @Override
                        public void onChanged(List<Matiere> matiereList) {
                            if(matiereList.isEmpty()){
                                showMatiereInListVeiw(matiereList);
                                afficherAlertDialog("","Aucune matière dans la base de données !");
                                //Toast.makeText(getContext(), "Aucune matière dans la base de données", Toast.LENGTH_SHORT).show();
                            }else{
                                showMatiereInListVeiw(matiereList);
                            }
                        }
                    });
                }else{
                    getAndShowAllMatiereInListView();
                }

            }
        });

    }

    public void showMatiereInListVeiw(List<Matiere> matiereslistv){
        matiereAdapter = new MatiereAdapter(matiereslistv,getContext());
        listViewMatiere.setAdapter(matiereAdapter);
        listViewMatiere.setOnItemClickListener(((adapterView, view1, i, l) -> {
            Matiere matiere = (Matiere) listViewMatiere.getAdapter().getItem(i);

            //Si j'ai le temps, faire passer liste par parcelable pour ne pas faire la requete 2 fois
            userViewModel.getCountTuteursOfMatiere(matiere.getId()).observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    if(integer==0){
                        afficherAlertDialog("","Aucun tuteur disponible pour cette matière");
                    }
                    else {
                        Intent intent = new Intent(getContext(), ListTuteurActivity.class);
                        intent.putExtra("idMatiere",matiere.getId());
                        startActivity(intent);
                    }
                }
            });

            /*Intent intent = new Intent(getContext(), ListTuteurActivity.class);
            intent.putExtra("idMatiere",matiere.getId());
            startActivity(intent);*/
        }));
    }

    public void getAndShowAllMatiereInListView(){
        matiereViewModel.getAllMatiereLiveData().observe(getViewLifecycleOwner(), new Observer<List<Matiere>>() {
            @Override
            public void onChanged(List<Matiere> matieres) {
                if(matieres!=null){
                    if(matieres.isEmpty()){
                        afficherAlertDialog("","Aucune matière dans la base de données");
                        //Toast.makeText(getContext(), "Aucune matière dans la base de données", Toast.LENGTH_SHORT).show();
                    }else{
                        showMatiereInListVeiw(matieres);
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
        dialog.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}