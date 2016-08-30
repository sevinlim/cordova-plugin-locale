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

/* global cordova, module */
"use strict";

module.exports = {
    get: function (id, success, failure) {
        cordova.exec(success, failure, 'localization', 'get', [id]);
    },

    getAll: function (ids, success, failure) {
        var successWrapper = function(_strings) {
            success(JSON.parse(_strings));
        };
        cordova.exec(successWrapper, failure, 'localization', 'getAll', [ids]);
    },

    register: function (success, failure) {
        cordova.exec(success, failure, 'localization', 'registerOnChange', []);
    },

    unregister: function (callbackId, success, failure) {
        cordova.exec(success, failure, 'localization', 'unregisterOnChange', [callbackId]);
    }
};
