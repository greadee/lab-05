package com.example.lab5_starter;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener {

    private Button addCityButton;
    private ListView cityListView;

    private ArrayList<City> cityArrayList;
    private ArrayAdapter<City> cityArrayAdapter;

    private FirebaseFirestore db;
    private CollectionReference citiesRef;
    private City selectedCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Add Firestore Database
        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        // Adding Snapshot Listener to the Collection
        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (value == null) return;
            cityArrayList.clear();
            for (QueryDocumentSnapshot snapshot : value){
                String name = snapshot.getString("name");
                String province = snapshot.getString("province");
                cityArrayList.add(new City(name, province));
            }
                cityArrayAdapter.notifyDataSetChanged();
        });


        // Set views
        addCityButton = findViewById(R.id.buttonAddCity);
        cityListView = findViewById(R.id.listviewCities);

        // create city array
        cityArrayList = new ArrayList<>();
        cityArrayAdapter = new CityArrayAdapter(this, cityArrayList);
        cityListView.setAdapter(cityArrayAdapter);

        //addDummyData();

        // set listeners
        addCityButton.setOnClickListener(view -> {
            CityDialogFragment cityDialogFragment = new CityDialogFragment();
            cityDialogFragment.show(getSupportFragmentManager(),"Add City");
        });

        cityListView.setOnItemClickListener((adapterView, view, i, l) -> {
            City city = cityArrayAdapter.getItem(i);
            selectedCity = city; // remember press
            CityDialogFragment cityDialogFragment = CityDialogFragment.newInstance(city);
            cityDialogFragment.show(getSupportFragmentManager(),"City Details");
        });

    }

    @Override
    public void updateCity(City city, String title, String year) {
        if (city == null) return;

        // doc Id before changes
        String oldDocId = city.getName();

        // update local object
        city.setName(title);
        city.setProvince(year);
        cityArrayAdapter.notifyDataSetChanged();

        // doc Id after changes
        String newDocId = city.getName();

        // delete old doc if there has been a name change in City.
        if (!oldDocId.equals(newDocId)) {
            citiesRef.document(oldDocId).delete()
                    .addOnFailureListener(e -> Log.e("Firestore", "Failed deleting old doc", e));
        }

        // add new Doc to firestore database.
        citiesRef.document(newDocId).set(city)
                .addOnFailureListener(e -> Log.e("Firestore", "Failed updating doc", e));
    }

    @Override
    public void addCity(City city){
        cityArrayList.add(city);
        cityArrayAdapter.notifyDataSetChanged();

        // 16/19 Modifiying the AddCity method
        DocumentReference docRef = citiesRef.document(city.getName());
        docRef.set(city);
    }
    @Override
    public void delCity(City city){
        cityArrayList.remove(city);
        cityArrayAdapter.notifyDataSetChanged();

        // Removing the entry from the firestore document.
        DocumentReference docRef = citiesRef.document(city.getName());
        docRef.delete();
    }
    public void addDummyData(){
        City m1 = new City("Edmonton", "AB");
        City m2 = new City("Vancouver", "BC");
        cityArrayList.add(m1);
        cityArrayList.add(m2);
        cityArrayAdapter.notifyDataSetChanged();
    }
}