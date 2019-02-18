# proximiio-react-native-core

## TODO

- Publish on npm to reduce setup steps.
- Add js bridge.

## Instructions

### Setup

- Clone into a new directory (proximiio-react) to your React Native projects directory.

- Add proximiio-react to package.json dependencies: `"proximiio-react": "file:../proximiio-react"`

- Run `npm install`

- If on Windows, Delete the symlink to proximiio-react in node_modules and copy proximiio-react to node_modules directly.

- Run `react-native link proximiio-react`

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
    ...
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
    ...
    implementation("androidx.core:core:1.0.1")
    implementation("androidx.versionedparcelable:versionedparcelable:1.0.0")
    implementation("androidx.collection:collection:1.0.0")
    implementation("androidx.annotation:annotation:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.0.0")
    implementation("androidx.lifecycle:lifecycle-common:2.0.0")
    implementation("androidx.arch.core:core-common:2.0.0")
}
```

### Usage

- Edit App.js and add your auth key:

```js
import { DeviceEventEmitter, NativeModules } from 'react-native';

export default class App extends Component<Props> {

    ...

    componentDidMount() {
        console.log(NativeModules.ProximiioReact);
        this.listener = DeviceEventEmitter.addListener(NativeModules.ProximiioReact.EVENT_POSITION, e => console.log(e));
        NativeModules.ProximiioReact.init('ADD YOUR AUTH KEY HERE');
    }
    
    componentWillUnmount() {
        this.listener.remove();
    }
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
