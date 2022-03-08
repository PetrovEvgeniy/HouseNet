package de.thu.housenet.applicationData.categories;

public class PropertyType {
    private final String name;
    private final int id;

    public PropertyType(String name,int id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getIdString() {return Integer.toString(id);}
}
