package de.thu.housenet.applicationData.locations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.thu.housenet.applicationData.locations.City;

public class Locations {
    private static final City[] CITIES = {
        new City("Abu Dhabi","6020"),
        new City("Al Ain","6057"),
        new City("Ajman","5385"),
        new City("Dubai","5002"),
        new City("Fujairah","6542"),
        new City("Sharjah","5351"),
        new City("Umm Al Quwain","5544"),
        new City("Ras Al Khaimah","5509"),
    };


    private final static Map<String, String> CITIES_MAP = new HashMap<>();

    private final static String[] CITIES_LIST;

    static {
        for (City c : CITIES) {
            CITIES_MAP.put(c.getName(), c.getCode());
        }
        CITIES_LIST = new String[CITIES_MAP.size()];

        CITIES_MAP.keySet().toArray(CITIES_LIST);
        Arrays.sort(CITIES_LIST);

    }


    public String[] getCityNames(){
        return CITIES_LIST;
    }

    public String getCityCode(String cityName){
        return CITIES_MAP.get(cityName);
    }
}
