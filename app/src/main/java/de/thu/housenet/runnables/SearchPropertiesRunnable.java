package de.thu.housenet.runnables;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.thu.housenet.adapters.PropertyAdapter;
import de.thu.housenet.applicationData.BackEndUrls;
import de.thu.housenet.applicationData.property.Property;

public class SearchPropertiesRunnable implements Runnable {

    private RecyclerView recyclerView;
    private ImageView notFoundImage;

    private Context context;
    private SharedPreferences preferences;
    private boolean running = false;
    private boolean success = false;

    public SearchPropertiesRunnable(Context context,RecyclerView recyclerView, ImageView notFoundImage){
        this.recyclerView = recyclerView;
        this.notFoundImage = notFoundImage;
        this.context = context;
        this.preferences = context.getSharedPreferences("INITIAL_APP_DATA",MODE_PRIVATE);
    }

    @Override
    public void run() {

        synchronized (SearchPropertiesRunnable.this){

            if(!running){
                running = true;
                //Display loading screen in the main UI thread
                showLoadingToast(true);
                toggleResultNotFoundImage(false);

                loadSearchedProperties();

                if(success){
                    //Successfully retrieved data
                    showLoadingToast(false);
                }
                else{
                    //display error dialog
                    showErrorAlert();
                    showLoadingToast(false);
                }

            }

        }

    }

    //This method will update the old exchange rates with the actual ones (from the API)

    synchronized public void loadSearchedProperties(){


        String data = "";
        ArrayList<Property> propertyList = new ArrayList<Property>();
        try{

            String urlString = getCustomSearchUrl();
            URL url = new URL(urlString);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            //Set the headers
            httpURLConnection.setRequestProperty("X-RapidAPI-Host",BackEndUrls.X_HOST);
            httpURLConnection.setRequestProperty("X-RapidAPI-Key",BackEndUrls.X_KEY);

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while((line = bufferedReader.readLine()) != null){

                data = data + line;
            }


            if(!data.isEmpty()){
                JSONObject jsonObject = new JSONObject(data);
                JSONArray hits = jsonObject.getJSONArray("hits");

                for(int i = 0; i<hits.length();i++){
                    JSONObject currentProperty = hits.getJSONObject(i);

                    //Use the custom JSONObject constructor
                    Property property = new Property(currentProperty,preferences);

                    propertyList.add(property);

                }
            }

            ContextCompat.getMainExecutor(context).execute(()  -> {
                // This is where the UI code goes.
                PropertyAdapter adapter = new PropertyAdapter(context,propertyList);
                recyclerView.setAdapter(adapter);

                //If property list is empty set the error image to be visible
                if(propertyList.isEmpty()){
                    toggleResultNotFoundImage(true);
                }

            });


            //here we sett running to false
            running = false;
            success = true;

            //OPTIONAL: Save the data to the preferences

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

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, null)

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setIcon(android.R.drawable.stat_notify_error)
                    .show();
        });

    }

    synchronized public void showLoadingToast(boolean show){
        ContextCompat.getMainExecutor(context).execute(()  -> {
            // This is where the UI code goes.

            Toast t = Toast.makeText(this.context, "Loading...", Toast.LENGTH_LONG);

            if(show){
                t.show();
            }
            else{
                t.cancel();
            }

        });
    }


    synchronized private void toggleResultNotFoundImage(boolean show){
        ContextCompat.getMainExecutor(context).execute(()  -> {
            if(show){
                notFoundImage.setVisibility(View.VISIBLE);
            }
            else{
                notFoundImage.setVisibility(View.INVISIBLE);
            }



        });
    }

    private String getCustomSearchUrl(){
        String resultUrl = BackEndUrls.BASE_PROPERTY_LIST_URL;

        //Now get the search parameters
        String cityCode = preferences.getString("cityCodeSelected","");
        String propertyTypeId = preferences.getString("propertyTypeSelectedId","");
        boolean showOnlyRentalsSelected = preferences.getBoolean("showOnlyRentalsSelected",false);

        if(!cityCode.isEmpty() && !propertyTypeId.isEmpty()){
            //Continue string logic
            resultUrl += "?locationExternalIDs=" + cityCode + "&purpose=";

            if(showOnlyRentalsSelected){
                resultUrl+="for-rent";
            }
            else{
                resultUrl+="for-sale";
            }

            resultUrl += "&lang=en&sort=city-level-score&rentFrequency=monthly";

            resultUrl += "&categoryExternalID=" + propertyTypeId;


        }
        else{
              Log.e("getSearchUrl", "ERROR! The shared preferences are empty");
              return "";
        }

        //Log.i("SearchUrl",resultUrl);

        return resultUrl;

    }



}
