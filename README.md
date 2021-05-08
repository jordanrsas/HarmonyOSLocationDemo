# HarmonyOS Device Location, Geocoding and Reverse Geocoding Capabilities
![Project Presentation.](/assets/presentation.png "Project Presentation.")

## Introduction
People take their mobile devices wherever they go. Mobile devices have become a necessity in people's daily routines, whether it be for looking at the weather forecast, browsing news, hailing a taxi, navigating, or recording data from a workout. All these activities are so much associated with the location services on mobile devices.

With the location awareness capability offered by HarmonyOS, mobile devices will be able to obtain real-time, accurate location data. Building location awareness into your application can also lead to a better contextual experience for application users.

Your application can call location-specific APIs to obtain the location information of a mobile device for offering location-based services such as drive navigation and motion track recording.

## Working Principles
Location awareness is offered by the system as a basic service for applications. Depending on the service scenario, an application needs to initiate a location request to the system and stop the location request when the service scenario ends. In this process, the system reports the location information to the application on a real-time basis.

## Limitations and Constraints
Your application can use the location function only after the user has granted the permission and turned on the function. If the location function is off, the system will not provide the location service for any application.

Since the location information is considered sensitive, your application still needs to obtain the location access permission from the user even if the user has turned on the location function. The system will provide the location service for your application only after it has been granted the permission to access the device location information.

## Obtaining Device Location Information

### Create View Layout
The view that we will do is very simple, we will only add four buttons to represent the request for the location of the device in with the different options that the system provides.
The resulting view is as follows:
![Capture.](/assets/Capture34.PNG "Capture.")

Dynamically Request Permission

Before using basic location capabilities, check whether your application has been granted the permission to access the device location information. If not, your application needs to obtain the permission from the user.

The system provides the following location permissions:

* **ohos.permission.LOCATION**
* **ohos.permission.LOCATION_IN_BACKGROUND**

The **ohos.permission.LOCATION** permission is a must if your application needs to access the device location information.

To allow your application to access device location information, you can declare the required permissions in the config.json file of your application. The sample code is as follows:

```
{
   "module": {
       "reqPermissions": [{
           "name": "ohos.permission.LOCATION",
           "reason": "location to get weather",
           "usedScene": {
               "ability": [
               "com.dtse.cjra.weatherapp.MainAbility"
               ],
               "when": "inuse"
           }, {
           ...
           }
       ]
   }
}
```

Call the ohos.app.Context.verifySelfPermission method to check whether the permission has been granted to the application.
* If yes, the permission request process is complete.
* If no, go to the next step.

Call the canRequestPermission method to check whether the permission can be dynamically requested.
* If no, permission authorization has been permanently disabled by the user or system. In this case, you can end the permission request process.
* If yes, call the requestPermissionFromUser method to dynamically request permissions.
```
@Override
public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsFromUserResult(requestCode, permissions, grantResults);
    Log.i(TAG, "onRequestPermissionsFromUserResult");
    switch (requestCode) {
        case 2023: {
            Log.i(TAG, "onRequestPermissionsFromUserResult");
            if (grantResults.length > 0
                    && grantResults[0] == IBundleManager.PERMISSION_GRANTED) {
                // The permission is granted.
                //Note: During permission check, an interface may be considered to have no required permissions
                // due to time difference. Therefore, it is necessary to capture and process the exception thrown
                // by such an interface.
                Log.i(TAG, "The permission is granted.");
            } else {
                // The permission request is rejected.
                Log.i(TAG, "The permission request is rejected.");
            }
            return;
        }
        default:
            Log.i(TAG, "IllegalStateException");
            throw new IllegalStateException("Unexpected value: " + requestCode);
    }
}
```

Create a **Locator** instance by which you can implement all APIs related to the basic location capabilities.

Locator locator = new Locator(context);
Instantiate the **RequestParam** object. 
```
RequestParam(int priority, int timeInterval, int distanceInterval)
```

The default value is 0.0,which indicates that the location accuracy is not applied when reporting the location result.

The object provides APIs to notify the system of the location service type and the interval of reporting location information. You can use the basic location priority policies provided by the system.

| Policy  | Constant | Description |
| ------------- |:-------------:|------------- |
|Location accuracy priority	| PRIORITY_ACCURACY	| This policy mainly uses the GNSS positioning technology. In an open area, the technology can achieve the meter-level location accuracy, depending on the hardware performance of the device. However, in a shielded environment, the location accuracy may significantly decrease |
|Fast location priority	 | PRIORITY_FAST_FIRST_FIX	| This policy uses the GNSS positioning, base station positioning, WLAN positioning, and Bluetooth positioning technologies simultaneously to obtain the device location in both the indoor and outdoor scenarios. When all positioning technologies provide a location result, the system provides the most accurate location result for your application. This policy can lead to significant hardware resource consumption and power consumption.|
| Power efficiency priority	| PRIORITY_LOW_POWER	| This policy mainly uses the base station positioning, WLAN positioning, and Bluetooth positioning technologies to obtain device location in both indoor and outdoor scenarios. The location accuracy depends on the distribution of surrounding base stations, visible WLANs, and Bluetooth devices and therefore may fluctuate greatly. This policy is recommended and can reduce power consumption when your application does not require high location accuracy or when base stations, visible WLANs, and Bluetooth devices are densely distributed.|

The following instantiates the **RequestParam** object for the location accuracy priority policy:
```
RequestParam param = new RequestParam(RequestParam.PRIORITY_ACCURACY, 0, 0);
```

Then **Instantiate** the LocatorCallback object for the system to report location results.
Your application needs to implement the callback interface defined by the system. When the system successfully obtains the real-time location of a device, it will report the location result to your application through the **onLocationReport** callback. You can implement the onLocationReport callback in your application to complete the service logic.
```
private LocatorCallback requestLocationCallback = new LocatorCallback() {
    @Override
    public void onLocationReport(Location location) {
        Log.i(TAG, "onLocationReport: ");
        logText.println("Location: ", getLocationString(location));
        postScroll();
    }
 
    @Override
    public void onStatusChanged(int i) {
        Log.i(TAG, "onStatusChanged: " + i);
    }
 
    @Override
    public void onErrorReport(int i) {
        String message = "Error get location error: " + i;
        Log.e(TAG, message);
        logText.println(TAG, message);
        postScroll();
    }
};
```

Start device location:
```
private void startTracking() {
    RequestParam param = new RequestParam(RequestParam.PRIORITY_ACCURACY, 0, 0);
    locator = new Locator(getContext());
    locator.startLocating(param, requestLocationCallback);
}
```

You can call the method requestOnce if your application does not need to continuously access the device location. The system will report the real-time location to your application and automatically end the location request. 
```
private void requestLocationOnce() {
    RequestParam param = new RequestParam(RequestParam.PRIORITY_ACCURACY, 0, 0);
    locator.requestOnce(param, requestLocationCallback);
}
```

Stop device location
```
private void stopTracking() {
    locator.stopLocating(requestLocationCallback);
    logText.println("Tracking Location: ", "Stop Tracking");
    postScroll();
}
```

If your application does not need the real-time device location, it can use the last known device location cached in the system instead.
```
private void getCachedLocation() {
    Location cachedLocation = locator.getCachedLocation();
    logText.println("Cached Location: ", getLocationString(cachedLocation));
    postScroll();
}
```

The response is an object  Location that you can manage as you need. For this application you can separate some values to print them. 
```
private String getLocationString(Location location) {
    ResourceManager resourceManager = this.getResourceManager();
    StringBuilder stringBuilder = new StringBuilder();
    try {
        String lat = resourceManager.getElement(ResourceTable.String_latitudeLabel).getString();
        String lon = resourceManager.getElement(ResourceTable.String_longitudLabel).getString();
        String alt = resourceManager.getElement(ResourceTable.String_altitudeLabel).getString();
        String acc = resourceManager.getElement(ResourceTable.String_accuracyLabel).getString();
        String dir = resourceManager.getElement(ResourceTable.String_directionLabel).getString();
 
        stringBuilder.append(lat);
        stringBuilder.append(location.getLatitude());
        stringBuilder.append(SPACE).append(lon);
        stringBuilder.append(location.getLongitude());
        stringBuilder.append(SPACE).append(alt);
        stringBuilder.append(location.getAltitude());
        stringBuilder.append(SPACE).append(acc);
        stringBuilder.append(location.getAccuracy());
        stringBuilder.append(SPACE).append(dir);
        stringBuilder.append(location.getDirection());
    } catch (IOException | NotExistException | WrongTypeException e) {
        e.printStackTrace();
    }
    return stringBuilder.toString();
}
```

# Geocoding and Reverse Geocoding Capabilities

**Geocoding**, is the process of taking a text-based description of a location, such as an address or the name of a place, and returning geographic coordinates, frequently latitude/longitude pair, to identify a location on the Earth's surface.

**Reverse geocoding**, on the other hand, converts geographic coordinates to a description of a location, usually the name of a place or an addressable location.
![geocoding2.](/assets/geocoding2.png "geocoding2.")

The system offers the geocoding and reverse geocoding capabilities, making your application be able to convert coordinates into location information or vice versa. The geocoding information describes a location using several attributes, including the country, administrative region, street, house number, and address, etc.

## Create View Layout
The view that we will do is very simple, we will only add some fields to enter latitude and longitude and that the system returns us an address or name of a point of interest.

Then we add a field to enter an address and we can obtain a list of GeoAddress objects.
The view that we obtain will be the following
![Capture78.](/assets/Capture78.PNG "Capture78.")

First you have to create a GeoConvert instance by which you can implement all APIs related to the geocoding and reverse geocoding conversion capabilities.

GeoConvert geoConvert = new GeoConvert();
You can use GeoConvert(Locale locale) to create a GeoConvert instance based on specified parameters, such as the language and region.

Then call getAddressFromLocation(double latitude, double longitude, int maxItems) to convert coordinates to location information.
```
private void getAddress(String lat, String lon) {
    GeoConvert geoConvert = new GeoConvert();
    try {
        List<GeoAddress> addressFromLocation = geoConvert
                .getAddressFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), MAX_ITEMS);
 
        printGeoAddressList(addressFromLocation);
    } catch (IOException e) {
        e.printStackTrace();
        logText.println("getAddress printStackTrace", e.getMessage());
        postScroll();
    }
}
```

Call getAddressFromLocationName(String description, int maxItems) to convert location information to coordinates
```
private void getAddressByLocation(String locationName) {
    GeoConvert geoConvert = new GeoConvert();
    try {
        List<GeoAddress> addressFromLocationName = geoConvert
                .getAddressFromLocationName(locationName, MAX_ITEMS);
 
        printGeoAddressList(addressFromLocationName);
 
    } catch (IOException e) {
        e.printStackTrace();
        logText.println("getAddressByLocation printStackTrace", e.getMessage());
        postScroll();
    }
}
```
Your application can obtain the GeoAddress list that matches the specified location information and read coordinates from it.
```
private String getAddressString(GeoAddress address) {
    ResourceManager resourceManager = this.getResourceManager();
    StringBuilder stringBuilder = new StringBuilder();
 
    try {
        String addressLabel = resourceManager.getElement(ResourceTable.String_addressInfoLabel).getString();
        String place = resourceManager.getElement(ResourceTable.String_placeNameLabel).getString();
        String country = resourceManager.getElement(ResourceTable.String_countyLabel).getString();
        String admin = resourceManager.getElement(ResourceTable.String_adminAreaLabel).getString();
        String zipCode = resourceManager.getElement(ResourceTable.String_zipCodeLabel).getString();
 
        stringBuilder.append(addressLabel).append(SPACE);
        stringBuilder.append(place);
        stringBuilder.append(address.getPlaceName());
        stringBuilder.append(SPACE).append(country);
        stringBuilder.append(address.getCountryName());
        stringBuilder.append(SPACE).append(admin);
        stringBuilder.append(address.getAdministrativeArea());
        stringBuilder.append(SPACE).append(zipCode);
        stringBuilder.append(address.getPostalCode());
        stringBuilder.append(RETURN).append(LINE_FEED);
    } catch (IOException | NotExistException | WrongTypeException e) {
        e.printStackTrace();
    }
    return stringBuilder.toString();
}
```

The result of Geociding and Reverse Geocoding is:

![resultreversegeocoding.](/assets/resultreversegeocoding.PNG "resultreversegeocoding.")

You can see the result of the application running next:


![example_gif.](/assets/example_gif.gif "example_gif.")

# Tips and Tricks
To navigate between Slices when we use the AbilitySlice we just have to invoke the method present(ohos.aafwk.ability.AbilitySlice, ohos.aafwk.content.Intent) to present a new ability slice, and transfer customized parameters using Intent. Example code:
```
Button button = new Button(this);
   button.setClickedListener(listener -> {
       DeviceLocationSlice targetSlice = new DeviceLocationSlice();
       Intent intent = new Intent();
       intent.setParam("value", 10);
       present(targetSlice, intent);
   });
```

The GeoConvert instance needs to access backend services to obtain information. Therefore, before performing the following steps, ensure that your device is connected to the network

# References
RequestParam Api Reference: (https://developer.harmonyos.com/en/docs/documentation/doc-references/requestparam-0000001054238901)

Permission Development Guidelines: (https://developer.harmonyos.com/en/docs/documentation/doc-guides/security-permissions-guidelines-0000000000029886#EN-US_TOPIC_0000001072906209__section7431121314439)

HarmonyOS Location: (https://developer.harmonyos.com/en/docs/documentation/doc-guides/device-location-overview-0000000000031896)

Address Geocoding: (https://en.wikipedia.org/wiki/Address_geocoding)

AbilitySlice: (https://developer.harmonyos.com/en/docs/documentation/doc-references/abilityslice-0000001054678680)
