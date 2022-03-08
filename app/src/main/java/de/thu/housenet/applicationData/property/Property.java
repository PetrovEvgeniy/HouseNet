package de.thu.housenet.applicationData.property;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.json.JSONObject;

public class Property {
    private String externalID;
    private String title;
    private String type;
    private String purpose;
    private String description;
    private String imageUrl;
    private double area;
    private int bedrooms;
    private int baths;
    private double price;
    private String contactName;
    private String phoneNumber;
    private String location;

    //Constructor
    public Property(String externalID,
                    String title,
                    String type,
                    String purpose,
                    String description,
                    String imageUrl,
                    double area,
                    int bedrooms,
                    int baths,
                    double price,
                    String contactName,
                    String phoneNumber,
                    String location) {
        this.externalID = externalID;
        this.title = title;
        this.type = type;
        this.purpose = purpose;
        this.description = description;
        this.imageUrl = imageUrl;
        this.area = area;
        this.bedrooms = bedrooms;
        this.baths = baths;
        this.price = price;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
        this.location = location;
    }

    //Second constructor (JSONObject)
    public Property(JSONObject jsonObject, SharedPreferences preferences){
        try {
            String externalId = jsonObject.getString("externalID");
            String title = jsonObject.getString("title");
            String type = preferences.getString("propertyTypeSelectedId","4");
            String purpose = jsonObject.getString("purpose");

            JSONObject coverPhoto = jsonObject.getJSONObject("coverPhoto");
            String imageUrl = coverPhoto.getString("url");

            double area = jsonObject.getDouble("area");
            int beds = jsonObject.getInt("rooms");
            int baths = jsonObject.getInt("baths");
            double price = jsonObject.getDouble("price");

            String contactName = jsonObject.getString("contactName");

            JSONObject geography = jsonObject.getJSONObject("geography");
            String lat = Double.toString(geography.getDouble("lat"));
            String lng = Double.toString(geography.getDouble("lng"));

            String location = lat + ", " + lng;

            //Initially empty (it will be populated via additional API request
            String phoneNumber  = "";
            String description = "";

            JSONObject phone = jsonObject.getJSONObject("phoneNumber");

            //The description and the phoneNumber may be empty
            try{
                description = jsonObject.getString("description");
                phoneNumber  = phone.getString("mobile");
                if(phoneNumber.isEmpty()){
                    phoneNumber = phone.getString("phone");
                }
            }
            catch (Exception e){
                //No phone number or description, do nothing...
            }


            //Set all values
            this.externalID = externalId;
            this.title = title;
            this.type = type;
            this.purpose = purpose;
            this.description = description;
            this.imageUrl = imageUrl;
            this.area = area;
            this.bedrooms = beds;
            this.baths = baths;
            this.price = price;
            this.contactName = contactName;
            this.phoneNumber = phoneNumber;
            this.location = location;
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    //Getters
    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getExternalID() {
        return externalID;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getArea() {
        return area;
    }

    public int getBedRooms() {
        return bedrooms;
    }

    public int getBaths() {
        return baths;
    }

    public double getPrice() {
        return price;
    }

    public String getContactName() {
        return contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    public String toString(){
        return String.format("%s, %s %s %s %s %s %s %s %s %d %d %f %f",externalID,title,type,purpose,description,imageUrl,contactName,phoneNumber,location,bedrooms,baths,area,price);
    }
}
