package io.proximi.react;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.proximi.proximiiolibrary.ProximiioAPI;
import io.proximi.proximiiolibrary.ProximiioApplication;
import io.proximi.proximiiolibrary.ProximiioArea;
import io.proximi.proximiiolibrary.ProximiioBLEDevice;
import io.proximi.proximiiolibrary.ProximiioEddystone;
import io.proximi.proximiiolibrary.ProximiioFloor;
import io.proximi.proximiiolibrary.ProximiioGeofence;
import io.proximi.proximiiolibrary.ProximiioIBeacon;
import io.proximi.proximiiolibrary.ProximiioInput;
import io.proximi.proximiiolibrary.ProximiioListener;
import io.proximi.proximiiolibrary.ProximiioOptions;

import static io.proximi.proximiiolibrary.ProximiioListener.LoginError.LOGIN_FAILED;

public class RNProximiioReactModule extends ReactContextBaseJavaModule implements LifecycleEventListener, ActivityEventListener {
    private ProximiioOptions options;
    private ProximiioAPI proximiioAPI;
    private DeviceEventManagerModule.RCTDeviceEventEmitter emitter;

    private final ReactApplicationContext reactContext;

    private static final String TAG = "ProximiioReact";

    private static final String EVENT_POSITION_UPDATED = "ProximiioPositionUpdated";
    private static final String EVENT_FLOOR_CHANGED = "ProximiioFloorChanged";
    private static final String EVENT_GEOFENCE_ENTER = "ProximiioEnteredGeofence";
    private static final String EVENT_GEOFENCE_EXIT = "ProximiioExitedGeofence";
    private static final String EVENT_PRIVACY_ZONE_ENTER = "ProximiioEnteredPrivacyZone";
    private static final String EVENT_PRIVACY_ZONE_EXIT = "ProximiioExitedPrivacyZone";
    private static final String EVENT_FOUND_IBEACON = "ProximiioFoundIBeacon";
    private static final String EVENT_LOST_IBEACON = "ProximiioLostIBeacon";
    private static final String EVENT_FOUND_EDDYSTONE = "ProximiioFoundEddystoneBeacon";
    private static final String EVENT_LOST_EDDYSTONE = "ProximiioLostEddystoneBeacon";

    private Promise authPromise;
    private ProximiioFloor lastFloor;
    private List<ProximiioGeofence> geofences = new ArrayList<ProximiioGeofence>();

    RNProximiioReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        reactContext.addActivityEventListener(this);
        options = new ProximiioOptions();
    }

    @ReactMethod
    public void setNotificationMode(int mode) {
        options.setNotificationMode(ProximiioOptions.NotificationMode.fromInt(mode));
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
    public void setNotificationIcon(String icon) {
        int identifier = reactContext.getResources().getIdentifier(icon, "drawable", reactContext.getPackageName());
        if (identifier != 0) {
            options.setNotificationIcon(identifier);
        }
    }


    @ReactMethod
    public void updateOptions() {
        if (proximiioAPI != null) {
            proximiioAPI.updateNotificationOptions(options);
        }
    }

    @ReactMethod
    public void setNativeAccuracy(int accuracy) {
        if (proximiioAPI != null) {
            proximiioAPI.setNativeAccuracy(ProximiioApplication.NativeAccuracy.fromInt(accuracy));
        }
    }

    @ReactMethod
    public void requestPermissions() {

    }

    @ReactMethod
    public void currentFloor(Promise promise) {
        if (lastFloor == null) {
            promise.resolve(null);
        } else {
            promise.resolve(convertFloor(lastFloor));
        }
    }

    @ReactMethod
    public void currentGeofeDnces(Promise promise) {
        promise.resolve(geofences);
    }

    private WritableMap convertLocation(double lat, double lon, double accuracy, @Nullable ProximiioGeofence.EventType type) {
        WritableMap map = Arguments.createMap();
        map.putDouble("lat", lat);
        map.putDouble("lng", lon);
        map.putDouble("accuracy", accuracy);

        if (type != null) {
            String typeString = "";
            if (type == ProximiioGeofence.EventType.NATIVE) {
                typeString = "native";
            } else if (type == ProximiioGeofence.EventType.BEACON) {
                typeString = "beacon";
            } else if (type == ProximiioGeofence.EventType.TRILATERATED) {
                typeString = "trilaterated";
            } else if (type == ProximiioGeofence.EventType.INDOORATLAS) {
                typeString = "indooratlas";
            } else if (type == ProximiioGeofence.EventType.DISCONNECT) {
                typeString = "disconnect";
            } else if (type == ProximiioGeofence.EventType.CUSTOM) {
                typeString = "custom";
            } else {
                typeString = "unknown";
            }
            map.putString("sourceType", typeString);
        }
        return map;
    }

    private WritableMap convertArea(ProximiioArea area) {
        WritableMap map = Arguments.createMap();
        map.putString("id", area.getID());
        map.putString("name", area.getName());
        map.putMap("area", convertLocation(area.getLat(), area.getLon(), area.getRadius(), null));
        map.putDouble("radius", area.getRadius());
        map.putBoolean("isPolygon", area.getPolygon() != null);

        if (area.getPolygon() != null) {
            WritableArray polygon = Arguments.createArray();
            for (int i = 0; i < area.getPolygon().length; i++) {
                WritableArray coords = Arguments.createArray();
                coords.pushDouble(area.getPolygon()[i][0]);
                coords.pushDouble(area.getPolygon()[i][1]);
                polygon.pushArray(coords);
            }
            map.putArray("polygon", polygon);
        }

        return map;
    }

    private WritableMap convertDevice(ProximiioBLEDevice device) {
        WritableMap map = Arguments.createMap();

        WritableMap inputMap = Arguments.createMap();
        ProximiioInput input = device.getInput();

        if (device.getType() == ProximiioInput.InputType.IBEACON) {
            ProximiioIBeacon beacon = (ProximiioIBeacon)device;
            map.putString("uuid", beacon.getUUID());
            map.putString("type", "ibeacon");
            map.putString("identifier", beacon.getUUID()+"/" + beacon.getMajor() + "/" + beacon.getMinor());
            map.putInt("major", beacon.getMajor());
            map.putInt("minor", beacon.getMinor());

            if (beacon != null && beacon.getDistance() != null) {
                map.putDouble("accuracy", beacon.getDistance());
            } else {
                map.putDouble("accuracy", 50.0);
            }
            inputMap.putString("type", "ibeacon");
        } else if (device.getType() == ProximiioInput.InputType.EDDYSTONE) {
            ProximiioEddystone beacon = (ProximiioEddystone)device;
            map.putString("type", "eddystone");
            map.putString("namespace", beacon.getNamespace());
            map.putString("instanceId", beacon.getInstanceID());
            map.putString("identifier", beacon.getNamespace() + "/" + beacon.getInstanceID());
            inputMap.putString("type", "eddystone");
        } else if (device.getType() == ProximiioInput.InputType.GENERIC_BLE ||
                   device.getType() == ProximiioInput.InputType.CUSTOM) {
            inputMap.putString("type", "custom");
        }

        if (input != null) {
            inputMap.putString("id", input.getID());
            inputMap.putString("name", input.getName());
        }

        map.putMap("input", inputMap);
        return map;
    }

    private Object convertFloor(ProximiioFloor floor) {
        WritableMap map = Arguments.createMap();
        if (floor != null) {
            map.putString("id", floor.getID());
            map.putString("name", floor.getName());
            if (floor.getFloorNumber() != null) {
                map.putInt("level", floor.getFloorNumber());
            } else {
                map.putInt("level", 0);
            }

            if (floor.getPlace() != null) {
                map.putString("place_id", floor.getPlace().getID());
            }

            if (floor.getFloorPlanURL() != null) {
                map.putString("floorplan", floor.getFloorPlanURL());
            }

            if (floor.getAnchors() != null) {
                WritableArray anchors = Arguments.createArray();
                for (int i = 0; i < floor.getAnchors().length; i++) {
                    WritableArray coords = Arguments.createArray();
                    coords.pushDouble(floor.getAnchors()[i][0]);
                    coords.pushDouble(floor.getAnchors()[i][1]);
                    anchors.pushArray(coords);
                }
                map.putArray("anchors", anchors);
            } else {
                map.putArray("anchors", Arguments.createArray());
            }
        }
        return map;
    }

    @ReactMethod
    public void authWithToken(String auth, Promise promise) {
        authPromise = promise;
        if (proximiioAPI == null) {
            proximiioAPI = new ProximiioAPI(TAG, reactContext, options);
            proximiioAPI.setListener(new ProximiioListener() {
                @Override
                public void positionExtended(double lat, double lon, double accuracy, ProximiioGeofence.EventType type) {
                    sendEvent(EVENT_POSITION_UPDATED, convertLocation(lat, lon, accuracy, type));
                }

                @Override
                public void changedFloor(@Nullable ProximiioFloor floor) {
                    lastFloor = floor;
                    sendEvent(EVENT_FLOOR_CHANGED, convertFloor(floor));
                }

                @Override
                public void geofenceEnter(ProximiioGeofence geofence) {
                    if (!geofences.contains(geofence)) {
                        geofences.add(geofence);
                    }
                    sendEvent(EVENT_GEOFENCE_ENTER, convertArea(geofence));
                }

                @Override
                public void geofenceExit(ProximiioGeofence geofence, @Nullable Long dwellTime) {
                    WritableMap map = convertArea(geofence);
                    if (dwellTime != null) {
                        map.putInt("dwellTime", dwellTime.intValue());
                    } else {
                        map.putNull("dwellTime");
                    }
                    if (geofences.contains(geofence)) {
                        geofences.remove(geofence);
                    }
                    sendEvent(EVENT_GEOFENCE_EXIT, map);
                }

                @Override
                public void privacyZoneEnter(ProximiioArea area) {
                    sendEvent(EVENT_PRIVACY_ZONE_ENTER, convertArea(area));
                }

                @Override
                public void privacyZoneExit(ProximiioArea area) {
                    sendEvent(EVENT_PRIVACY_ZONE_EXIT, convertArea(area));
                }

                @Override
                public void foundDevice(ProximiioBLEDevice device, boolean registered) {
                    WritableMap map = convertDevice(device);
                    if (device.getType() == ProximiioInput.InputType.IBEACON) {
                        sendEvent(EVENT_FOUND_IBEACON, map);
                    } else if (device.getType() == ProximiioInput.InputType.EDDYSTONE) {
                        sendEvent(EVENT_FOUND_EDDYSTONE, map);
                    }
                }

                @Override
                public void lostDevice(ProximiioBLEDevice device, boolean registered) {
                    WritableMap map = convertDevice(device);
                    if (device.getType() == ProximiioInput.InputType.IBEACON) {
                        sendEvent(EVENT_LOST_IBEACON, map);
                    } else if (device.getType() == ProximiioInput.InputType.EDDYSTONE) {
                        sendEvent(EVENT_LOST_EDDYSTONE, map);
                    }
                }

                @Override
                public void loggedIn(boolean online, String auth) {
                    if (authPromise != null && online) {
                        WritableMap map = Arguments.createMap();
                        map.putString("visitorId", proximiioAPI.getVisitorID());
                        authPromise.resolve(map);
                    }
                }

                @Override
                public void loginFailed(LoginError error) {
                    if (authPromise != null) {
                        if (error == LOGIN_FAILED) {
                            authPromise.reject("403", "Proximi.io authorization failed");
                        } else {
                            authPromise.reject("404", "Proximi.io connection failed");
                        }
                    }
                }
            });

            proximiioAPI.setAuth(auth, true);
            trySetActivity();
            proximiioAPI.onStart();
        } else {
            WritableMap map = Arguments.createMap();
            map.putString("visitorId", proximiioAPI.getVisitorID());
            authPromise.resolve(map);
        }
    }

    @ReactMethod
    public void destroy(boolean eraseData) {
        if (proximiioAPI != null) {
            proximiioAPI.onStop();
            proximiioAPI.destroy();
        }
        this.destroyService(eraseData);
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
            proximiioAPI.setActivity(null);
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
        }
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public String getName() {
        return "ProximiioNative";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<>();
        for (ProximiioOptions.NotificationMode mode : ProximiioOptions.NotificationMode.values()) {
            constants.put("NOTIFICATION_MODE_" + mode.toString(), mode.toInt());
        }

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
}
