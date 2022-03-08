package de.thu.housenet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import de.thu.housenet.R;

import de.thu.housenet.adapters.SavedPropertyAdapter;
import de.thu.housenet.applicationData.property.Property;

public class PropertiesSavedActivity extends AppCompatActivity {


    TextView noDataTextView;
    RecyclerView recyclerView;

    SharedPreferences preferences;
    ArrayList<Property> savedProperties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_saved);

        preferences = getSharedPreferences("INITIAL_APP_DATA",MODE_PRIVATE);

        //Recycler view will display all of the searched properties
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewSaved);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Load saved properties
        loadSavedProperties();

        //Set the Logo on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_baseline_house_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        loadSavedProperties();
        super.onResume();
    }

    @SuppressLint("SetTextI18n")
    public void loadSavedProperties(){

        noDataTextView = (TextView)findViewById(R.id.noDataTextView);

        String dataJsonString = preferences.getString("SAVED_PROPERTIES_JSON","");
        savedProperties = new ArrayList<Property>();

        if(dataJsonString.equals("")){
            noDataTextView.setVisibility(View.VISIBLE);
        }
        else{
            noDataTextView.setVisibility(View.INVISIBLE);


            if(!dataJsonString.isEmpty()){
                //Fill in the list with the new data

                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Property>>(){}.getType();
                savedProperties = gson.fromJson(dataJsonString, type);

            }

        }

        SavedPropertyAdapter adapter = new SavedPropertyAdapter(PropertiesSavedActivity.this,savedProperties);
        recyclerView.setAdapter(adapter);

    }
}