# proximiio-react-native-core

## Description

Proximiio React Native library wraps Proximi.io SDKs and provides natural React Native workflow.
Proximiio RN Library works as Singleton object, that should be authorized once per app start, components can subscribe to available notification and act upon.

Providing Application authorization Token is necessary, token can be found at Proximi.io portal in Applications section

## Available Events

**PositionUpdated**
- emits location object on position updates

```
{
    lat: 48.12345678,
    lng: 18.12345678,
    accuracy: 5.12345678
}
```

**FloorChanged**
- emits floor object on floor change

```
{
    id: "82050f63-7cd1-476e-b958-88b1994cb5b7",
    name: "First floor",
    level: 1,
    floorplan: "https://hosting.com/floorplan_1.png",
}
```

**EnteredGeofence**
- emits geofence object when geofence is entered

```
{
    id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
    name: "Office A Geofence",
    radius: 20,
    area: {
        lat: 48.12345678,
        lng: 18.12345678,
        accuracy: 20
    },
    isPolygon: true,
    polygon: [
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678]
    ]
}
```

**ExitedGeofence**
- emits geofence object when geofence is left

```
{
    id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
    name: "Office A Geofence",
    radius: 20,
    area: {
        lat: 48.12345678,
        lng: 18.12345678,
        accuracy: 20
    },
    isPolygon: true,
    polygon: [
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678]
    ]
}
```

**EnteredPrivacyZone**
- emits privacy zone object when privacy zone is entered

```
{
    id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
    name: "Toilets A",
    radius: 5,
    area: {
        lat: 48.12345678,
        lng: 18.12345678,
        accuracy: 20
    },
    isPolygon: true,
    polygon: [
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678]
    ]
}
```

**ExitedPrivacyZone**
- emits privacy zone object when privacy zone is left
```
{
    id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
    name: "Toilets A",
    radius: 5,
    area: {
        lat: 48.12345678,
        lng: 18.12345678,
        accuracy: 20
    },
    isPolygon: true,
    polygon: [
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678],
        [18.12345678, 48.12345678]
    ]
}
```

**FoundIBeacon**
- emits ibeacon object when new ibeacon is detected

```
{
    uuid: "82050f63-7cd1-476e-b958-88b1994cb5b8",
    major: 12345,
    minor: 23456,
    input: {
        id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
        name: "iKpR",
        type: "ibeacon"
    }
}
```

**LostIBeacon**
- emits ibeacon object when ibeacon signal is lost

```
{
    uuid: "82050f63-7cd1-476e-b958-88b1994cb5b8",
    major: 12345,
    minor: 23456,
    input: {
        id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
        name: "iKpR",
        type: "ibeacon"
    }
}
```

**FoundEddystoneBeacon**
- emits eddystone object when new eddystone beacon is detected

```
{
    namespace: "8b0ca750095477cb3e77",
    instanceId: "123456789abc",
    input: {
        id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
        name: "iKpR",
        type: "eddystone"
    }
}
```

**LostEddystoneBeacon**
- emits eddystone object when eddystone beacon signal is lost

```
{
    namespace: "8b0ca750095477cb3e77",
    instanceId: "123456789abc",
    input: {
        id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
        name: "iKpR",
        type: "eddystone"
    }
}
```

## Example Usage

```js
import { DeviceEventEmitter, NativeModules } from 'react-native';
import Proximiio from 'proximiio-react-native-core';

const TOKEN = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJoZWxsbyI6InNpciIsInlvdV9hcmUiOiJ0b28gY3VyaW91cyA6KSJ9.dlHIpojulZDNt80o2c7bslAAKCKEDwSePsTsXIv8v0o'; // you can find the application token in proximi.io portal / applications

export default class App extends Component<Props> {

  async componentDidMount() {
    try {
      const state = await Proximiio.authorize(TOKEN)
      // state.visitorId available here

      Proximiio.requestPermissions()

      this.subscriptions = {
        positionUpdated: Proximiio.subscribe(Proximiio.Events.PositionUpdated, this.onPositionChange),
        enteredGeofence: Proximiio.subscribe(Proximiio.Events.EnteredGeofence, this.onGeofenceEnter),
        exitedGeofence: Proximiio.subscribe(Proximiio.Events.ExitedGeofence, this.onGeofenceExit)
      }

    } catch (e) {
      console.log('proximi.io authorization failed')
    }
  }

  componentWillUnmount() {
    // remove event subscriptions
    Object.keys(this.subscriptions).forEach(key => this.subscriptions[key].remove())
  }

  onPositionChange(location) {
    console.log(`location updated: ${location.lat} / ${location.lng} (${location.accuracy})`)
  }

  onGeofenceEnter(geofence) {
    console.log(`entered geofence: ${geofence.name}`)
  }

  onGeofenceExit(geofence) {
    console.log(`left geofence: ${geofence.name}`)
  }

}
```

## Instructions

### Setup / React Native Application

In your project folder:

- npm install -s proximiio-react-native-core


### Setup / Android

- If on Windows, change the slashes in `android/settings.gradle`.

- Remove `android:allowBackup` field from `android/app/src/main/AndroidManifest.xml`, or use `tools:override` if you need the option.

- Edit gradle.properties to include the following:

```
android.useAndroidX=true
android.enableJetifier=true
```

- Add the following to `app/build.gradle`:

```
android {
    ```
    packagingOptions {
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/LICENSE-FIREBASE.txt'
        exclude 'META-INF/NOTICE'
        exclude 'lib/armeabi/libcpaJNI.so'
        exclude 'lib/armeabi/libsqlcipher.so'
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

repositories {
    maven {
        url "http://proximi-io.bintray.com/proximiio-android"
    }
    maven {
        url "http://indooratlas-ltd.bintray.com/mvn-public"
    }
    maven {
        url 'https://maven.google.com'
    }
}

dependencies {
    ```
    implementation("androidx.core:core:1.0.1")
    implementation("androidx.versionedparcelable:versionedparcelable:1.0.0")
    implementation("androidx.collection:collection:1.0.0")
    implementation("androidx.annotation:annotation:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.0.0")
    implementation("androidx.lifecycle:lifecycle-common:2.0.0")
    implementation("androidx.arch.core:core-common:2.0.0")
}
```

- Edit MainApplication.java:

```java
import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import io.proximi.react.RNProximiioReactPackage;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {
    private RNProximiioReactPackage proximiioPackage = new RNProximiioReactPackage();

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {

        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                new MainReactPackage(),
                proximiioPackage
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, /* native exopackage */ false);
    }

    RNProximiioReactPackage getProximiioPackage() {
        return proximiioPackage;
    }
}
```

- Add this method to your MainActivity.java:

```java
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    ((MainApplication)getApplication()).getProximiioPackage().onRequestPermissionsResult(requestCode, permissions, grantResults);
}
```

### Setup / IOS

- open ios project in XCode
- open Finder and go to project folder,
- drag node_modules/proximiio-react-native-core/ios/ProximiioNative.xcodeproj   into Libraries folder in XCode (in left sidebar)
- in XCode expand Libraries/ProximiioNative.xcodeproj/ProximiioNative folder
- drag Proximiio.framework to the application directory under top level
- ensure that Proximiio.framework is present in Embedded Binaries section in  project configation / General Tab
- select Capabilities Tab in project configuration
- enable Background Modes and select both "Location Updates" & "Uses Bluetooth LE accessories" to fully allow beacon operation while application is in background
- locate project Info.plist file, right-click it and select Open As -> Source File
- copy & paste following lines to the bottom of the Info.plist file, but above </plist>, modify the text to suit your application

```
  <key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
  <string>Allow Background Location updates for Event triggering while the App is in background</string>
  <key>NSLocationWhenInUseUsageDescription</key>
  <string>Allow Location Updates for basic Proximi.io SDK operation</string>
  <key>NSLocationAlwaysUsageDescription</key>
  <string>Allow always usage for permanent positioning support</string>
  <key>NSMotionUsageDescription</key>
  <string>Allow motion detection for improved positioning</string>
  <key>NSBluetoothPeripheralUsageDescription</key>
  <string>Allow bluetooth for improved beacon operation</string>
```