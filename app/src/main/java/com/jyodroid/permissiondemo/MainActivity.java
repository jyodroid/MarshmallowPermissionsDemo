package com.jyodroid.permissiondemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected static final int RESULT_SPEECH = 1;
    TextView txtSpeechResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabSpeech = (FloatingActionButton) findViewById(R.id.fab_speech);
        fabSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechRecognition();
            }
        });

        Button fabRecord = (Button) findViewById(R.id.btn_record_activity);
        final Activity mainActivity = this;
        fabRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, RecordActivity.class);
                mainActivity.startActivity(intent);
            }
        });

        txtSpeechResult = (TextView) findViewById(R.id.speech_result);
    }

    //Catch speech results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtSpeechResult.setText(text.get(0));
                }else if (resultCode == RESULT_CANCELED){
                    Toast toast = new Toast(this);
                    toast.makeText(this, "Cancelled operation", Toast.LENGTH_LONG);
                }
                break;
            }
        }
    }

    private void speechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        startActivityForResult(intent, RESULT_SPEECH);
    }
}
