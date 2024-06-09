package com.hareem.anxietyrelief.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hareem.anxietyrelief.R;
import com.hareem.anxietyrelief.Therapist;
import com.hareem.anxietyrelief.ViewTherapistProfile;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TherapistAdapter extends RecyclerView.Adapter<TherapistAdapter.TherapistViewHolder> {

    private List<Therapist> therapists = new ArrayList<>();

    @NonNull
    @Override
    public TherapistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_therapist, parent, false);
        return new TherapistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TherapistViewHolder holder, int position) {
        Therapist therapist = therapists.get(position);
        holder.bind(therapist);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewTherapistProfile.class);
                SharedPreferences ViewTherapistsharedPreferences = view.getContext().getSharedPreferences("ViewTherapistPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = ViewTherapistsharedPreferences.edit();
                editor.putString("ViewProfiletherapist_id", therapist.getUid());
                editor.apply();
//                intent.putExtra("therapist_id", therapist.getUid());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return therapists.size();
    }

    public void setTherapists(List<Therapist> therapists) {
        this.therapists = therapists;
        notifyDataSetChanged();
    }

    static class TherapistViewHolder extends RecyclerView.ViewHolder {

        private TextView username;
        private ImageView expandIcon;
        private TextView degreeTextView;
        private LinearLayout degreesLayout;
        private boolean isExpanded = false;
        private Therapist therapist;
        private CircleImageView profilePicture;

        TherapistViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.textView_username);
            profilePicture = itemView.findViewById(R.id.account_image);
            expandIcon = itemView.findViewById(R.id.expandicon);
            degreeTextView = itemView.findViewById(R.id.textView_degree);
            degreesLayout = itemView.findViewById(R.id.layout_degrees);
            expandIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpansion();
                }
            });
        }

        void bind(Therapist therapist) {
            this.therapist = therapist;
            username.setText(therapist.getUsername());
            if (profilePicture != null && therapist.getImage() != null) {
                byte[] decodedImage = Base64.decode(therapist.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                profilePicture.setImageBitmap(bitmap);
            }
            degreesLayout.removeAllViews();
            if (therapist.getDegree() != null && !therapist.getDegree().isEmpty()) {
                degreeTextView.setText("\u2022 " + therapist.getDegree().get(0));
                if (therapist.getDegree().size() > 1) {
                    expandIcon.setVisibility(View.VISIBLE);
                } else {
                    expandIcon.setVisibility(View.GONE);
                }
            } else {
                expandIcon.setVisibility(View.GONE);
            }
            degreesLayout.setVisibility(View.GONE);
        }

        private void toggleExpansion() {
            isExpanded = !isExpanded;
            if (isExpanded) {
                degreesLayout.removeAllViews();
                // Start from index 1 to skip the first degree
                for (int i = 1; i < therapist.getDegree().size(); i++) {
                    addDegreeTextView(therapist.getDegree().get(i));
                }
                expandIcon.setRotation(180);
                degreesLayout.setVisibility(View.VISIBLE);
            } else {
                degreesLayout.removeAllViews();
                degreeTextView.setText("\u2022 " + therapist.getDegree().get(0));
                expandIcon.setRotation(0);
                degreesLayout.setVisibility(View.GONE);
            }
        }


        private void addDegreeTextView(String degreeText) {
            TextView degreeTextView = new TextView(itemView.getContext());
            degreeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            degreeTextView.setText("\u2022 " + degreeText);
            degreeTextView.setTextSize(12);
            degreesLayout.addView(degreeTextView);
        }
    }
}