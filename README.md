# Hidden Voice

Hidden Voice is a simple Android voice chat application that allows users to create or join rooms and communicate as either a Speaker or a Listener. The app connects to a Python-based server to relay audio between participants in real-time.

## Features
- Create or join voice chat rooms with custom names.
- Join as a Speaker to send audio or as a Listener to receive audio.
- User-friendly interface with call status indicators and icons.
- End call functionality with a clear UI.
- Server-side support for multiple rooms and proper audio relay.

## Screenshots

The main screen allows users to enter a room name or select from a list of existing rooms and join as either a Speaker or Listener.

<br /><br />
<h2 align="center">Preview</h2>

Main             |  Room
:-------------------------:|:-------------------------:
<img src="https://raw.githubusercontent.com/ayukistudio/HiddenVoice/refs/heads/main/img/1.jpg?token=GHSAT0AAAAAADCVORLL44STG7K4BKNK66OW2AKYV5A" width="700">  |  <img src="https://raw.githubusercontent.com/ayukistudio/HiddenVoice/refs/heads/main/img/2.jpg?token=GHSAT0AAAAAADCVORLLJT7KHXRNIZE5XISG2AKYZ5A" width="700">

<br />

## Prerequisites

### Client (Android App)
- Android device or emulator running Android 5.0 (Lollipop) or higher.
- `RECORD_AUDIO` and `INTERNET` permissions must be granted (declared in `AndroidManifest.xml`).

### Server
- Python 3.6 or higher.
- A publicly accessible IP address and port (e.g., `127.0.0.1:5000` in the code).
- Port forwarding if running on a local network.

## Setup

### Server Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/ayukistudio/HiddenVoice.git
   cd HiddenVoice
   ```
2. Run the server:
   ```bash
   python server.py
   ```
   - The server will start on the specified `HOST` and `PORT` (default: `127.0.0.1:5000`).
   - Ensure the IP and port are accessible and match the values in the Android app.

### Android App Setup
1. Open the project in Android Studio.
2. Update the `Socket` connection in `SpeakerActivity.java` and `ListenerActivity.java` to use the correct server IP and port if necessary:
   ```java
   socket = new Socket("127.0.0.1", 5000);
   ```
3. Build and run the app on an Android device or emulator.
4. Grant the required permissions (`RECORD_AUDIO` and `INTERNET`) when prompted.

## Usage
1. **Start the Server:**
   - Run `server.py` on your server machine.
   - The server will log connections and room activity.

2. **Launch the App:**
   - Open the Hidden Voice app on your Android device.
   - On the main screen, enter a room name (e.g., `room1`) or select an existing room from the dropdown.
   - Tap "Join as Speaker" to send audio or "Join as Listener" to receive audio.

3. **During a Call:**
   - The Speaker or Listener screen will show the room name and connection status.
   - Tap "End Call" to disconnect and return to the main screen.

## Project Structure
- `app/src/main/java/com/shadywoof/hiddenvoice/` - Android app source code.
  - `MainActivity.java` - Entry point for selecting or creating rooms.
  - `SpeakerActivity.java` - Handles audio recording and sending.
  - `ListenerActivity.java` - Handles audio playback.
- `app/src/main/res/layout/` - XML layouts for the app.
  - `activity_main.xml` - Main screen layout.
  - `activity_speaker.xml` - Speaker screen layout.
  - `activity_listener.xml` - Listener screen layout.
- `server.py` - Python server script for handling voice chat connections.
- `img/` - Directory containing screenshots for the README.

## Limitations
- The app currently uses a hardcoded list of rooms in the `Spinner`. Future updates could fetch the list dynamically from the server.
- Only one Speaker is allowed per room. Additional Speakers will be rejected by the server.
- No mute/unmute functionality (planned for future updates).
- No call duration timer (planned for future updates).

## Contributing
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Make your changes and commit (`git commit -m "Add your feature"`).
4. Push to your branch (`git push origin feature/your-feature`).
5. Open a Pull Request.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Author
- [@AyukiDev](https://github.com/ayukistudio)