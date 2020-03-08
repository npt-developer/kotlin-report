package com.example.kotlinreport.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.collections.ArrayList

class PermissionUtils {

    companion object {
        @JvmStatic
        public val REQUEST_CODE_PERMISSION_CHECK: Int = 1001
        @JvmStatic
        public val REQUEST_CODE_PERMISSION_SETTING: Int = 1002

        fun findUnAskedPermissions(context: Context, wanted: ArrayList<String>): ArrayList<String> {
            var result: ArrayList<String> = ArrayList();

            for (perm: String in wanted) {
                if (!PermissionUtils.hasPermission(context, perm)) {
                    result.add(perm);
                }
            }

            return result;
        }

        @JvmStatic
        fun hasPermission(context: Context, permission: String): Boolean {
            if (PermissionUtils.canAskPermission()) {
                val result = ContextCompat.checkSelfPermission(context, permission)
                return result == PackageManager.PERMISSION_GRANTED
            }
            return true
        }

        @JvmStatic
        fun canAskPermission(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }

        @JvmStatic
        @RequiresApi(api = Build.VERSION_CODES.M)
        fun requestPermissions(context: Context, permissions: ArrayList<String>) {
            if (permissions == null || permissions.size == 0) {
                return
            }

            val permissionSize = permissions.size
            val stringPermissions = arrayOfNulls<String>(permissionSize)
            for (i in 0 until permissionSize) {
                stringPermissions[i] = permissions[i]
            }

            (context as Activity).requestPermissions(stringPermissions, REQUEST_CODE_PERMISSION_CHECK)
        }


        fun onRequestPermissionsResult(
            context: Context,
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            when (requestCode) {
                REQUEST_CODE_PERMISSION_CHECK -> {
                    // If request is cancelled, the result arrays are empty.
                    val needPermissions = java.util.ArrayList<String>()
                    var hasPermissionDontShowDialog: Boolean? = false
                    val grantResultsSize = grantResults.size
                    for (i in 0 until grantResultsSize) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                            // permission was granted, yay! Do the
                            // contacts-related task you need to do.
                            Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
                        } else {
                            needPermissions.add(permissions[i])
                            // permission denied, boo! Disable the
                            // functionality that depends on this permission.
                            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()

                            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                    context as Activity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                hasPermissionDontShowDialog = true
                                break
                            }
                        }
                    }

                    if (needPermissions.size > 0) {
                        if (hasPermissionDontShowDialog!!) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            (context as Activity).startActivityForResult(
                                intent,
                                REQUEST_CODE_PERMISSION_SETTING
                            )
                            return
                        } else {
                            Toast.makeText(context, "Errr $needPermissions", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun onRequestPermissionsResult(
            context: Context,
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray,
            wantedPermissions: java.util.ArrayList<String>,
            textViewError: TextView
        ) {
            when (requestCode) {
                REQUEST_CODE_PERMISSION_CHECK -> {
                    // If request is cancelled, the result arrays are empty.
                    val needPermissions = java.util.ArrayList<String>()
                    var hasPermissionDontShowDialog: Boolean? = false
                    val grantResultsSize = grantResults.size
                    for (i in 0 until grantResultsSize) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                            // permission was granted, yay! Do the
                            // contacts-related task you need to do.
                            Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show()
                        } else {
                            needPermissions.add(permissions[i])
                            // permission denied, boo! Disable the
                            // functionality that depends on this permission.
                            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()

                            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                    context as Activity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )
                            ) {
                                hasPermissionDontShowDialog = true
                                break
                            }
                        }
                    }

                    /*
                    Cannot base on needPermissions because case don't show Dialog => perrmisions don't return
                    Using wantedPermissions and check again
                 */
                    val requestPermission =
                        PermissionUtils.findUnAskedPermissions(context, wantedPermissions)
                    if (requestPermission.size != 0) {
                        textViewError.text =
                            "No permission: $requestPermission\n Please open Settings/App permissions"
                    }
                }
            }
        }
    }

}