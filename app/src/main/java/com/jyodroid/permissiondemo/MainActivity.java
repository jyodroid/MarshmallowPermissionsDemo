package com.jyodroid.permissiondemo;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static String mFileName = null;

    private FloatingActionButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private FloatingActionButton mPlayButton = null;
    private MediaPlayer mPlayer = null;

    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;

    private boolean isAskingForPermission = false;
    private PermissionsUtility.HandleMessage handleMessage;

    public MainActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecordButton = (FloatingActionButton) findViewById(R.id.fab_start_recording);
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    Snackbar.make(view, "Starting recording", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Stopping recording", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                mStartRecording = !mStartRecording;
            }
        });

        mPlayButton = (FloatingActionButton) findViewById(R.id.fab_play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    Snackbar.make(view, "Starting Playing", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Stopping Playing", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                mStartPlaying = !mStartPlaying;
            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();
        //Ask for camera and write permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isAskingForPermission) {
            try {
                //Obtain permission type required
                int requiredPermissionType = PermissionsUtility.obtainPermissionType(this);
                if (requiredPermissionType != PermissionsUtility.MY_PERMISSIONS_UNKNOWN) {
                    isAskingForPermission = true;
                    //Prepare Thread to show message and handler to management
                    handleMessage = new PermissionsUtility.HandleMessage();
                    PermissionsUtility.requestCardPicturePermissions(
                            requiredPermissionType, this, handleMessage);
                }
            } catch (Exception e) {
                //TODO: handle error
                Log.e(LOG_TAG, "NOT RECOGNIZED PERMISSION", e);
                finish();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        isAskingForPermission = false;
        boolean permissionRequiredGranted = false;
        switch (requestCode) {
            case PermissionsUtility.MY_PERMISSIONS_MULTIPLE:
                if (grantResults.length > 0) {
                    for (int grantedCode : grantResults) {
                        permissionRequiredGranted = true;
                        if (grantedCode == PackageManager.PERMISSION_GRANTED) {
                            Log.v(LOG_TAG, "permission granted!!");
                        } else {
                            Log.v(LOG_TAG, "one permission not granted");
                            permissionRequiredGranted = false;
                            break;
                        }
                    }
                }
                break;
            case PermissionsUtility.MY_PERMISSIONS_REQUEST_AUDIO_RECORD_PERMISSION:
                if (grantResults.length > 0 &&
                        PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    Log.v(LOG_TAG, "Camera permission granted!!");
                    permissionRequiredGranted = true;
                }
                break;
            case PermissionsUtility.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0 &&
                        PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                    Log.v(LOG_TAG, "Write external storage permission granted!!");
                    permissionRequiredGranted = true;
                }
                break;
        }

        if (!permissionRequiredGranted) {
            Log.v(LOG_TAG, "Permission not granted");

            //Verify if user check on "Never ask again" and take him to application settings to
            //Grant permission manually
            if (PermissionsUtility.isCheckedDontAskAgain(
                    this, PermissionsUtility.RECORD_PERMISSION) ||
                    PermissionsUtility.isCheckedDontAskAgain(
                            this, PermissionsUtility.WRITE_EXTERNAL_STORAGE_PERMISSION)) {
                if (handleMessage == null) {
                    handleMessage = new PermissionsUtility.HandleMessage();
                }
                isAskingForPermission = true;
                PermissionsUtility.showPermissionMessage(
                        "Permissions for settings",
                        "Get me to settings",
                        "Latter",
                        null,
                        this,
                        PermissionsUtility.MY_PERMISSIONS_UNKNOWN,
                        handleMessage);

            } else {
                finish();
            }
        } else {
            if (mRecorder != null) {
                mRecorder.release();
                mRecorder = null;
            }

            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
