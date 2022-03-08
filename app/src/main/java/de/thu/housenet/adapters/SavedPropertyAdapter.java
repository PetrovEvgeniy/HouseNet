package de.thu.housenet.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.ArrayList;

import de.thu.housenet.R;
import de.thu.housenet.activities.PropertyDetailActivity;
import de.thu.housenet.applicationData.property.Property;

public class SavedPropertyAdapter extends RecyclerView.Adapter<SavedPropertyAdapter.SavedPropertyHolder> {

    private Context context;
    private ArrayList<Property> propertyList;


    public SavedPropertyAdapter(Context context, ArrayList<Property> savedProperties){
        this.context = context;
        propertyList = savedProperties;
    }

    @NonNull
    @Override
    public SavedPropertyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_saved_listing_item, parent, false);
        return new SavedPropertyHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull SavedPropertyHolder holder, int position) {

        Property property = propertyList.get(position);


        String priceInEuroString = getPropertyPriceText(property.getPrice(),property.getPurpose());
        String areaInSquareMetersString = getPropertySquareMetersText(property.getArea());

        holder.titleView.setText(property.getTitle());
        holder.priceView.setText(priceInEuroString);
        holder.bedCountView.setText(Integer.toString(property.getBedRooms()));
        holder.bathCountView.setText(Integer.toString(property.getBaths()));
        holder.areaView.setText(areaInSquareMetersString);

        //Load the property image with an transition effect (using external library)
        Glide.with(context)
                .load(property.getImageUrl())
                .transition(withCrossFade(new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()))
                .into(holder.propertyCoverImageView);

        //Attach event listener on click

        holder.propertySavedConstraintLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PropertyDetailActivity.class);
                //Put the propertyExternalId
                intent.putExtra("SELECTED_PROPERTY_EXTERNAL_ID",property.getExternalID());
                intent.putExtra("SELECTED_PROPERTY_TITLE",property.getTitle());
                intent.putExtra("SELECTED_PROPERTY_TYPE",property.getType());
                intent.putExtra("SELECTED_PROPERTY_PURPOSE",property.getPurpose());
                intent.putExtra("SELECTED_PROPERTY_IMAGE_URL",property.getImageUrl());
                intent.putExtra("SELECTED_PROPERTY_CONTACT_NAME",property.getContactName());
                intent.putExtra("SELECTED_PROPERTY_LOCATION",property.getLocation());

                intent.putExtra("SELECTED_PROPERTY_PRICE",priceInEuroString);
                intent.putExtra("SELECTED_PROPERTY_AREA",areaInSquareMetersString);
                intent.putExtra("SELECTED_PROPERTY_BEDS",Integer.toString(property.getBedRooms()));
                intent.putExtra("SELECTED_PROPERTY_BATHS",Integer.toString(property.getBaths()));

                context.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public class SavedPropertyHolder extends RecyclerView.ViewHolder{

        ConstraintLayout propertySavedConstraintLayout;
        ImageView propertyCoverImageView;
        TextView titleView,priceView,bedCountView,bathCountView,areaView;

        public SavedPropertyHolder(View itemView){
            super(itemView);

            propertySavedConstraintLayout = itemView.findViewById(R.id.savedPropertyListingItem);
            titleView = itemView.findViewById(R.id.titleTextView);
            propertyCoverImageView = itemView.findViewById(R.id.coverImageView);
            priceView = itemView.findViewById(R.id.propertyPriceView);
            bedCountView = itemView.findViewById(R.id.bedroomsCountView);
            bathCountView = itemView.findViewById(R.id.bathroomsCountView);
            areaView = itemView.findViewById(R.id.squareMetersView);


        }
    }

    public String getPropertyPriceText(double dirhamPrice, String purpose){

        @SuppressLint("DefaultLocale") String resultingString = String.format("%.0f",(dirhamPrice * 0.24)) + "€";
        if(purpose.equals("for-rent")){
            resultingString += " / month";
        }

        return resultingString;
    }

    @SuppressLint("DefaultLocale")
    public String getPropertySquareMetersText(double squareMeters){
        return String.format("%.2f",squareMeters);
    }
}
