package de.pk.rphc.server;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlServer extends WebSocketServer {

	private Logger logger;

	public ControlServer(InetSocketAddress address) {
		super(address);

		logger = LoggerFactory.getLogger(ControlServer.class);
		logger.info("Staring Control Server (" + address + ")");
	}

	@Override
	public void onMessage(WebSocket connection, String message) {
		logger.debug("(" + connection.getRemoteSocketAddress() + ") New message: " + message);
	}

	@Override
	public void onOpen(WebSocket connection, ClientHandshake handshake) {
		logger.info("(" + connection.getRemoteSocketAddress() + ") New Connection");
	}

	@Override
	public void onClose(WebSocket connection, int code, String reason, boolean remote) {
		logger.info("(" + connection.getRemoteSocketAddress() + ") Connection closed");
	}

	@Override
	public void onError(WebSocket connection, Exception exception) {
		logger.error("(" + connection.getRemoteSocketAddress() + ") Error", exception);
	}

}
