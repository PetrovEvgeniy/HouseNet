package de.thu.housenet.applicationData.categories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.thu.housenet.applicationData.locations.City;

public class Categories {
    private static final PropertyType[] PROPERTY_TYPES = {
            new PropertyType("Apartment", 4),
            new PropertyType("Office", 5),
            new PropertyType("Penthouses", 18),
            new PropertyType("Townhouses", 16),
            new PropertyType("Villa Compound", 19),
            new PropertyType("Residential Floor", 12),
            new PropertyType("Residential Building", 17),
            new PropertyType("Shop", 6),
            new PropertyType("Warehouse", 7),
            new PropertyType("Showroom", 24),
    };

    private final static Map<String, Integer> PROPERTIES_MAP = new HashMap<>();

    private final static String[] PROPERTIES_LIST;

    static {
        for (PropertyType p : PROPERTY_TYPES) {
            PROPERTIES_MAP.put(p.getName(),p.getId());
        }
        PROPERTIES_LIST = new String[PROPERTIES_MAP.size()];

        PROPERTIES_MAP.keySet().toArray(PROPERTIES_LIST);
        Arrays.sort(PROPERTIES_LIST);

    }


    public String[] getPropertyTypeNames() {
        return PROPERTIES_LIST;
    }

    public int getPropertyTypeId(String propertyTypeName){
        return PROPERTIES_MAP.get(propertyTypeName);
    }

    public String getPropertyTypeIdString(String propertyTypeName){
        return Integer.toString(PROPERTIES_MAP.get(propertyTypeName));
    }
}