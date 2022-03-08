package de.thu.housenet.applicationData;

public class BackEndUrls {
    public static final String BASE_PROPERTY_LIST_URL ="https://bayut.p.rapidapi.com/properties/list";
    public static final String BASE_PROPERTY_DETAIL_URL ="https://bayut.p.rapidapi.com/properties/detail";


    //SUBJECT OF CHANGE... (possible authentication in the future)
    public static final String X_HOST = "bayut.p.rapidapi.com";

    //YOU MAY CHANGE THIS WITH YOUR OWN (https://rapidapi.com/apidojo/api/bayut/) register here to get one...
    //IMPORTANT! the key gives you only limited amount of requests (500 per month)
    //If you want to send more than 500 requests either get new key by creating new account or purchase premium membership
    public static final String X_KEY = "e96e84d042mshc08560d787e949cp14543cjsnf23cadc8e046";
}
