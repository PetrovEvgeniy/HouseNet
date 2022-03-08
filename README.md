# HouseNet
HouseNet is my latest Android project, of the Mobile Application Development course, held in the third semester of Technische Hochschule Ulm. It was an OPTIONAL project and it was rated with 11/12 bonus points. (The exam was a grand total of 90 points).

## Main Functionality 
The general purpose of the project is an Android application, which gives the opportunity to the user to see all available listed real estate properties. Additionally, filter them out (search) by their property type and city, in which they are located.

## Other Functionalities
Another feature is to see more details about each individual property, for example, its description, price, picture, location, and telephone number for contact.

For better UX there exists a functionality to save and store individual properties into a list and display them to another "Favorites" view page.

Finally, there is a built-in error handling (in case of network error for example, or if no search results are found)

## API Used
Bayut API (real publically available Real Estate data for properties located in the United Arab Emirates)

LIMIT 500 requests per month (if you want more change the authorization token in the BackEndUrls.java file, or pay the premium RapidAPI subscription)
