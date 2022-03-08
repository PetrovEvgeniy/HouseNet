package de.thu.housenet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Objects;

import de.thu.housenet.R;
import de.thu.housenet.applicationData.categories.Categories;
import de.thu.housenet.applicationData.locations.Locations;
import de.thu.housenet.applicationData.property.Property;
import de.thu.housenet.runnables.SearchPropertiesRunnable;

public class MainActivity extends AppCompatActivity {

    private static Categories categories;
    private static Locations locations;


    //Shared preferences will share data between activities
    private SharedPreferences preferences;

    //Saved properties list
    public ArrayList<Property> savedProperties;

    //Runnable
    private SearchPropertiesRunnable searchPropertiesRunnable;

    //Elements
    private Spinner locationsSpinner;
    private Spinner propertyTypesSpinner;
    private Switch showRentalsSwitch;
    private RecyclerView recyclerView;
    private ImageView notFoundImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories = new Categories();
        locations = new Locations();

        locationsSpinner = (Spinner) findViewById(R.id.locationsSpinner);
        propertyTypesSpinner = (Spinner) findViewById(R.id.propertyTypesSpinner);
        showRentalsSwitch = (Switch) findViewById(R.id.showRentalsSwitch);

        //The following image view will make the app more UX friendly when no result is found
        notFoundImage = (ImageView) findViewById(R.id.resultsNotFoundImageView);

        //Recycler view will display all of the searched properties
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Set the Logo on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_baseline_house_24);

        //Load the shared preferences
        preferences = getSharedPreferences("INITIAL_APP_DATA",MODE_PRIVATE);


        //Runnable initialisations
        searchPropertiesRunnable = new SearchPropertiesRunnable(this,recyclerView,notFoundImage);


        //Fill in the spinners with this data
        fillInSpinnersWithData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.saved) {
            //Go to saved activity
            openSavedPropertiesActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSavedPropertiesActivity(){
        Intent intent = new Intent(this,PropertiesSavedActivity.class);
        startActivity(intent);
    }

    public void fillInSpinnersWithData(){

        String[] locationsArray = locations.getCityNames();
        String[] categoriesArray = categories.getPropertyTypeNames();


        //Fill in he spinners, using the adapters
        ArrayAdapter<String> locationsArrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,locationsArray);
        locationsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        locationsSpinner.setAdapter(locationsArrayAdapter);


        ArrayAdapter<String> categoriesArrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, categoriesArray);
        categoriesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        propertyTypesSpinner.setAdapter(categoriesArrayAdapter);

    }


    public void onSearchButtonClick(View v){
        String cityName = locationsSpinner.getSelectedItem().toString();
        String propertyType = propertyTypesSpinner.getSelectedItem().toString();
        boolean showOnlyRentals = showRentalsSwitch.isChecked();

        String cityCode = locations.getCityCode(cityName);
        String propertyId = categories.getPropertyTypeIdString(propertyType);


        //Save the searched data in Shared preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cityCodeSelected",cityCode);
        editor.putString("propertyTypeSelectedId", propertyId);
        editor.putBoolean("showOnlyRentalsSelected",showOnlyRentals);
        editor.apply();

        //Start runnable that will send GET request to the API server with these parameters

        Thread searchThread = new Thread(searchPropertiesRunnable);
        searchThread.start();


    }


}