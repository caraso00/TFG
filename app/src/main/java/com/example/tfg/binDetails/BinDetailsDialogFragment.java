package com.example.tfg.binDetails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tfg.R;
import com.example.tfg.reportMod.ReportModActivity;
import com.google.android.material.snackbar.Snackbar;

public class BinDetailsDialogFragment extends DialogFragment {

    private static final String ARG_BIN = "bin";
    private RatingBar newRatingBar;
    private float newRatingValue;
    private TextView dialog_ur_rating_value;

    public static BinDetailsDialogFragment newInstance(BinDetails bin) {
        BinDetailsDialogFragment fragment = new BinDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BIN, bin);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup contenedor, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bin_details, contenedor, false);

        BinDetails bin = (BinDetails) (getArguments() != null ? getArguments().getSerializable(ARG_BIN) : null);

        if (bin != null) {
            TextView titleUi = view.findViewById(R.id.dialog_info_title);
            titleUi.setText(bin.getTitulo());

            ImageView imageUi = view.findViewById(R.id.dialog_info_image);
            imageUi.setImageResource(bin.getImagenResId());

            TextView locationUi = view.findViewById(R.id.dialog_info_location);
            locationUi.setText(bin.getUbicacion());

            TextView typeUi = view.findViewById(R.id.dialog_info_type);
            typeUi.setText(bin.getTipo());

            TextView statusUi = view.findViewById(R.id.dialog_info_status);
            statusUi.setText(bin.getEstado());

            RatingBar currentRatingVar = view.findViewById(R.id.dialog_current_rating_bar);
            TextView currentRatingValue = view.findViewById(R.id.dialog_current_rating_value);
            currentRatingVar.setRating(bin.getRating());
            currentRatingValue.setText(String.valueOf(bin.getRating()));

            newRatingBar = view.findViewById(R.id.dialog_new_rating_bar);
            dialog_ur_rating_value = view.findViewById(R.id.dialog_ur_rating_value);
            newRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (fromUser) {
                        showRatingConfirmationDialog(rating);
                    }
                }
            });

            ImageView modIcon = view.findViewById(R.id.mod_icon);
            modIcon.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ReportModActivity.class);
                intent.putExtra("ubicacion", bin.getUbicacion());
                intent.putExtra("tipoSelected", getTipo(bin.getTipo()));  // Replace with actual selected index
                intent.putExtra("estadoSelected", getEstado(bin.getEstado()));  // Replace with actual selected index
                startActivity(intent);
            });
        }

        ImageView closeIcon = view.findViewById(R.id.close_icon);
        closeIcon.setOnClickListener(v -> dismiss());

        return view;
    }

    private void showRatingConfirmationDialog(float rating) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Quiere valorar el contenedor?")
            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    newRatingValue = rating;
                    newRatingBar.setRating(newRatingValue);
                    dialog_ur_rating_value.setText(String.valueOf(newRatingValue));
                    newRatingBar.setIsIndicator(true);
                    Snackbar.make(getView(), "Valoración registrada", Snackbar.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    newRatingBar.setRating(0);
                }
            });
        builder.create().show();
    }

    private int getTipo(String tipo) {
        switch (tipo) {
            case ("Orgánico"):
                return 0;
            case ("Plástico"):
                return 1;
            case ("Papel y cartón"):
                return 2;
            default:
                return 3;
        }
    }

    private int getEstado(String estado) {
        switch (estado) {
            case ("Perfecto"):
                return 0;
            case ("Bien"):
                return 1;
            case ("Sucio"):
                return 2;
            default:
                return 3;
        }
    }
}
