package com.example.isgatuteur.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.isgatuteur.R;
import com.example.isgatuteur.models.Seance;
import com.example.isgatuteur.repositories.SeanceRepositoryImpl;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PublierFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PublierFragment extends Fragment {

    DatePicker datePickerDate;
    TimePicker timePicker;
    EditText editTextNumEtudiant;
    SeanceRepositoryImpl seanceRepository;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PublierFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PublierFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PublierFragment newInstance(String param1, String param2) {
        PublierFragment fragment = new PublierFragment();
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
        ((MainActivity)getActivity()).setActionBarTitle("Création d'une séance");
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_publier, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seanceRepository = new SeanceRepositoryImpl();
        datePickerDate = view.findViewById(R.id.datePicker);
        editTextNumEtudiant = view.findViewById(R.id.numEtudantEditText);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        Button btn = view.findViewById(R.id.btncreerseance);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DocumentReference tuteurRef = db.collection("users").document(mAuth.getCurrentUser().getUid()); //a remplacer par getUserRefById
        String connectedUserEmail = mAuth.getCurrentUser().getEmail();


        btn.setOnClickListener(v -> {
            String emailEtd = editTextNumEtudiant.getText().toString().trim();
            if (emailEtd.isEmpty()){
                editTextNumEtudiant.setError("Veuillez inserer l'email de l'étudiant");
            }
            else if(emailEtd.equalsIgnoreCase(connectedUserEmail)){
                editTextNumEtudiant.setError("Vous ne pouvez pas créer une séance avec vous même");
            }
            else {
                long date = datePickerDate.getCalendarView().getDate();
                Date date1 = new Date(date);
                date1.setHours(timePicker.getCurrentHour());
                date1.setMinutes(timePicker.getCurrentMinute());

                /*Boolean a = seanceRepository.ajouterSeance(emailEtd,tuteurRef,date1);
                if(a){
                    Toast.makeText(getContext(),"Séance Ajoutée avec succès",Toast.LENGTH_LONG).show();
                    editTextNumEtudiant.setText("");
                }*/

                Seance seance = new Seance();
                seance.setId(UUID.randomUUID().toString());
                seance.setEtat("en attente");
                seance.setNote(0);
                seance.setDate(date1);
                                    db.collection("users").whereEqualTo("email",emailEtd).limit(1)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    if(task1.getResult().size()!=0){

                                                        DocumentReference seanceRef = db.collection("seances").document(seance.getId());
                                                        seanceRef.set(seance);
                                                        for (QueryDocumentSnapshot documentSnapshot : task1.getResult()) {

                                                            DocumentReference etudiantRef = documentSnapshot.getReference();
                                                            //DocumentReference etudiantRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
                                                            seanceRef.update("tuteurRef", tuteurRef)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        // Field updated successfully
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        // Field update failed
                                                                    });
                                                            seanceRef.update("etudiantRef", etudiantRef)
                                                                    .addOnSuccessListener(aVoid -> {
                                                                        // Field updated successfully
                                                                    })
                                                                    .addOnFailureListener(e -> {
                                                                        // Field update failed
                                                                    });
                                                        }
                                                        Toast.makeText(getContext(),"Séance Ajoutée avec succès",Toast.LENGTH_LONG).show();
                                                        editTextNumEtudiant.setText("");
                                                    }else {
                                                        editTextNumEtudiant.setError("Aucun étudiant ne possède ce mail");
                                                        //Toast.makeText(getContext(),"Aucun utilisateur ne correspond avec cet email",Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
            }


        });

    }
}