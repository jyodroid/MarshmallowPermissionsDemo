package com.jyodroid.permissiondemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by johntangarife on 4/15/16.
 * Helps update or access application resources and ask for permissions at runtime in new Android
 * devices with Marshmallow (API 23) or newest.
 * <p/>
 * Beginning in Android 6.0 (API level 23), users grant permissions to apps while the app is
 * running, not when they install the app
 * (http://developer.android.com/intl/es/training/permissions/requesting.html)
 */

@TargetApi(Build.VERSION_CODES.M)
public class PermissionsUtility {

    private static final String PACKAGE_URI = "package:";

    @StringDef({RECORD_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ApplicationPermissions {
    }

    //Permissions
    public static final String RECORD_PERMISSION = Manifest.permission.RECORD_AUDIO;
    //Read permission group storage includes WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE
    public static final String WRITE_EXTERNAL_STORAGE_PERMISSION =
            Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @IntDef(
            {MY_PERMISSIONS_REQUEST_AUDIO_RECORD_PERMISSION,
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION,
                    MY_PERMISSIONS_MULTIPLE, MY_PERMISSIONS_UNKNOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionType {
    }

    //Request constants
    public static final int MY_PERMISSIONS_UNKNOWN = -1;
    public static final int MY_PERMISSIONS_REQUEST_AUDIO_RECORD_PERMISSION = 1;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2;
    public static final int MY_PERMISSIONS_MULTIPLE = 12;

    /**
     * Verify if determinate permission is granted.
     *
     * @param activity   where is called the method
     * @param permission permission that you want to know if is granted
     * @return true if permission is granted
     */
    public static boolean verifyPermission(
            @NonNull Activity activity, @ApplicationPermissions String permission) {

        return PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(activity, permission);
    }

    /**
     * verify is the checkbox "" on permission promp is checked
     *
     * @param activity   from where is asking permissions
     * @param permission permission to verified if is checked the don't show again
     * @return tru if checkbox is checked
     */
    public static boolean isCheckedDontAskAgain(
            @NonNull Activity activity, @NonNull @ApplicationPermissions String permission) {

        //If don ask for explanation and permissions still are not granted
        if (!shouldAskForPermission(activity, permission)) {
            if (!verifyPermission(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * define if is necessary an explanation for solicited permission
     *
     * @param activity   from where the permission is solicited
     * @param permission feature from device required
     * @return true if you have to explain why you need permission
     */
    public static boolean shouldAskForPermission(
            @NonNull Activity activity, @ApplicationPermissions String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }


    /**
     * Verifies the camera permission is granted or else for android M or higher ask for missing
     * permission.
     *
     * @param permissions List of permission to be asked
     * @param request     permission type code to be return to caller
     * @param activity    from where the method is called
     */
    public static void requestPermissions(
            @ApplicationPermissions final String[] permissions,
            @PermissionType final int request,
            @NonNull final Activity activity) {

        ActivityCompat.requestPermissions(activity, permissions, request);
    }

    /**
     * Helps to find the permission type required for feature take card picture and store it
     *
     * @param activity from where is called
     * @return PermissionType required for application. If returns -1 application doesn't require
     * known permission
     */
    public static
    @PermissionType
    int obtainPermissionType(@NonNull Activity activity) {
        //Verify all permission needed
        if (!verifyPermission(activity, RECORD_PERMISSION) &&
                !verifyPermission(activity, WRITE_EXTERNAL_STORAGE_PERMISSION)) {
            return MY_PERMISSIONS_MULTIPLE;
        } else
            //Verify if only camera permission is needed
            if (!verifyPermission(activity, RECORD_PERMISSION)) {
                return MY_PERMISSIONS_REQUEST_AUDIO_RECORD_PERMISSION;
            } else
                //Verify if only write on external storage permission is needed
                if (!verifyPermission(
                        activity, WRITE_EXTERNAL_STORAGE_PERMISSION)) {
                    return MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION;
                }
        return MY_PERMISSIONS_UNKNOWN;
    }

    /**
     * Request necessary permissions for taking heartcard picture functionality from a
     * {@link Fragment}
     *
     * @param permissionType type of the permission solicited from the fagment
     * @param activity       from where the method is called
     * @param messageHandler for control message state in a separated thread
     */
    public static void requestCardPicturePermissions(
            @PermissionType int permissionType,
            @NonNull Activity activity,
            @NonNull HandleMessage messageHandler) {

        String okButtonText = "Lets do it!!";
        String negativeButtonText = "No way";

        switch (permissionType) {
            case MY_PERMISSIONS_MULTIPLE:
                //List of permissions needed;
                String[] multiplePermissionList = {
                        RECORD_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION};
                //Verify if you should explain why you need permission
                if (PermissionsUtility.shouldAskForPermission(
                        activity, WRITE_EXTERNAL_STORAGE_PERMISSION) ||
                        shouldAskForPermission(activity, RECORD_PERMISSION)) {

                    PermissionsUtility
                            .showPermissionMessage(
                                    "We will need permission",
                                    okButtonText,
                                    negativeButtonText,
                                    multiplePermissionList,
                                    activity,
                                    MY_PERMISSIONS_MULTIPLE,
                                    messageHandler);
                }
                //don't need to explain ask for permissions directly
                else {
                    requestPermissions(
                            multiplePermissionList, MY_PERMISSIONS_MULTIPLE, activity);
                }
                break;
            case MY_PERMISSIONS_REQUEST_AUDIO_RECORD_PERMISSION:
                //camera permission needed;
                String[] cameraPermissionList = {RECORD_PERMISSION};

                //Verify if you should explain why you need camera permission
                if (shouldAskForPermission(activity, RECORD_PERMISSION)) {

                    showPermissionMessage(
                            "We will need permission to record!!",
                            okButtonText,
                            negativeButtonText,
                            cameraPermissionList,
                            activity,
                            MY_PERMISSIONS_REQUEST_AUDIO_RECORD_PERMISSION,
                            messageHandler);
                }
                //don't need to explain ask for camera permission directly
                else {
                    requestPermissions(
                            cameraPermissionList, MY_PERMISSIONS_REQUEST_AUDIO_RECORD_PERMISSION, activity);
                }
                break;
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION:
                //write on external storage permissions needed;
                String[] writePermissionList = {WRITE_EXTERNAL_STORAGE_PERMISSION};

                //Verify if you should explain why you need permission to write in external storage
                if (shouldAskForPermission(activity, WRITE_EXTERNAL_STORAGE_PERMISSION)) {

                    showPermissionMessage(
                            "We need to storage the recorded!!",
                            okButtonText,
                            negativeButtonText,
                            writePermissionList,
                            activity,
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION,
                            messageHandler);
                }
                //don't need to explain ask for permissions directly or maybe "Never ask again is called"
                else {
                    requestPermissions(
                            writePermissionList,
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION,
                            activity);
                }
        }
    }

    /**
     * Determines if is needed to show explanation an shows when ask from permission from a
     * {@link Fragment}
     *
     * @param description        description to be shown in dialog message
     * @param positiveButtonText message in the positive button of the dialog
     * @param negativeButtonText message in the positive button of the dialog
     * @param permissionList     permissions to solicit from fragment. If is null will assume
     *                           message is for go to application settings
     * @param activity           from where is call the method
     * @param requestType        type of the permission solicited
     * @param handler            for control message state in a separated thread
     */
    public static void showPermissionMessage(
            @NonNull String description,
            @NonNull String positiveButtonText,
            @NonNull String negativeButtonText,
            @Nullable final String[] permissionList,
            @NonNull final Activity activity,
            @PermissionType final int requestType,
            @NonNull final HandleMessage handler) {

        //Dialog buttons listeners
        final DialogShower dialogShower = new DialogShower();
        DialogInterface.OnClickListener negativeButtonListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                        handler.removeCallbacks(dialogShower);
                    }
                };

        DialogInterface.OnClickListener positiveButtonListener;
        //Determine type of message
        //If no permission list, we
        if (null == permissionList) {
            positiveButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Send any message (empty one in this case) to dismiss dialog
                    handler.sendEmptyMessage(0);
                    //Take the user to application settings
                    Intent settingsIntent = new Intent();
                    settingsIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri applicationUri = Uri.parse(PACKAGE_URI + activity.getPackageName());
                    settingsIntent.setData(applicationUri);
                    activity.startActivity(settingsIntent);
                }
            };
        } else {
            positiveButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Send any message (empty one in this case) to dismiss dialog
                        handler.sendEmptyMessage(0);
                        requestPermissions(
                                permissionList,
                                requestType,
                                activity);
                }
            };
        }

        //create dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setCancelable(false)
                .setTitle("Permissions needed")
                .setMessage(description)
                .setNegativeButton(negativeButtonText, negativeButtonListener)
                .setPositiveButton(positiveButtonText, positiveButtonListener);

        AlertDialog permissionDialog = builder.create();


        /*Show dialog: (from android documentation)
        Show an explanation to the user *asynchronously* -- don't block
        this thread waiting for the user's response! After the user
        sees the explanation, try again to request the permission.*/
        dialogShower.setMDialog(permissionDialog);
        handler.setMDialogShower(dialogShower);
        handler.post(dialogShower);
    }

    /**
     * Shows the {@link Dialog} in a separate thread
     */
    private static class DialogShower extends Thread {

        private Dialog mDialog;

        @Override
        public void run() {
            mDialog.show();
        }

        public void dismissDialog() {
            mDialog.dismiss();
        }

        public void setMDialog(@NonNull Dialog dialog) {
            mDialog = dialog;
        }
    }

    /**
     * Handles the {@link Thread} of the DialogShower and dismiss the dialog when an empty message
     * with 0 is received.
     */
    public static class HandleMessage extends Handler {

        private DialogShower mDialogShower;

        @Override
        public void handleMessage(Message msg) {
            //Receive any message and dismiss dialog
            if (msg.what == 0) {
                super.handleMessage(msg);
                if (null != mDialogShower) {
                    mDialogShower.dismissDialog();
                }
            }
        }

        public void setMDialogShower(@NonNull DialogShower dialogShower) {
            mDialogShower = dialogShower;
        }
    }
}
