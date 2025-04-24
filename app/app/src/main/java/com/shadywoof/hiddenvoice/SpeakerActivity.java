package com.shadywoof.hiddenvoice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SpeakerActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private AudioRecord recorder;
    private boolean isRecording = true;
    private Socket socket;
    private OutputStream outputStream;
    private TextView textViewCallStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker);

        String roomName = getIntent().getStringExtra("ROOM_NAME");
        TextView textViewRoomName = findViewById(R.id.textViewRoomName);
        textViewRoomName.setText(roomName);
        textViewCallStatus = findViewById(R.id.textViewCallStatus);

        findViewById(R.id.btnEndCall).setOnClickListener(v -> finish());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            startRecording(roomName);
        }
    }

    private void startRecording(String roomName) {
        new Thread(() -> {
            try {
                socket = new Socket("127.0.0.1", 5000);
                outputStream = socket.getOutputStream();
                outputStream.write("SPEAKER\n".getBytes());
                outputStream.write((roomName + "\n").getBytes());

                int sampleRate = 16000;
                int minBufSize = AudioRecord.getMinBufferSize(sampleRate,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize);

                try {
                    recorder.startRecording();
                } catch (SecurityException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        textViewCallStatus.setText("Permission Error");
                        textViewCallStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        Toast.makeText(this, "Permission error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                byte[] buffer = new byte[minBufSize];
                while (isRecording) {
                    int read = recorder.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        outputStream.write(buffer, 0, read);
                    }
                }
            } catch (IOException e) {
                runOnUiThread(() -> {
                    textViewCallStatus.setText("Connection Error");
                    textViewCallStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    Toast.makeText(SpeakerActivity.this, "Connection error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRecording = false;
        if (recorder != null) {
            recorder.stop();
            recorder.release();
        }
        try {
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording(getIntent().getStringExtra("ROOM_NAME"));
            } else {
                textViewCallStatus.setText("Permission Denied");
                textViewCallStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                Toast.makeText(this, "Permission denied. Cannot record audio.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}