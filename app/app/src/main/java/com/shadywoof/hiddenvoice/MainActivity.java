package com.shadywoof.hiddenvoice;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editTextRoomName;
    private Spinner spinnerRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextRoomName = findViewById(R.id.editTextRoomName);
        spinnerRooms = findViewById(R.id.spinnerRooms);

        String[] rooms = {"Select a room", "room1", "room2", "room3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, rooms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRooms.setAdapter(adapter);

        findViewById(R.id.btnSpeaker).setOnClickListener(v -> {
            String roomName = getSelectedRoomName();
            if (roomName.isEmpty()) return;
            Intent intent = new Intent(this, SpeakerActivity.class);
            intent.putExtra("ROOM_NAME", roomName);
            startActivity(intent);
        });

        findViewById(R.id.btnListener).setOnClickListener(v -> {
            String roomName = getSelectedRoomName();
            if (roomName.isEmpty()) return;
            Intent intent = new Intent(this, ListenerActivity.class);
            intent.putExtra("ROOM_NAME", roomName);
            startActivity(intent);
        });
    }

    private String getSelectedRoomName() {
        String roomName = editTextRoomName.getText().toString().trim();
        if (roomName.isEmpty()) {
            String selectedRoom = spinnerRooms.getSelectedItem().toString();
            if (selectedRoom.equals("Select a room")) {
                editTextRoomName.setError("Please enter a room name or select one");
                return "";
            }
            roomName = selectedRoom;
        }
        return roomName;
    }
}