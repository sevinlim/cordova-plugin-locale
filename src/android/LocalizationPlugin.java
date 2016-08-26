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

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
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

  private JSONObject stringMap = new JSONObject();
  private BroadcastReceiver localeChangeReceiver = null;

  private CallbackContext localeChangeCallback = null;

//  ACTION_LOCALE_CHANGED

  private void registerLocaleChange() {
    if (localeChangeReceiver != null) {
      return;
    }
    Activity activity = cordova.getActivity();
    IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
    localeChangeReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        LOG.e(TAG, "Locale Updated");
        // update stored strings
        Iterator<String> jsonKeys = stringMap.keys();
        while (jsonKeys.hasNext()) {
          String key = jsonKeys.next();
          try {
            stringMap.put(key, getFromResource(key));
          } catch (JSONException e) {
            LOG.e(TAG, "Can't update string with key: "+key);
          }
        }
        if (localeChangeCallback != null) {
          PluginResult result = new PluginResult(PluginResult.Status.OK, getLocale());
          result.setKeepCallback(true);
          localeChangeCallback.sendPluginResult(result);
//          localeChangeCallback.success(Locale.getDefault().getDisplayLanguage());
        }
      }
    };
    activity.registerReceiver(localeChangeReceiver, filter);
  }

  private String getFromResource(String resourceId) {
    Activity activity = cordova.getActivity();
    int identifier = activity.getResources().getIdentifier(resourceId, "string", activity.getPackageName());
    if (identifier == 0) {
      return null;
    }
    String resString = activity.getResources().getString(identifier);
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

  public String getAll() {
    return stringMap.toString();
  }

  public String get(String resourceId) throws JSONException {
    String resString;
    if (!stringMap.has(resourceId)) {
      resString = getFromResource(resourceId);
      if (resString != null) {
        stringMap.put(resourceId, resString);
      }
    } else {
      resString = stringMap.getString(resourceId);
    }
    return resString;
  }

   public String getLocale() {
       return Locale.getDefault().getDisplayLanguage();
   }

  @Override
  public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
    registerLocaleChange();

    if (action.equals(ACTION_GET)) {
      String resourceId = args.getString(0);
      String resString = get(resourceId);
      if (resString != null) {
        callbackContext.success(resString);
      } else {
        callbackContext.error("Resource not found: " + resourceId);
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
    } else if (action.equals(ACTION_REGISTER_CALLBACK)) {
      localeChangeCallback = callbackContext;
    } else if (action.equals(ACTION_UNREGISTER_CALLBACK)) {
      localeChangeCallback = null;
    } else if (action.equals((ACTION_GET_LOCALE))) {
      callbackContext.success(getLocale());
    } else {
      callbackContext.error("Invalid action");
      return false;
    }
    return true;
  }
}
