// (c) 2016-2016 Sevin Lim
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.sldev.cordova.locale;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.IntentFilter;
import android.os.Handler;

import android.provider.Settings;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.*;

public class LocalizationPlugin extends CordovaPlugin {

    // actions
    private static final String ACTION_GET = "get";
    private static final String ACTION_GET_ALL = "getAll";

    private static final String TAG = "LocalizationPlugin";

    private JSONObject stringMap = null;

    private JSONObject getStringMap() throws Exception {
        if (stringMap == null) {
            Activity activity = cordova.getActivity();
            stringMap = new JSONObject();
            // int identifier = activity.getResources().getIdentifier(resourceId, "string", activity.getPackageName());
            // activity.getResources().getString(identifier)
            // for (Field field : R.string.class.getDeclaredFields()) {
            //     if (Modifier.isStatic(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers()) && field.getType().equals(int.class)) {
            //         try {
            //             int stringId = field.getInt(null);
            //             stringMap.put(field.getName(), mContext.getResources().getString(stringId));
            //         } catch (JSONException e) {
            //             throw e.getMessage();
            //         } catch (IllegalAccessException e) {
            //             throw e.getMessage();
            //         }
            //     }
            // }
        }
        return stringMap;
    }

    public String getAll() {
        return stringMap.toString();
    }

    public String get(String resourceId) {
        if (!stringMap.has(resourceId)) {
            return null;
        }
        return stringMap.get(resourceId);
    }

    // public String getLocale() {
    //     return Locale.getDefault().getDisplayLanguage();
    // }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (stringMap == null) {
            try {
                getStringMap();
            } catch (Exception e) {
                callbackContext.error(e.getMessage());
            }
        }

        if (action.equals(ACTION_GET)) {
            String resourceId = args.getString(0);
            String resString = get(resourceId);
            if (resString != null) {
                callbackContext.success(resString);
            } else {
                callbackContext.error("Resource not found: "+resourceId);
                return false;
            }
        } else if (action.equals(ACTION_GET_ALL)) {
            String resString = getAll();
            if (resString == null) {
                callbackContext.success(resString);
            } else {
                callbackContext.error("Resource file not found");
                return false;
            }
        } else {
            callbackContext.error("Invalid action");
            return false;
        }
        return true;
    }
}
