package de.pk.rphc.server;

import de.pk.rphc.modules.ModuleController;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class ServerControl extends WebSocketServer {

	private Logger logger;
	private ModuleController moduleController;

	public ServerControl(InetSocketAddress address) {
		super(address);

		logger = LoggerFactory.getLogger(ServerControl.class);
		logger.info("Starting Control Server (" + address + ")");

		moduleController = ModuleController.getInstance();

	}

	@Override
	public void onMessage(WebSocket connection, String message) {
		logger.debug("(" + connection.getRemoteSocketAddress() + ") New message: " + message);


	}

	@Override
	public void onOpen(WebSocket connection, ClientHandshake handshake) {
		logger.info("(" + connection.getRemoteSocketAddress() + ") New Connection");
		sendWelcomeMessage(connection);
	}

	@Override
	public void onClose(WebSocket connection, int code, String reason, boolean remote) {
		logger.info("(" + connection.getRemoteSocketAddress() + ") Connection closed");
	}

	@Override
	public void onError(WebSocket connection, Exception exception) {
		logger.error("(" + connection.getRemoteSocketAddress() + ") Error", exception);
	}

	private void sendWelcomeMessage(WebSocket connection) {
		JSONObject message = new JSONObject();
		message.put("type", "welcome");

		JSONArray availableModules = new JSONArray();
		message.put("available-modules", availableModules);

		JSONObject ledControllerModule = new JSONObject();
		ledControllerModule.put("name", "LedController");
		availableModules.put(ledControllerModule);

		JSONObject lightControllerModule = new JSONObject();
		lightControllerModule.put("name", "LightController");
		lightControllerModule.put("available-lights", "5");
		availableModules.put(lightControllerModule);

		connection.send(message.toString());
	}
}
