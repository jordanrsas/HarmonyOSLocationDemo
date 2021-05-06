package com.dtse.cjra.locationdemo;

import com.dtse.cjra.locationdemo.log.Log;
import com.dtse.cjra.locationdemo.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.bundle.IBundleManager;

public class MainAbility extends Ability {
    private static String TAG = "MainAbility";
    private static int REQUEST_CODE = 2023;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        checkLocationPermissions();
    }

    private void checkLocationPermissions() {
        Log.i(TAG, "checkLocationPermissions");
        if (verifySelfPermission("ohos.permission.LOCATION") != IBundleManager.PERMISSION_GRANTED) {
            // The application has not been granted the permission
            if (canRequestPermission("ohos.permission.LOCATION")) {
                // Check whether permission authorization can be implemented via a dialog box
                // (at initial request or when the user has not chosen the option of "don't ask again" after rejecting a previous request).
                Log.i(TAG, "PERMISSON NOT GRANTED, canRequestPermission");
                requestPermissionsFromUser(new String[]{"ohos.permission.LOCATION"}, REQUEST_CODE);
            } else {
                // Display the reason why the application requests the permission and prompt the user to grant the permission.
                Log.i(TAG, "PERMISSON NOT GRANTED, can not RequestPermission");
            }
        } else {
            // The permission has been granted.
            Log.i(TAG, "The permission has been granted.");
        }
    }

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
}
