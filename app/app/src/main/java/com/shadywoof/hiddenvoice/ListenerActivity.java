package com.shadywoof.hiddenvoice;

import android.os.Bundle;
import android.media.AudioFormat;
import android.media.AudioTrack;
import androidx.appcompat.app.AppCompatActivity;
import android.media.AudioManager;
import android.widget.TextView;
import java.io.InputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;

public class ListenerActivity extends AppCompatActivity {
    private Socket socket;
    private InputStream inputStream;
    private AudioTrack audioTrack;
    private boolean isListening = true;
    private TextView textViewCallStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener);

        String roomName = getIntent().getStringExtra("ROOM_NAME");
        TextView textViewRoomName = findViewById(R.id.textViewRoomName);
        textViewRoomName.setText(roomName);
        textViewCallStatus = findViewById(R.id.textViewCallStatus);

        findViewById(R.id.btnEndCall).setOnClickListener(v -> finish());

        new Thread(() -> {
            try {
                socket = new Socket("127.0.0.1", 5000);
                inputStream = socket.getInputStream();
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("LISTENER");
                writer.println(roomName);

                int sampleRate = 16000;
                int minBufSize = AudioTrack.getMinBufferSize(sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        minBufSize, AudioTrack.MODE_STREAM);

                audioTrack.play();

                byte[] buffer = new byte[minBufSize];
                int count;
                while (isListening && (count = inputStream.read(buffer)) > 0) {
                    audioTrack.write(buffer, 0, count);
                }

            } catch (IOException e) {
                runOnUiThread(() -> {
                    textViewCallStatus.setText("Connection Error");
                    textViewCallStatus.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                });
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isListening = false;
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
        }
    }
}