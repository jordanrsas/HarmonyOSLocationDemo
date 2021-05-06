package com.dtse.cjra.locationdemo.slice;

import com.dtse.cjra.locationdemo.ResourceTable;
import com.dtse.cjra.locationdemo.log.Log;
import com.dtse.cjra.locationdemo.log.LogView;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.ScrollView;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.global.resource.NotExistException;
import ohos.global.resource.ResourceManager;
import ohos.global.resource.WrongTypeException;
import ohos.location.Location;
import ohos.location.Locator;
import ohos.location.LocatorCallback;
import ohos.location.RequestParam;

import java.io.IOException;

public class DeviceLocationSlice extends AbilitySlice {

    private static String TAG = "DeviceLocationSlice";
    private static char SPACE = 32;
    private static long DELAY_TIME_MILLIS = 50;

    private LogView logText;
    private ScrollView scrollView;

    private Locator locator;

    private LocatorCallback requestLocationCallback = new LocatorCallback() {
        @Override
        public void onLocationReport(Location location) {
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

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_device_location_slice);
        locator = new Locator(getContext());
        initViews();
    }

    private void initViews() {
        Button requestOnce = (Button) findComponentById(ResourceTable.Id_request_once_button);
        Button startTracking = (Button) findComponentById(ResourceTable.Id_start_tracking_button);
        Button stopTracking = (Button) findComponentById(ResourceTable.Id_stop_tracking_button);
        Button cachedLocation = (Button) findComponentById(ResourceTable.Id_get_cached_location);

        logText = (LogView) findComponentById(ResourceTable.Id_log_text);
        scrollView = (ScrollView) findComponentById(ResourceTable.Id_scroll_view);

        requestOnce.setClickedListener(listener -> requestLocationOnce());
        startTracking.setClickedListener(listener -> startTracking());
        stopTracking.setClickedListener(listener -> stopTracking());
        cachedLocation.setClickedListener(listener -> getCachedLocation());
    }

    private void requestLocationOnce() {
        RequestParam param = new RequestParam(RequestParam.PRIORITY_ACCURACY, 0, 0);
        locator.requestOnce(param, requestLocationCallback);
    }

    private void startTracking() {
        RequestParam param = new RequestParam(RequestParam.PRIORITY_ACCURACY, 0, 0);
        locator = new Locator(getContext());
        locator.startLocating(param, requestLocationCallback);

    }

    private void stopTracking() {
        locator.stopLocating(requestLocationCallback);
        logText.println("Tracking Location: ", "Stop Tracking");
        postScroll();
    }

    private void getCachedLocation() {
        Location cachedLocation = locator.getCachedLocation();
        logText.println("Cached Location: ", getLocationString(cachedLocation));
        postScroll();
    }

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

    private void postScroll() {
        TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
        globalTaskDispatcher.delayDispatch(() -> scrollView.scrollTo(0, scrollView.getBottom()), DELAY_TIME_MILLIS);
    }
}
