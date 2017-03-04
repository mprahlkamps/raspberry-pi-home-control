# Raspberry Pi Home Control

## Todo

-[ ] make tutorial how to setup rphc (with server part)
-[ ] support more socket types
-[ ] make json parsing more fail-safe
-[ ] support single addressable led strips

## Protocol

RPHC uses a JSON message protocol.
 
 **_Connection_**
 
 When a client connects to the server, the server sends a welcome message alongside
 useful information to setup the client.
 
 ``` json
 {
   "type": "welcome",
   "available-modules": []
 }
 ```
 
 * **type** - the type of the message
 * **available-modules** - an array representing available modules on this server and
    information about them

 **_Control LED strips_**
 
 When there are led strips available you can control them like this:
 
 ``` json
   {
     "type": "control",
     "module": "led_controller",
     "controller_id": 0,
     "red": 0-255,
     "green": 0-255,
     "blue": 0-255
   }
   ```
 
 
 **_Control remote sockets_**
 
 When there are remote sockets available you'll be able to control them like this:
 
  ``` json
  {
    "type": "control",
    "module": "remote_socket_controller",
    "controller_id": 0,
    "socket_id": 0,
    "value": 0/1
  }
  ```
  
  to turn the first socket of the first controller to ON.