package com.example.isgatuteur.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.example.isgatuteur.R;
import com.example.isgatuteur.models.User;
import com.example.isgatuteur.repositories.UserRepositoryImpl;
import com.example.isgatuteur.singleton.MyFirebaseAuthSingetion;
import com.example.isgatuteur.viewModels.UserViewModel;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    List<User> listUsers;
    Context c;
    LayoutInflater inflater;
    UserViewModel userViewModel;
    public SharedPreferences preferences;
    public static final String MY_PREFS = "fichierReferences";
    public static final String KEY_CHOIX_PROFIL = "choixProfil";

    public UserAdapter(List<User> listUsers, Context c) {
        this.listUsers = listUsers;
        this.c = c;
        this.inflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return listUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return listUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.liste_user_item,null);
        userViewModel = new UserViewModel(new UserRepositoryImpl());
        preferences = view.getContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        User user = (User) getItem(i);

        ImageView mImageView = view.findViewById(R.id.image);
        TextView mName = view.findViewById(R.id.title);
        RatingBar mRate = view.findViewById(R.id.ratingBar);
        TextView mEmail = view.findViewById(R.id.emailitem);
        TextView mNumTel = view.findViewById(R.id.numtelitem);
        CardView mCardView = view.findViewById(R.id.carViewTuteur);


        mName.setText(user.getPrenom()+" "+user.getNom());
        mRate.setRating(user.getNote());
        mEmail.setText(user.getEmail());
        mNumTel.setText(user.getTelephone());
        if(!(user.getPhoto().isEmpty())){
            Glide.with(view.getContext())
                    .load(user.getPhoto())
                    .into(mImageView);
        }
        if(android.os.Build.VERSION.SDK_INT>30){
            mImageView.setRenderEffect(
                    RenderEffect.createBlurEffect(
                            15.0f, 15.0f, Shader.TileMode.MIRROR
                    )
            );
        }
        if(user.getId().equals(MyFirebaseAuthSingetion.getInstance().getCurrentUser().getUid())){
            //mCardView.setVisibility(View.INVISIBLE);
            //mCardView.setOnClickListener(null);
            mCardView.setCardBackgroundColor(Color.parseColor("#12237E"));
            //Toast.makeText(c, "Vous ne pouvez pas vous contacter vous même", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

}
