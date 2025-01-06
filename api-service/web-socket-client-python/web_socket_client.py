import asyncio
import json

import websockets

async def connect_to_server():
  uri = "ws://localhost:8080/quarkus-demo-api/ws"
  try:
    async with websockets.connect(uri) as websocket:
      message = {"message": "Hello!"}
      await websocket.send(json.dumps(message))
      print(f"Sent: {message}")

      response = await websocket.recv()
      print(f"Received: {response}")

  except Exception as e:
    print(f"Error: {e}")

if __name__ == "__main__":
  asyncio.run(connect_to_server())