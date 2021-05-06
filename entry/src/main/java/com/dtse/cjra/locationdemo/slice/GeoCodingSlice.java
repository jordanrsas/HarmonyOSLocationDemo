package com.dtse.cjra.locationdemo.slice;

import com.dtse.cjra.locationdemo.ResourceTable;
import com.dtse.cjra.locationdemo.log.LogView;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.ScrollView;
import ohos.agp.components.TextField;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;
import ohos.location.GeoAddress;
import ohos.location.GeoConvert;

import java.io.IOException;
import java.util.List;

public class GeoCodingSlice extends AbilitySlice {

    private LogView logText;
    private ScrollView scrollView;
    private static char SPACE = 32;
    private static char LINE_FEED = 10;
    private static char RETURN = 13;
    private static int MAX_ITEMS = 5;
    private static long DELAY_TIME_MILLIS = 50;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_geocoding_slice);

        Button getAddress = (Button) findComponentById(ResourceTable.Id_get_address);
        Button getAddressByLocation = (Button) findComponentById(ResourceTable.Id_get_address_from_location);

        TextField locationName = (TextField) findComponentById(ResourceTable.Id_text_field_location_name);
        TextField latitude = (TextField) findComponentById(ResourceTable.Id_text_field_lat);
        TextField longitude = (TextField) findComponentById(ResourceTable.Id_text_field_lon);

        logText = (LogView) findComponentById(ResourceTable.Id_log_text);
        scrollView = (ScrollView) findComponentById(ResourceTable.Id_scroll_view);

        getAddress.setClickedListener(listener -> getAddress(latitude.getText(), longitude.getText()));
        getAddressByLocation.setClickedListener(listener -> getAddressByLocation(locationName.getText()));
    }

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

    private void printGeoAddressList(List<GeoAddress> geoAddressList) {
        if (geoAddressList.isEmpty()) {
            logText.println("List<GeoAddress>", "Empty GeoAddress List");
        } else {
            for (GeoAddress address : geoAddressList) {
                logText.println("Address:", getAddressString(address));
            }
        }
        postScroll();
    }

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

    private void postScroll() {
        TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
        globalTaskDispatcher.delayDispatch(() -> scrollView.scrollTo(0, scrollView.getBottom()), DELAY_TIME_MILLIS);
    }
}
