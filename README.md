# proximiio-react-native-core

React Native Library for Proximi.io Positioning Platform

## Description

Proximiio React Native library wraps Proximi.io SDKs and provides natural React Native workflow.
Proximiio RN Library works as Singleton object, that should be authorized once per app start,
components can subscribe to available notifications and perform.

Providing application authorization token is necessary, token can be found at Proximi.io portal in Applications section.

## Installation

### Library installation

```
npm install -s https://github.com/proximiio/proximiio-react-native-core
```

### IOS Installation

- open your application IOS project in XCode
- right-click 'Libaries' folder in Project Tree on the left side and select option 'Add Files to...'
![](https://proximi.io/wp-content/uploads/2019/05/libraries_add_files.png)
- navigate to 'node_modules/proximiio-react-native-core/ios' and select the file 'ProximiioNative.xcodeproj'
![](https://proximi.io/wp-content/uploads/2019/05/libraries_core.png)
- in XCode expand Libraries/ProximiioNative.xcodeproj/ProximiioNative folder
![](https://proximi.io/wp-content/uploads/2019/05/libraries_add_files.png)
- drag Proximiio.framework to your project directory under top level, leave the 'Copy items if needed' unchecked
![](https://proximi.io/wp-content/uploads/2019/05/libraries_add_confirm.png)
- ensure that Proximiio.framework is present in Embedded Binaries section in project configation / General Tab
![](https://proximi.io/wp-content/uploads/2019/05/settings_embedded.png)
- switch tab to 'Build Phases' tab and expand the 'Link Binary with Libraries' section
- click '+' icon below the library list and select 'libProximiioNative.a'
![](https://proximi.io/wp-content/uploads/2019/05/settings_link_library.png)
- switch to 'Build Settings' tab and search for 'framework_search', double click the 'Framework Search Paths' value field and add new line containing '$(PROJECT_DIR)/../node_modules/proximiio-react-native-core/ios/ProximiioNative'
![](https://proximi.io/wp-content/uploads/2019/05/settings_framework_search.png)

- switch to 'Capabilities' tab, enable 'Background Modes' and enable both 'Location Updates' & 'Uses Bluetooth LE accessories' to allow beacon operation while application is in background
![](https://proximi.io/wp-content/uploads/2019/05/settings_background.png)
- locate 'Info.plist' file belonging to the project, right-click it and select 'Open As' -> 'Source File'
![](https://proximi.io/wp-content/uploads/2019/05/plist.png)
- copy & paste following lines to the bottom of the Info.plist file, but above the last '/<dict></dict></plist>' lines, modify the text to suit your application
![](https://proximi.io/wp-content/uploads/2019/05/plist_edit.png)

```diff
+  <key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
+  <string>Allow Background Location updates for Event triggering while the App is in background</string>
+  <key>NSLocationWhenInUseUsageDescription</key>
+  <string>Allow Location Updates for basic Proximi.io SDK operation</string>
+  <key>NSLocationAlwaysUsageDescription</key>
+  <string>Allow always usage for permanent positioning support</string>
+  <key>NSMotionUsageDescription</key>
+  <string>Allow motion detection for improved positioning</string>
+  <key>NSBluetoothPeripheralUsageDescription</key>
+  <string>Allow bluetooth for improved beacon operation</string>
```

![](https://proximi.io/wp-content/uploads/2019/05/plist_edit.png)

### Android Installation

#### `PROJECT_ROOT/android/app/src/main/AndroidManifest.xml`
Remove android:allowBackup field or use tools:override if you need the option.

#### `PROJECT_ROOT/android/gradle.properties`
Edit file to include following:

```diff
+android.useAndroidX=true
+android.enableJetifier=true
```


#### `PROJECT_ROOT/android/build.gradle`
We need to add an additional repository in order to get our dependencies.

* `https://jitpack.io`

```diff
allprojects {
    repositories {
        mavenLocal()
        google()
        jcenter()
+       maven { url "https://jitpack.io" }
        maven {
            // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
            url "$rootDir/../node_modules/react-native/android"
        }
    }
}
```

Make sure that your `buildscript > ext` settings are correct.
We want to be on `28` or higher:

```
buildscript {
    ext {
        buildToolsVersion = "28.0.3"
        minSdkVersion = 20
        compileSdkVersion = 28
        targetSdkVersion = 28
    }
}
```

#### `PROJECT_ROOT/android/app/build.gradle`

Add following to the file:

```diff
android {
+    packagingOptions {
+        exclude 'META-INF/LICENSE'
+        exclude 'META-INF/LICENSE-FIREBASE.txt'
+        exclude 'META-INF/NOTICE'
+        exclude 'lib/armeabi/libcpaJNI.so'
+        exclude 'lib/armeabi/libsqlcipher.so'
+    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

+repositories {
+    maven { url "http://proximi-io.bintray.com/proximiio-android" }
+    maven { url "http://indooratlas-ltd.bintray.com/mvn-public" }
+    maven { url 'https://maven.google.com' }
+}

dependencies {
+    implementation("androidx.core:core:1.0.1")
+    implementation("androidx.versionedparcelable:versionedparcelable:1.0.0")
+    implementation("androidx.collection:collection:1.0.0")
+    implementation("androidx.annotation:annotation:1.0.0")
+    implementation("androidx.lifecycle:lifecycle-runtime:2.0.0")
+    implementation("androidx.lifecycle:lifecycle-common:2.0.0")
+    implementation("androidx.arch.core:core-common:2.0.0")
+    implementation project(':proximiio-react-native-core')
+    implementation 'io.proximi.proximiiolibrary:proximiiolibrary:2.8.5'
}
```

You can set the Support Library version or the okhttp version if you use other modules that depend on them:
* `supportLibVersion "28.0.0"`
* `okhttpVersion "3.12.1"`


#### `PROJECT_ROOT/android/app/settings.gradle`

Include project, so gradle knows where to find the project

```diff
rootProject.name = <YOUR_PROJECT_NAME>

+include ':proximiio-react-native-core'
+project(':proximiio-react-native-core').projectDir = new File(rootProject.projectDir, '../node_modules/proximiio-react-native-core/android')

include ':app'¬
```

#### `PROJECT_ROOT/android/app/src/main/java/com/YOUR_PROJECT_NAME/MainApplication.java`

We need to register proximiio package:

```diff
package <YOUR_PROJECT_NAME>;

import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
+import io.proximi.react.RNProximiioReactPackage;

import java.util.Arrays;
import java.util.List;

public class MainApplication extends Application implements ReactApplication {
+  private RNProximiioReactPackage proximiioPackage = new RNProximiioReactPackage();

  private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
    @Override
    public boolean getUseDeveloperSupport() {
      return BuildConfig.DEBUG;
    }

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
+         proximiioPackage
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
}

```

## Usage / Javascript API

### Initialization
```js
import Proximiio from 'proximiio-react-native-core'

const PROXIMIIO_TOKEN = 'ey...' // found at proximi.io portal / applications section

// authorization
async initProximiio() {
    if (!Proximiio.isAuthorized()) {
      await this._setState({ visitorId: "authorizing..." })

      // notification customization (android only)
      Proximiio.setNotificationMode(Proximiio.NotificationModes.Enabled)
      Proximiio.setNotificationTitle("Proximi.io Background Service")
      Proximiio.setNotificationText("Allows location interactivity while the application is in background")
      Proximiio.setNotificationIcon('ic_notification')

      try {
        const state = await Proximiio.authorize(PROXIMIIO_TOKEN)
        Proximiio.requestPermissions()
        await this._setState({
          visitorId: state.visitorId,
          proximiioReady: true
        })
      } catch (error) {
        await this._setState({ visitorId: "auth failure" })
      }
    } else {
      await this._setState({
        visitorId: Proximiio.state.visitorId,
        proximiioReady: true
      })
    }

    // check other available subscription events listed below
    this.proximiioSubscriptions = {
      positionUpdates: Proximiio.subscribe(
        Proximiio.Events.PositionUpdated, this.onPositionUpdate.bind(this)
      ),
      enteredGeofence: Proximiio.subscribe(
        Proximiio.Events.EnteredGeofence, this.onGeofenceEnter.bind(this)
      ),
      exitedGeofence: Proximiio.subscribe(
        Proximiio.Events.ExitedGeofence, this.onGeofenceExit.bind(this)
      )
    }
}

onPositionUpdate(location) {
    console.log(`location updated: ${location.lat} / ${location.lng} (${location.accuracy})`)
}

onGeofenceEnter(geofence) {
    console.log(`entered geofence: ${geofence.name}`)
}

onGeofenceExit(geofence) {
    console.log(`left geofence: ${geofence.name}`)
}

// call this method before component deallocation, ie componentWillUnmount
async destroyProximiio() {
    Object.keys(this.proximiioSubscriptions).forEach(key => this.proximiioSubscriptions[key].remove())
}
```

### (Promise) Proximiio.authorize(proximiio_token) -> StateObject
authorizes the sdk and activates positioning, returns Object containing generated visitorId property.

### Proximiio.requestPermissions() -> void (IOS only)
requests location permissions from user if necessary

### (Promise) Proximiio.currentFloor() -> Promise(Floor/null)
returns current Floor object

### Proximiio.setBufferSize(Number) -> void (IOS only)
sets positioning buffer size, this option affects how often will the positioning engine recalculate
most optimal position, use with one of folowing values:

```
Proximiio.setBufferSize(0) // 'mini' (0.5s)
Proximiio.setBufferSize(1) // 'small' (1.2s)
Proximiio.setBufferSize(2) // 'medium' (3s)
Proximiio.setBufferSize(3) // 'large' (6s)
Proximiio.setBufferSize(4) // 'xlarge' (10s)
```

### Proximiio.setNativeAccuracy(NativeAccuracy) -> void
sets native accuracy threshold, the higher the accuracy, the more sensors are used resulting into
more precise positioning but with larger battery usage.

Note that Cellular and WIFI settings produce more sparse position updates with lower then GPS accuracy

```
Proximiio.setNativeAccuracy(Proximiio.NativeAccuracy.Cellular)
Proximiio.setNativeAccuracy(Proximiio.NativeAccuracy.WIFI)
Proximiio.setNativeAccuracy(Proximiio.NativeAccuracy.GPS)
Proximiio.setNativeAccuracy(Proximiio.NativeAccuracy.Navigation)
```

### Proximiio.setNotificationMode(NotificationMode) -> void (Android Only)
Sets the notification policy of the SDK.

When a notification is displayed, the Proximi.io Service is operating in a foreground service mode. This disables several battery and resource optimizations in Android that target background services, allowing Proximi.io to properly function in background. If you disable the notification, Proximi.io will not be able to function properly in background.

It's recommended to always display a notification, to keep your application transparent to the user, as well as to guarantee a consistent experience across all platforms.

```
Proximiio.setNotificationMode(Proximiio.NotificationModes.Enabled)
// Notification is Enabled

Proximiio.setNotificationMode(Proximiio.NotificationModes.Disabled)
// Notification is Disabled

Proximiio.setNotificationMode(Proximiio.NotificationModes.Required)
// Notification is enabled when running on Android 8 and above. (Please note that previous platforms also apply some limits to background services.)
```

*Note that calling Proximiio.updateOptions() after notification customization is required for the changes to take effect

### Proximiio.setNotificationTitle(String) -> void (Android Only)
Allows you to set custom content to the notification displayed.

Please note that a title, text, and an icon must be supplied for custom notification content to show. When customized notification content is shown, tapping the notification will open the application instead of the settings screen for the application. See Android documentation for more info.

```
Proximiio.setNotificationTitle("Proximi.io Background Service")
```

*Note that calling Proximiio.updateOptions() after notification customization is required for the changes to take effect

### Proximiio.setNotificationText(String) -> void (Android Only)
Allows you to set custom content to the notification displayed.

```
Proximiio.setNotificationText("Allows location interactivity while the application is in background")
```

### Proximiio.setNotificationIcon(String) -> void (Android Only)
Allows you to set custom content to the notification displayed.
The String parameter should contain the name of drawable icon file, this file has to be added in Android Studio as standard drawable image file.

```
Proximiio.setNotificationIcon('ic_notification')
```

*Note that calling Proximiio.updateOptions() after notification customization is required for the changes to take effect

### Proximiio.updateOptions() -> void (Android Only)
Performs notification options update. Call this method once after customizing notifciation title, text or icon.

## Subscription Events

**PositionUpdated** - emits location object on position updates

```
{
    lat: 48.12345678,
    lng: 18.12345678,
    accuracy: 5.12345678
}
```

**FloorChanged** - emits floor object on floor change

```
{
    id: "82050f63-7cd1-476e-b958-88b1994cb5b7",
    name: "First floor",
    level: 1,
    floorplan: "https://hosting.com/floorplan_1.png",
}
```

**EnteredGeofence** - emits geofence object when geofence is entered

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

**ExitedGeofence** - emits geofence object when geofence is left

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

**EnteredPrivacyZone** - emits privacy zone object when privacy zone is entered

```
{
    id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
    name: "Cafe A",
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

**ExitedPrivacyZone** - emits privacy zone object when privacy zone is left
```
{
    id: "82050f63-7cd1-476e-b958-88b1994cb5b8",
    name: "Cafe A",
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

**FoundIBeacon** - emits ibeacon object when new ibeacon is detected

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

**LostIBeacon** - emits ibeacon object when ibeacon signal is lost

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

**FoundEddystoneBeacon** - emits eddystone object when new eddystone beacon is detected

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

**LostEddystoneBeacon** - emits eddystone object when eddystone beacon signal is lost

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

## Example Snippets

### Change notification title using current geofence

```js
this.proximiioSubscriptions = {
    enteredGeofence: Proximiio.subscribe(
        Proximiio.Events.EnteredGeofence, this.onGeofenceEnter.bind(this)
    ),
    exitedGeofence: Proximiio.subscribe(
        Proximiio.Events.ExitedGeofence, this.onGeofenceExit.bind(this)
    )
}

onGeofenceEnter(geofence) {
    console.log(`entered geofence: ${geofence.name}`)
    Proximiio.setNotificationTitle(`Entered ${geofence.name}`)
    Proximiio.updateOptions()
}

onGeofenceExit(geofence) {
    Proximiio.setNotificationTitle(`Left ${geofence.name}`)
    Proximiio.updateOptions()
}

```

## ChangeLog

### 0.2.6
- added example snippets section in the README.md file
- android position source type support
- android lifecycle improvements
- android added beacon identifiers
- js setBufferSize ios platform limitation
- js added destroy lifecycle method

### 0.2.5
- added Proximiio.setNativeAccuracy() method
- added Proximiio.setNotificationMode(NotificationMode)
- added Proximiio.setNotificationTitle(String)
- added Proximiio.setNotificationText(String)
- added Proximiio.setNotificationIcon(String)
- added Proximiio.updateOptions()
- Android SDK version bump to 2.8.5
- IOS SDK version bump to 1.1.70
- rewritten Readme.md documentation + added this ChangeLog section