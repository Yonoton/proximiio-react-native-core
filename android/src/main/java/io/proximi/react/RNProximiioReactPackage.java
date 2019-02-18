
package io.proximi.react;

import android.app.Activity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

import androidx.annotation.NonNull;

public class RNProximiioReactPackage implements ReactPackage {
    private RNProximiioReactModule proximiioModule;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        if (proximiioModule == null) {
            proximiioModule = new RNProximiioReactModule(reactContext);
        }
        return Collections.singletonList(proximiioModule);
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (proximiioModule != null) {
            proximiioModule.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}