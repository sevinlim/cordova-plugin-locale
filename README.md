# cordova-plugin-locale

This plugin allows the user to read strings directly from strings.xml of the device locale resource file in Android system. 
It also allows registering a callback to listen for a change in locale.

 * Get localized strings
 * Get device localization
 * Listen for change in device localization

## Supported Platforms

* Android (4.3 or greater)

# Installing

### Cordova

    $ cordova plugin add https://github.com/sevinlim/cordova-plugin-locale.git

### Ionic

    $ ionic plugin add https://github.com/sevinlim/cordova-plugin-locale.git

# Methods

- [localization.get](#get)
- [localization.getAll](#getAll)
- [localization.getLocale](#getLocale)
- [localization.register](#register)
- [localization.unregister](#unregister)

## get

Get the localized string with a given id

    localization.get(string_id, success, failure);

### Parameters

- __string_id__: String value name, i.e. app_name
- __success__: Success callback function
- __failure__: Error callback function

### Returns

- __string__: String value

### Quick Example

    localization.get('app_name', function(_string) {
        console.log(_string);
    }, failure);

## getAll

Get all the localized string that have been retrieved before

    localization.getAll(string_ids, success, failure);

### Parameters

- __string_ids__: Array of string value names, i.e. [app_name, ..]
- __success__: Success callback function
- __failure__: Error callback function

### Returns

- __string__: JSON string map with (string_id, string_value) pairs

### Quick Example

    localization.getAll(function(_stringJson) {
        console.log(_stringJson);
    }, failure);

## getLocale

Get the device locale

    localization.getLocale(success, failure);

### Parameters

- __success__: Success callback function
- __failure__: Error callback function

### Returns

- __string__: String locale

### Quick Example

    localization.getLocale(function(_locale) {
        console.log(_locale); // English
    }, failure);


## register

Register a listener for when the device locale is changed

    localization.register(success, failure);

### Parameters

- __success__: Success callback function, gets callback everytime locale is changed until unregistered
- __failure__: Error callback function

### Returns

- __string__: Callback ID (NO_RESULT), Locale (RESULT_OK)

### Quick Example

    localization.register(function(_locale) {
        console.log(_locale);
    }, failure);


## unregister

Unregister a previous listener with callback ID

    localization.register(success, failure);

### Parameters

- __callback_id__: Callback ID for listener
- __success__: Success callback function, gets callback everytime locale is changed until unregistered
- __failure__: Error callback function

### Returns

### Quick Example

    localization.unregister(callbackId, function(_locale) {
        console.log(_locale);
    }, failure);