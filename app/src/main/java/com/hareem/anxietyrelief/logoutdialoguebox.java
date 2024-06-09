package com.hareem.anxietyrelief;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class logoutdialoguebox extends DialogFragment {
    ImageView cancel;
    Button staylogin;
    Button logout;

    @Nullable
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_logoutdialoguebox, null);

        cancel = dialogView.findViewById(R.id.cancelbtn);
        staylogin = dialogView.findViewById(R.id.stayloginbtn);
        logout = dialogView.findViewById(R.id.logoutbtn);
        staylogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
         logout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("mypref", Context.MODE_PRIVATE);
                 SharedPreferences.Editor editor1 = sharedPreferences.edit();
                 editor1.putBoolean("therapist_login",false);
                 editor1.apply();



                 Intent i = new Intent(getActivity(),TherapistLogin.class)
                         .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(i);
                 getActivity().finish();// O
             }
         });


        builder.setView(dialogView);

        return builder.create();
    }

}