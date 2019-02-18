package io.proximi.react;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.proximi.proximiiolibrary.ProximiioAPI;
import io.proximi.proximiiolibrary.ProximiioGeofence;
import io.proximi.proximiiolibrary.ProximiioListener;
import io.proximi.proximiiolibrary.ProximiioOptions;

public class RNProximiioReactModule extends ReactContextBaseJavaModule implements LifecycleEventListener, ActivityEventListener {
    private ProximiioOptions options;
    private ProximiioAPI proximiioAPI;
    private DeviceEventManagerModule.RCTDeviceEventEmitter emitter;

    private final ReactApplicationContext reactContext;

    private static final String TAG = "ProximiioReact";

    private static final String EVENT_POSITION = "PROXIMIIO_EVENT_POSITION";
    private static final String EVENT_GEOFENCE_ENTER = "PROXIMIIO_EVENT_GEOFENCE_ENTER";
    private static final String EVENT_GEOFENCE_EXIT = "PROXIMIIO_EVENT_GEOFENCE_EXIT";

    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_ACCURACY = "accuracy";
    private static final String ARG_GEOFENCE = "geofence";
    private static final String ARG_DWELL_TIME = "dwellTime";

    RNProximiioReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        reactContext.addActivityEventListener(this);
        options = new ProximiioOptions();
    }

    @ReactMethod
    public void setNotificationMode(Object mode) {
        options.setNotificationMode(ProximiioOptions.NotificationMode.fromInt((int)mode));
    }

    @ReactMethod
    public void setNotificationTitle(String title) {
        options.setNotificationTitle(title);
    }

    @ReactMethod
    public void setNotificationText(String text) {
        options.setNotificationText(text);
    }
    
    @ReactMethod
    public void init(String auth) {
        if (proximiioAPI == null) {
            proximiioAPI = new ProximiioAPI(TAG, reactContext, options);

            proximiioAPI.setListener(new ProximiioListener() {
                @Override
                public void position(double lat, double lon, double accuracy) {
                    WritableMap map = Arguments.createMap();
                    map.putDouble(ARG_LATITUDE, lat);
                    map.putDouble(ARG_LONGITUDE, lon);
                    map.putDouble(ARG_ACCURACY, accuracy);
                    sendEvent(EVENT_POSITION, map);
                }

                @Override
                public void geofenceEnter(ProximiioGeofence geofence) {
                    WritableMap map = Arguments.createMap();
                    map.putString(ARG_GEOFENCE, geofence.getJSON());
                    sendEvent(EVENT_GEOFENCE_ENTER, map);
                }

                @Override
                public void geofenceExit(ProximiioGeofence geofence, @Nullable Long dwellTime) {
                    WritableMap map = Arguments.createMap();
                    map.putString(ARG_GEOFENCE, geofence.getJSON());
                    if (dwellTime != null) {
                        map.putInt(ARG_DWELL_TIME, dwellTime.intValue());
                    }
                    else {
                        map.putNull(ARG_DWELL_TIME);
                    }
                    sendEvent(EVENT_GEOFENCE_EXIT, map);
                }
            });

            proximiioAPI.setAuth(auth, true);

            trySetActivity();

            proximiioAPI.onStart();
        }
    }

    @ReactMethod
    public void destroyService(boolean eraseData) {
        if (proximiioAPI != null) {
            proximiioAPI.destroyService(eraseData);
            proximiioAPI = null;
        }
    }

    @Override
    public void onHostResume() {
        if (proximiioAPI != null) {
            trySetActivity();
            proximiioAPI.onStart();
        }
    }

    @Override
    public void onHostPause() {
        if (proximiioAPI != null) {
            proximiioAPI.onStop();
        }
    }

    @Override
    public void onHostDestroy() {
        if (proximiioAPI != null) {
            proximiioAPI.destroy();
        }
    }

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (proximiioAPI != null) {
            proximiioAPI.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Log.d("ReactNative", "ANDROID GOT PERMISSION RESULT");
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (proximiioAPI != null) {
            proximiioAPI.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public String getName() {
        return "ProximiioReact";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<>();
        for (ProximiioOptions.NotificationMode mode : ProximiioOptions.NotificationMode.values()) {
            constants.put("NOTIFICATION_MODE_" + mode.toString(), mode.toInt());
        }
        constants.put("EVENT_POSITION", EVENT_POSITION);
        constants.put("EVENT_GEOFENCE_ENTER", EVENT_GEOFENCE_ENTER);
        constants.put("EVENT_GEOFENCE_EXIT", EVENT_GEOFENCE_EXIT);
        constants.put("ARG_LATITUDE", ARG_LATITUDE);
        constants.put("ARG_LONGITUDE", ARG_LONGITUDE);
        constants.put("ARG_ACCURACY", ARG_ACCURACY);
        constants.put("ARG_GEOFENCE", ARG_GEOFENCE);
        constants.put("ARG_DWELL_TIME", ARG_DWELL_TIME);
        return constants;
    }

    private void sendEvent(String event, Object data) {
        if (emitter == null) {
            emitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        }
        emitter.emit(event, data);
    }

    private void trySetActivity() {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            proximiioAPI.setActivity(activity);
        }
        Log.d("ReactNative", "Activity available: " + (activity != null));
    }
}
