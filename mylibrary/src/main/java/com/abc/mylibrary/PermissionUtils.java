/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abc.mylibrary;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

public class PermissionUtils {
     private static final String TAG = "PermissionUtils";

    public static boolean requestPermission(Activity activity, int requestCode, String... permissions) {

        boolean granted = true;
        ArrayList<String> permissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            for (String s : permissions) {
                int permissionCheck = ContextCompat.checkSelfPermission(activity, s);
                boolean hasPermission = (permissionCheck == PackageManager.PERMISSION_GRANTED);
                granted &= hasPermission;
                if (!hasPermission) {
                    permissionsNeeded.add(s);
                }
            }


            if (granted) {
                return true;
            } else {
                ActivityCompat.requestPermissions(activity,
                        permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                        requestCode);
                return false;

            }
        } else {
            return true;
            // do something for phones running an SDK before M
        }


    }


    public static boolean permissionGranted(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int permissionCode) {
        if (requestCode == permissionCode) {

            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    //denied
                    Log.w("denied", permission);

                    Toast.makeText(activity, "Required permission : " + permission + "", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
                        Log.w("allowed", permission);

                        return true;
                    } else {
                        //set to never ask again
                        Log.w("set to never ask again", permission);
                        //do something here.
                        Toast.makeText(activity, "Enable permission in Settings :" + permission + "", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }
            }

        }
        return false;
    }


}
