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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.*;

public class LocalizationPlugin extends CordovaPlugin {

  // actions
  private static final String ACTION_GET = "get";
  private static final String ACTION_GET_ALL = "getAll";
  private static final String ACTION_GET_LOCALE = "getLocale";
  private static final String ACTION_REGISTER_CALLBACK = "registerOnChange";
  private static final String ACTION_UNREGISTER_CALLBACK = "unregisterOnChange";

  private static final String TAG = "LocalizationPlugin";

  private JSONObject stringMap = null;
  private IntentFilter localeChangeIntent = null;
  private BroadcastReceiver localeChangeReceiver = null;
  private Map<String, CallbackContext> localeChangeListeners;

  private Activity cordovaActivity;

  @Override
  protected void pluginInitialize() {
    super.pluginInitialize();
    cordovaActivity = cordova.getActivity();
    stringMap = new JSONObject();
    localeChangeListeners = new HashMap<String, CallbackContext>();
    initLocaleChangeReceiver();
    onStart();
  }

  @Override
  public void onStart() {
    try {
      cordovaActivity.registerReceiver(localeChangeReceiver, localeChangeIntent);
    } catch (IllegalArgumentException e) {
      Log.e(TAG, e.getLocalizedMessage());
    }
  }

  @Override
  public void onStop() {
    try {
      cordovaActivity.unregisterReceiver(localeChangeReceiver);
    } catch (IllegalArgumentException e) {
      Log.e(TAG, e.getLocalizedMessage());
    }
  }

  private void initLocaleChangeReceiver() {
    if (localeChangeReceiver != null) {
      return;
    }
    localeChangeIntent = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
    localeChangeReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        // update stored strings
        Iterator<String> jsonKeys = stringMap.keys();
        while (jsonKeys.hasNext()) {
          String key = jsonKeys.next();
          try {
            stringMap.put(key, getFromResource(key));
          } catch (JSONException e) {
//            Log.e(TAG, "Can't update string with key: "+key);
          }
        }
        if (!localeChangeListeners.isEmpty()) {
          PluginResult result = new PluginResult(PluginResult.Status.OK, getLocale());
          result.setKeepCallback(true);
          for (CallbackContext listener : localeChangeListeners.values()) {
            listener.sendPluginResult(result);
          }
//          localeChangeCallback.success(Locale.getDefault().getDisplayLanguage());
        }
      }
    };
  }

  private String getFromResource(String resourceId) {
    int identifier = cordovaActivity.getResources().getIdentifier(resourceId, "string", cordovaActivity.getPackageName());
    if (identifier <= 0) {
      return null;
    }
//    Log.d(TAG, "Get String with Identifier "+identifier);
    String resString = cordovaActivity.getResources().getString(identifier);
    return resString;
  }

//  private JSONObject getStringMap() throws Exception {
//    if (stringMap == null) {
//      stringMap = new JSONObject();
//       for (Field field : R.string.class.getDeclaredFields()) {
//           if (Modifier.isStatic(field.getModifiers()) && !Modifier.isPrivate(field.getModifiers()) && field.getType().equals(int.class)) {
//               try {
//                   int stringId = field.getInt(null);
//                   stringMap.put(field.getName(), mContext.getResources().getString(stringId));
//               } catch (JSONException e) {
//                   throw e.getMessage();
//               } catch (IllegalAccessException e) {
//                   throw e.getMessage();
//               }
//           }
//       }
//    }
//    return stringMap;
//  }

  private String get(String resourceId) throws JSONException {
    String resString;
    if (!stringMap.has(resourceId)) {
      resString = getFromResource(resourceId);
      if (resString != null) {
        stringMap.put(resourceId, resString);
      } else {
        Log.d(TAG, "Resource string null for id: "+resourceId);
      }
    } else {
      resString = stringMap.getString(resourceId);
    }
    return resString;
  }

  private String getAll(JSONArray resIds) throws JSONException {
    if (resIds != null) {
      for (int i=0; i<resIds.length(); ++i) {
        get(resIds.getString(i));
      }
    }
    return stringMap.toString();
  }


   public String getLocale() {
       return Locale.getDefault().getDisplayLanguage();
   }

  @Override
  public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
    if (action.equals(ACTION_GET)) {
      String resourceId = args.getString(0);
      String resString = get(resourceId);
      if (resString != null) {
        callbackContext.success(resString);
      } else {
        callbackContext.error("Resource not found: " + resourceId);
      }
    } else if (action.equals(ACTION_GET_ALL)) {
      LOG.d(TAG, args.toString());
      JSONArray resIds = null;
      try {
        resIds = args.getJSONArray(0);
      } catch (JSONException je) {
        // no res ids
        callbackContext.error("Resource ID Error: "+je.getLocalizedMessage());
      }
      Log.d(TAG, "getAll: "+resIds);
      String resString = getAll(resIds);
      if (resString != null) {
        callbackContext.success(resString);
      } else {
        callbackContext.error("GETALL: Resource not found: "+resIds);
      }
    } else if (action.equals(ACTION_REGISTER_CALLBACK)) {
      localeChangeListeners.put(callbackContext.getCallbackId(), callbackContext);
      PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT, callbackContext.getCallbackId());
      result.setKeepCallback(true);
      callbackContext.sendPluginResult(result);
    } else if (action.equals(ACTION_UNREGISTER_CALLBACK)) {
      String callbackId = args.getString(0);
      if (callbackId != null && localeChangeListeners.containsKey(callbackId)) {
        localeChangeListeners.remove(callbackId);
      } else {
        callbackContext.error("Callback ID invalid.");
      }
    } else if (action.equals((ACTION_GET_LOCALE))) {
      callbackContext.success(getLocale());
    } else {
      callbackContext.error("Invalid action: "+action);
    }
    return true;
  }
}
