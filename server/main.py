import socket
import threading
import json

HOST = '127.0.0.1'
PORT = 5000

rooms = {}
lock = threading.Lock()

def read_until_newline(conn):
    buffer = b""
    while True:
        data = conn.recv(1)
        if not data:
            return None
        buffer += data
        if data == b"\n":
            return buffer.decode().strip()

def handle_client(conn, addr):
    try:
        mode = read_until_newline(conn)
        if not mode:
            return

        if mode.upper() == "GET_ROOMS":
            with lock:
                room_list = list(rooms.keys())
            conn.sendall((json.dumps(room_list) + "\n").encode())
            return

        room_id = read_until_newline(conn)
        if not room_id:
            return

        print(f"[{addr}] Mode: {mode}, Room: {room_id}")

        with lock:
            if room_id not in rooms:
                rooms[room_id] = {"speaker": None, "listeners": []}
            room = rooms[room_id]

        if mode.upper() == "SPEAKER":
            with lock:
                if room["speaker"] is not None:
                    conn.sendall(b"ERROR: Speaker already exists in this room\n")
                    return
                room["speaker"] = conn

            while True:
                data = conn.recv(1024)
                if not data:
                    break
                with lock:
                    for listener in room["listeners"]:
                        try:
                            listener.sendall(data)
                        except:
                            pass

        elif mode.upper() == "LISTENER":
            with lock:
                room["listeners"].append(conn)
            while True:
                try:
                    if conn.recv(1) == b'':
                        break
                except:
                    break

    except Exception as e:
        print(f"[{addr}] Error: {e}")
    finally:
        print(f"[{addr}] Disconnected")
        with lock:
            for room_id, room in list(rooms.items()):
                if conn in room["listeners"]:
                    room["listeners"].remove(conn)
                if room.get("speaker") == conn:
                    room["speaker"] = None
                if not room["speaker"] and not room["listeners"]:
                    del rooms[room_id]
        conn.close()

def start_server():
    print(f"🎙 Voice Chat Server running on {HOST}:{PORT}")
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server:
        server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server.bind((HOST, PORT))
        server.listen()
        while True:
            conn, addr = server.accept()
            threading.Thread(target=handle_client, args=(conn, addr), daemon=True).start()

if __name__ == "__main__":
    start_server()
