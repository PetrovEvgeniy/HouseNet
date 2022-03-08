package de.thu.housenet.runnables;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.thu.housenet.adapters.PropertyAdapter;
import de.thu.housenet.applicationData.BackEndUrls;
import de.thu.housenet.applicationData.property.Property;

public class PropertyDetailsRunnable implements Runnable{

    private String selectedPropertyId;
    private Context context;

    ArrayList<Property> savedProperties;

    Button callButton,saveButton;
    TextView descriptionTextView;

    private SharedPreferences preferences;
    private boolean running = false;
    private boolean success = false;
    private boolean noPhoneNumber = true;

    public PropertyDetailsRunnable(Context context,String selectedPropertyId, Button callButton,Button saveButton, TextView descriptionTextView){
        this.callButton =  callButton;
        this.saveButton = saveButton;
        this.descriptionTextView = descriptionTextView;

        this.context = context;
        this.selectedPropertyId = selectedPropertyId;
        this.preferences = context.getSharedPreferences("INITIAL_APP_DATA",MODE_PRIVATE);
    }

    @Override
    public void run() {
        synchronized (PropertyDetailsRunnable.this){

            if(!running){
                running = true;
                loadPropertyDetails();

                if(success){
                    //display success notification
                    //showSuccessToast();
                }
                else{
                    //display error notification
                    showErrorAlert();
                }

            }

        }
    }

    //This method will update the old exchange rates with the actual ones (from the API)

    synchronized public void loadPropertyDetails(){

        loadSavedProperties();

        String data = "";
        Property propertyWithDetails;
        try{

            String urlString = getCustomDetailsUrl();
            URL url = new URL(urlString);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            //Set the headers
            httpURLConnection.setRequestProperty("X-RapidAPI-Host", BackEndUrls.X_HOST);
            httpURLConnection.setRequestProperty("X-RapidAPI-Key",BackEndUrls.X_KEY);

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = bufferedReader.readLine()) != null){

                data = data + line;
            }


            if(!data.isEmpty()){
                JSONObject property = new JSONObject(data);

                //Usage of the custom JSONObject constructor
                propertyWithDetails = new Property(property,preferences);


                if(!propertyWithDetails.getPhoneNumber().isEmpty()){
                    noPhoneNumber = false;
                }



                //Set data here in the UI Thread
                ContextCompat.getMainExecutor(context).execute(()  -> {
                    // This is where the UI code goes.
                    descriptionTextView.setText(propertyWithDetails.getDescription());
                    descriptionTextView.setMovementMethod(new ScrollingMovementMethod());

                    // Check if the property ID is saved, if yes then change the button saved colour to yellow

                    toggleSaveButtonText(isPropertySaved(selectedPropertyId));

                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View view) {

                            if(isPropertySaved(selectedPropertyId)){
                                //if it is saved -> remove it from the list and then save the list in the shared preferences as json string

                                savedProperties.removeIf(p -> p.getExternalID().equals(selectedPropertyId));

                                //toggle save button
                                toggleSaveButtonText(false);
                            }
                            else{
                                //if it is not saved-> add it to the list and save the list in the shared preferences as json string
                                savedProperties.add(propertyWithDetails);

                                 //toggle save button
                                 toggleSaveButtonText(true);
                            }

                            updateSavedProperties();


                        }
                    });

                    callButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Go to new phone keyboard activity with the phone number given

                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+propertyWithDetails.getPhoneNumber()));
                            context.startActivity(intent);
                        }
                    });

                    if(!noPhoneNumber){
                        callButton.setEnabled(true);
                    }
                    else{
                        callButton.setEnabled(false);
                    }

                    saveButton.setEnabled(true);
                });
            }


            //here we sett running to false
            running = false;
            success = true;


        }
        catch (Exception e){
            running = false;
            success = false;
            Log.e("Bayut API", "DATABASE ERROR!!");
            e.printStackTrace();
        }
    }


    synchronized public void showErrorAlert() {

        ContextCompat.getMainExecutor(context).execute(()  -> {
            // This is where the UI code goes.

            AlertDialog ad = new AlertDialog.Builder(this.context)
                    .setTitle("Database error!")
                    .setMessage("An error has occurred while trying to receive data from the API server. Please check the log message!")
                    .setPositiveButton(android.R.string.yes, null)

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setIcon(android.R.drawable.stat_notify_error)
                    .show();
        });

    }

    synchronized public void showSuccessToast(){
        ContextCompat.getMainExecutor(context).execute(()  -> {

            Toast t = Toast.makeText(this.context, "Successfully retrieved details data!", Toast.LENGTH_LONG);
           t.show();
        });
    }

    private String getCustomDetailsUrl(){
        String resultUrl = BackEndUrls.BASE_PROPERTY_DETAIL_URL;

        if(!selectedPropertyId.isEmpty()){
            resultUrl += "?externalID=" + selectedPropertyId;

        }
        else{
            Log.e("getDetailsUrl", "ERROR! The selected property id is empty");
            return "";
        }


        return resultUrl;

    }

    synchronized private void loadSavedProperties(){
        String jsonStringProperties = preferences.getString("SAVED_PROPERTIES_JSON","");

        savedProperties = new ArrayList<Property>();

        if(!jsonStringProperties.isEmpty()){
            //Fill in the list

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Property>>(){}.getType();
            savedProperties = gson.fromJson(jsonStringProperties, type);

        }

    }


    synchronized private void updateSavedProperties(){
        //Update the json string in the shared preferences

        SharedPreferences.Editor editor = preferences.edit();

        //Convert java object to JSON format,
        //And returned as JSON formatted string

        Gson gson = new Gson();
        if(!savedProperties.isEmpty()){
            //If the list is not empty update the json string in preferences

            String savedPropertiesJSONString = gson.toJson(savedProperties);

            editor.putString("SAVED_PROPERTIES_JSON", savedPropertiesJSONString);
            editor.apply();
        }
        else{
            //If the list is empty update clear the preferences

            editor.putString("SAVED_PROPERTIES_JSON", "");
            editor.apply();
        }

    }

    synchronized private boolean isPropertySaved(String propertyId){
        for(Property p : savedProperties){
            if(p.getExternalID() != null && p.getExternalID().equals(propertyId)){
             return true;
            }
        }

        return false;
    }


    synchronized private void toggleSaveButtonText(boolean isSaved){
        ContextCompat.getMainExecutor(context).execute(()  -> {
           if(!isSaved){
               saveButton.setText("Save");
               saveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
           }
           else{
               saveButton.setText("Saved");
               saveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);

           }
        });
    }

}
