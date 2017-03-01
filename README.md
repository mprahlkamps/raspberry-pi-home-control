# Raspberry Pi Home Control

## Protocol

RPHC uses a JSON message protocol.
 
 ### Connection
 
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