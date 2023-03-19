package com.example.isgatuteur.helper;

import android.app.Application;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.isgatuteur.R;

public class HelperAlertDialog {

    static TextView textViewAlertDialogMsg;
    static AlertDialog.Builder alertDialog;
    static ImageView cancel;

    public static void afficherAlertDialog(String titre, String message, Application context){
        alertDialog = new AlertDialog.Builder(context);
        View altertePersonalise = LayoutInflater.from(context).inflate(R.layout.altert_dialog_layout,null);
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
