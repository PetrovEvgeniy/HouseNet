package de.thu.housenet.activities;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Objects;

import de.thu.housenet.R;
import de.thu.housenet.runnables.PropertyDetailsRunnable;

public class PropertyDetailActivity extends AppCompatActivity {

    private String selectedPropertyId;

    private ImageView propertyCoverImageView;
    private TextView priceView,bedCountView,bathCountView,areaView,titleView,contactNameView,descriptionView;
    private Button callButton,locationButton,saveButton;

    //Shared preferences will share data between activities
    private SharedPreferences preferences;

    //Runnable
    private PropertyDetailsRunnable propertyDetailsRunnable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        selectedPropertyId = getIntent().getStringExtra("SELECTED_PROPERTY_EXTERNAL_ID");

        propertyCoverImageView = (ImageView)findViewById(R.id.coverImageView);
        priceView = (TextView)findViewById(R.id.propertyPriceView);
        descriptionView = (TextView)findViewById(R.id.descriptionTextView);
        bedCountView = (TextView)findViewById(R.id.bedroomsCountView);
        bathCountView = (TextView)findViewById(R.id.bathroomsCountView);
        areaView = (TextView) findViewById(R.id.squareMetersView);
        titleView = (TextView) findViewById(R.id.titleTextView);
        contactNameView = (TextView) findViewById(R.id.contactNameView);

        callButton = (Button) findViewById(R.id.callButton);
        locationButton = (Button) findViewById(R.id.locationButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        callButton.setEnabled(false);
        locationButton.setEnabled(false);
        saveButton.setEnabled(false);

        //Click listeners
        locationButton.setOnClickListener(this::onLocationButtonClick);


        //Set the Logo on the action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_baseline_house_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //Load passed data
        loadParsedData();

        //Load missing data

        propertyDetailsRunnable = new PropertyDetailsRunnable(this,selectedPropertyId,callButton,saveButton,descriptionView);

        Thread propertyDetailsThread = new Thread(propertyDetailsRunnable);
        propertyDetailsThread.start();

        locationButton.setEnabled(true);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadParsedData(){
        Glide.with(this)
                .load(getIntent().getStringExtra("SELECTED_PROPERTY_IMAGE_URL"))
                .into(propertyCoverImageView);

        priceView.setText(getIntent().getStringExtra("SELECTED_PROPERTY_PRICE"));
        titleView.setText(getIntent().getStringExtra("SELECTED_PROPERTY_TITLE"));
        bedCountView.setText(getIntent().getStringExtra("SELECTED_PROPERTY_BEDS"));
        bathCountView.setText(getIntent().getStringExtra("SELECTED_PROPERTY_BATHS"));
        areaView.setText(getIntent().getStringExtra("SELECTED_PROPERTY_AREA"));
        contactNameView.setText(getIntent().getStringExtra("SELECTED_PROPERTY_CONTACT_NAME"));
    }


    public void onLocationButtonClick(View view){
        // Go to Google maps location app and search by these coordinates
        String location = getIntent().getStringExtra("SELECTED_PROPERTY_LOCATION");
        Uri gmmIntentUri = Uri.parse("geo:"+location);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

}