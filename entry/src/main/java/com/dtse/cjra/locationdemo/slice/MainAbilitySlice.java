package com.dtse.cjra.locationdemo.slice;

import com.dtse.cjra.locationdemo.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;

public class MainAbilitySlice extends AbilitySlice {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        initViews();
    }

    private void initViews() {
        Button deviceLocationButton = (Button) findComponentById(ResourceTable.Id_device_location_button);
        Button geocodingButton = (Button) findComponentById(ResourceTable.Id_geocoding_button);

        deviceLocationButton.setClickedListener(listener -> present(new DeviceLocationSlice(), new Intent()));
        geocodingButton.setClickedListener(listener -> present(new GeoCodingSlice(), new Intent()));
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
