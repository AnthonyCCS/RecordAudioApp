package com.accs.audioapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static MediaRecorder mediaRecorder;
    private static MediaRecorder mediaRecorder2;
    private static MediaPlayer mediaPlayer;

    private String audioFilePath;
    private String audioFilePath_Internal;
    private  Button stopButton;
    private  Button playButton;
    private  Button recordButton;

    private boolean isRecording = false;

    private static final int RECORD_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioSetup();
    }

    private void audioSetup(){
        recordButton = findViewById(R.id.recordButton);
        playButton = findViewById(R.id.playButton);
        stopButton = findViewById(R.id.stopButton);

        if(!hasMicrophone()){
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            recordButton.setEnabled(false);
        }
        else{
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
        }

        audioFilePath_Internal = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC).
                getAbsolutePath() +"/myaudio.3gp";

        audioFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).
                getAbsolutePath()+"/myaudio3.mp3";
        

        requestPermission(Manifest.permission.RECORD_AUDIO, RECORD_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[]grantResults){
        switch(requestCode){
            case RECORD_REQUEST_CODE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    recordButton.setEnabled(false);
                    Toast.makeText(this, "Record Permission required", Toast.LENGTH_LONG).
                            show();
                } else {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_REQUEST_CODE);
                }
                return;
            }

            case STORAGE_REQUEST_CODE:{
                if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    recordButton.setEnabled(false);
                    Toast.makeText(this, "External Storage permission required",
                            Toast.LENGTH_LONG).show();
                }
            }

        }
    }




    protected boolean hasMicrophone(){
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }



    public void recordAudio(View view){
        isRecording = true;
        stopButton.setEnabled(true);
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        try{
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setOutputFile(audioFilePath_Internal);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();

            mediaRecorder2 = new MediaRecorder();
            mediaRecorder2.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder2.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder2.setOutputFile(audioFilePath);
            mediaRecorder2.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder2.prepare();


        }
        catch (Exception e){
            e.printStackTrace();
        }
        mediaRecorder.start();
        mediaRecorder2.start();
    }

    public void stopAudio(View view){
        stopButton.setEnabled(false);
        playButton.setEnabled(true);

        if(isRecording) {
            recordButton.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            mediaRecorder2.stop();
            mediaRecorder2.release();
            mediaRecorder2 = null;
            isRecording = false;
        }
        else{
            mediaPlayer.release();
            mediaPlayer = null;
            recordButton.setEnabled(true);
        }
    }


    public void playAudio(View view) throws IOException{
        playButton.setEnabled(false);
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(audioFilePath_Internal);
        //mediaPlayer.setDataSource(audioFilePath);
        mediaPlayer.prepare();
        mediaPlayer.start();

    }

    protected void requestPermission(String permissionType, int requestCode){
        int permission = ContextCompat.checkSelfPermission(this, permissionType);

        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permissionType},requestCode);
        }
    }











}//MainActivity