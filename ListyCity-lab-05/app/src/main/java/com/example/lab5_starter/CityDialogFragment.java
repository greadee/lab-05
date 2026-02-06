package com.example.lab5_starter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class CityDialogFragment extends DialogFragment {
    interface CityDialogListener {
        void updateCity(City city, String title, String year);
        void addCity(City city);
        void delCity(City city);
    }
    private CityDialogListener listener;

    public static CityDialogFragment newInstance(City city){
        Bundle args = new Bundle();
        args.putSerializable("City", city);

        CityDialogFragment fragment = new CityDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CityDialogListener){
            listener = (CityDialogListener) context;
        }
        else {
            throw new RuntimeException("Implement listener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_city_details, null);
        EditText editCityName = view.findViewById(R.id.edit_city_name);
        EditText editCityProv = view.findViewById(R.id.edit_province);

        String tag = getTag();
        Bundle bundle = getArguments();
        City city;

        boolean isAnEdit = Objects.equals(tag, "City Details");

        if (isAnEdit && bundle != null){
            city = (City) bundle.getSerializable("City");
            assert city != null;
            editCityName.setText(city.getName());
            editCityProv.setText(city.getProvince());
        }
        else {
            city = null;}

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle(isAnEdit ? "City Details" : "Add City")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Continue", (dialog, which) -> {
                    String name = editCityName.getText().toString();
                    String prov = editCityProv.getText().toString();
                    if (Objects.equals(tag, "City Details")) {
                        listener.updateCity(city, name, prov);
                    } else {
                        listener.addCity(new City(name, prov));
                    }
                });

        if (isAnEdit) {
            builder.setNeutralButton("Delete", (dialog, which) -> {
                if (city != null) {
                    listener.delCity(city);
                }
            });
        }
        return builder.create();

        }
    }

