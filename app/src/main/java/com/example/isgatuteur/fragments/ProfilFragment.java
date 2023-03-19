package com.example.isgatuteur.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.example.isgatuteur.R;
import com.example.isgatuteur.models.User;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.viewModels.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    public static final String KEY_CHOIX_PROFIL = "choixProfil";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db;
    User us;
    Button btnUpload;
    ImageView profilImg;
    TextView textViewName,textViewDesc,textViewNote;
    RatingBar ratingBar;
    Uri imageUri;
    UserViewModel userViewModel;
    TextView textViewAlertDialogMsg;
    AlertDialog.Builder alertDialog;
    ImageView cancel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfilFragment newInstance(String param1, String param2) {
        ProfilFragment fragment = new ProfilFragment();
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
        ((MainActivity)getActivity()).setActionBarTitle("Votre profil");
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel = new UserViewModel(new UserRepositoryImpl());

        preferences = view.getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        btnUpload = view.findViewById(R.id.btnUpload);
        profilImg = view.findViewById(R.id.imageVewProfil);
        textViewName = view.findViewById(R.id.NamePF);
        textViewNote = view.findViewById(R.id.NotePF);
        textViewDesc = view.findViewById(R.id.DescPF);
        ratingBar = view.findViewById(R.id.ratingBarPF);

        String valueChoixProfil = preferences.getString(KEY_CHOIX_PROFIL,null);
        if(valueChoixProfil.equals("etudiant")){
            ratingBar.setVisibility(View.INVISIBLE);
        }

        userViewModel.getMyCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User useer) {
                String documentId = useer.getId();
                userViewModel.getUserByDocId(documentId).observe(getViewLifecycleOwner(), new Observer<User>() {
                    @Override
                    public void onChanged(User user1) {
                        if(user1.getPhoto()!=null){
                            chargerImageAvecGlide(getContext(),user1.getPhoto(),profilImg);
                        }
                        textViewName.setText(user1.getPrenom()+" "+user1.getNom());
                        textViewDesc.setText(user1.getEmail());
                        DecimalFormat df = new DecimalFormat("#.#");
                        textViewNote.setText("("+df.format(user1.getNote())+" / 5)");
                        ratingBar.setRating(user1.getNote());
                        btnUpload.setEnabled(false);
                    }
                });
            }
        });

        profilImg.setOnClickListener(v -> {
            Intent photoIntent = new Intent(Intent.ACTION_PICK);
            photoIntent.setType("image/*");
            galleryActivityResultLauncher.launch(photoIntent);
        });

        btnUpload.setOnClickListener(v -> {
            uploadImage();
        });
    }

    private void uploadImage() {
        //ProgressBar progressBar = new ProgressBar(getContext());
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("En cours de chargement...");
        progressDialog.show();

        String nomFicher = FirebaseAuth.getInstance().getCurrentUser().getUid();

        StorageReference storageRef = storage.getReference("images/"+nomFicher);
        storageRef.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            //******Mettre à jour l'image de l'utilisateur****/
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        userViewModel.updateProfilePicture(task.getResult().toString());
                                        if(task.getResult().toString()!=null){
                                            chargerImageAvecGlide(getContext(),task.getResult().toString(),profilImg);
                                        }
                                        btnUpload.setEnabled(false);
                                    }
                                }
                            });
                            /*******************/
                            //profilImg.setImageBitmap();
                            Toast.makeText(getContext(), "Image ajouté avec succès !", Toast.LENGTH_SHORT).show();
                        }else {
                            afficherAlertDialog("","Erreur lors de l'ajout de la photo !");
                            //Toast.makeText(getContext(), "Erreur lors de l'ajout de la photo !", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = 100 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount();
                        progressDialog.setMessage("En cours "+(int)progress+"%");
                    }
                });


    }

    public void chargerImageAvecGlide(Context context,String imgsrc, ImageView imgView){
        Glide.with(context)
                .load(imgsrc)
                .into(imgView);
    }

    /*private void updateProfilePicture(String url){
        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.update("photo",url);
    }*/

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Bitmap bitmap=null;
                        Intent data = result.getData();
                        imageUri = data.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        profilImg.setImageBitmap(bitmap);
                        btnUpload.setEnabled(true);
                    }
                    else {
                        Toast.makeText(getContext(), "Retour...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

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