package de.pk.rphc.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.pk.rphc.modules.ModuleController;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
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

		JsonObject jsonMessage = (JsonObject) new JsonParser().parse(message);

		if (jsonMessage.get("type").getAsString().equals("control")) {

			if (jsonMessage.get("module").getAsString().equals("remote_socket_controller")) {
				int controllerId = jsonMessage.get("controller_id").getAsInt();
				int socketId = jsonMessage.get("socket_id").getAsInt();
				int value = jsonMessage.get("value").getAsInt();

				if (value == 1) {
					moduleController.getLightController(controllerId).switchOn(socketId);
				} else {
					moduleController.getLightController(controllerId).switchOff(socketId);
				}

			} else if (jsonMessage.get("module").getAsString().equals("led_controller")) {
				int controllerId = jsonMessage.get("controller_id").getAsInt();

				int red = jsonMessage.get("red").getAsInt();
				int green = jsonMessage.get("green").getAsInt();
				int blue = jsonMessage.get("blue").getAsInt();

				moduleController.getLedController(controllerId).setLedColor(new Color(red, green, blue));
			}

		}
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
		JsonObject welcomeMessage = new JsonObject();
		welcomeMessage.addProperty("type", "welcome");
		welcomeMessage.add("available-modules", moduleController.getAvailableModules());
		connection.send(welcomeMessage.toString());
	}
}
