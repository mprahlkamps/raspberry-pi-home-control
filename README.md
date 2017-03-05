# Raspberry Pi Home Control

Raspberry Pi Home Control (RPHC) is a Websocket server written in Java, to simplify and unify
the control of connected Led's, remote sockets and other hardware.

For a sample website communicating with RPHC, please look at [this](https://github.com/mprahlkamps/rphc-website)
repository.

## Getting started with RPHC

todo

---

# For Developers

## Todo

- [ ] make tutorial how to setup rphc (with server part)
- [ ] support more socket types
- [ ] make json parsing more fail-safe
- [ ] support single addressable led strips
- [ ] support more hardware

## Protocol

RPHC uses a JSON message protocol.

**_Control LED strips_**

When there are led strips available you can control them like this:

``` json
{
    "type": "control",
    "module": "led_controller",
    "controller_id": 0,
    "red": 255,
    "green": 0,
    "blue": 0
}
```

to set the first led controller to fully red.

**_Control remote sockets_**

When there are remote sockets available you'll be able to control them like this:

``` json
{
    "type": "control",
    "module": "remote_socket_controller",
    "controller_id": 0,
    "socket_id": 0,
    "value": 1
}
```

to turn the first socket of the first controller to ON.

**_Connection_**

When a client connects to the server, the server sends a welcome message alongside
useful information to setup the client.

``` json
{
    "type":"welcome",
    "available-modules":
    {
        "led_controller":
        [
            {
                "name":"LEDs 1"
            }
        ],
        "remote_socket_controller":
        [
            {
                "name":"Socket Controller 1",
                "sockets":
                [
                    {
                        "name":"Socket 1"
                    },
                    {
                        "name":"Socket 2"
                    }
                ]
            }
        ]
    }
}
```

**_Errors_**

When a client sends a malformed message the server replies with this error message

``` json
{
    "type": "error",
    "message": "Cause of the error"
}
```