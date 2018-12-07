package de.pk.rphc.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import de.pk.rphc.model.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ModuleController {

	private Logger logger;

	private ArrayList<LedController> ledController;
	private ArrayList<SaLedController> saLedController;
	private ArrayList<SocketController> socketController;

	private ModuleController() {
		logger = LoggerFactory.getLogger(ModuleController.class);

		ledController = new ArrayList<LedController>(0);
		saLedController = new ArrayList<SaLedController>(0);
		socketController = new ArrayList<SocketController>(0);

	}

	private JsonObject loadModuleConfiguration() {
		JsonObject configuration = null;

		try {
			FileReader reader = new FileReader("modules.json");
			configuration = (JsonObject) new JsonParser().parse(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return configuration;
	}

	public void loadModules() {
		logger.info("Loading Modules...");

		JsonObject configuration = loadModuleConfiguration();

		if (configuration.has("led_controller")) {
			JsonArray ledControllerArray = configuration.getAsJsonArray("led_controller");
			ledController = new ArrayList<LedController>(ledControllerArray.size());

			for (int i = 0; i < ledControllerArray.size(); i++) {
				JsonObject ledController = (JsonObject) ledControllerArray.get(i);
				if (ledController.get("enabled").getAsBoolean()) {
					logger.info("Activating LEDs (" + ledController.get("name").getAsString() + ")");

					Pin redPin = RaspiPin.getPinByAddress(ledController.get("gpio_red").getAsInt());
					Pin greenPin = RaspiPin.getPinByAddress(ledController.get("gpio_green").getAsInt());
					Pin bluePin = RaspiPin.getPinByAddress(ledController.get("gpio_blue").getAsInt());

					LedController newLedController = new LedController(ledController.get("name").getAsString(), redPin, greenPin, bluePin);
					newLedController.initialize();

					this.ledController.add(newLedController);
				}
			}
		}

		if (configuration.has("sa_led_controller")) {
			JsonArray saLedControllerArray = configuration.getAsJsonArray("sa_led_controller");
			saLedController = new ArrayList<SaLedController>(saLedControllerArray.size());

			for (int i = 0; i < saLedControllerArray.size(); i++) {
				JsonObject ledController = (JsonObject) saLedControllerArray.get(i);
				if (ledController.get("enabled").getAsBoolean()) {
					logger.info("Activating SA LEDs (" + ledController.get("name").getAsString() + ")");

					SaLedController newSaLedController = new SaLedController(ledController.get("name").getAsString(), ledController.get("led_count").getAsInt());
					newSaLedController.initialize();

					saLedController.add(newSaLedController);
				}
			}
		}

		if (configuration.has("remote_socket_controller")) {
			JsonArray remoteSocketControllerArray = configuration.getAsJsonArray("remote_socket_controller");
			socketController = new ArrayList<SocketController>(remoteSocketControllerArray.size());

			for (int i = 0; i < remoteSocketControllerArray.size(); i++) {
				JsonObject remoteSocketController = (JsonObject) remoteSocketControllerArray.get(i);
				if (remoteSocketController.get("enabled").getAsBoolean()) {
					logger.info("Activating Remote Socket Controller (" + remoteSocketController.get("name").getAsString() + ")");

					Pin transmitterPin = RaspiPin.getPinByAddress(remoteSocketController.get("gpio_transmit").getAsInt());

					JsonArray socketArray = remoteSocketController.getAsJsonArray("sockets");
					Socket[] sockets = new Socket[socketArray.size()];

					for (int j = 0; j < socketArray.size(); j++) {
						JsonObject socket = (JsonObject) socketArray.get(j);
						sockets[j] = new Socket(socket.get("name").getAsString(), socket.get("group").getAsString(), socket.get("device").getAsString());
					}

					SocketController newSocketController = new SocketController(remoteSocketController.get("name").getAsString(), transmitterPin, sockets);
					newSocketController.initialize();

					socketController.add(newSocketController);
				}
			}
		}

		logger.info("Done loading Modules!");
	}

	public JsonObject getAvailableModules() {
		JsonObject modules = new JsonObject();

		JsonArray ledStripModules = new JsonArray();
		for (LedController aLedController : ledController) {
			if (aLedController != null) {
				JsonObject ledStrip = new JsonObject();
				ledStrip.addProperty("name", aLedController.getName());
				ledStripModules.add(ledStrip);
			}
		}
		modules.add("led_controller", ledStripModules);

		JsonArray saLedStripModules = new JsonArray();
		for (SaLedController aSaLedController : saLedController) {
			if (aSaLedController != null) {
				JsonObject saLedStrip = new JsonObject();
				saLedStrip.addProperty("name", aSaLedController.getName());
				saLedStripModules.add(saLedStrip);
			}
		}
		modules.add("sa_led_controller", saLedStripModules);

		JsonArray transmitterModules = new JsonArray();
		for (SocketController aSocketController : socketController) {
			if (aSocketController != null) {
				JsonObject transmitter = new JsonObject();
				transmitter.addProperty("name", aSocketController.getName());

				JsonArray socketArray = new JsonArray();
				Socket[] sockets = aSocketController.getSockets();
				for (int j = 0; j < sockets.length; j++) {
					JsonObject socket = new JsonObject();
					socket.addProperty("name", sockets[j].name);
					socketArray.add(socket);
				}
				transmitter.add("sockets", socketArray);
				transmitterModules.add(transmitter);
			}
		}

		modules.add("remote_socket_controller", transmitterModules);

		return modules;
	}

	public LedController getLedController(int index) {
		return ledController.get(index);
	}

	public SaLedController getSaLedController(int index) {
		return saLedController.get(index);
	}

	public SocketController getLightController(int index) {
		return socketController.get(index);
	}

	public static ModuleController getInstance() {
		return ModuleControllerHolder.INSTANCE;
	}

	public void stop() {
		for (LedController aLedController : ledController) {
			aLedController.stop();
		}

		for (SaLedController aSaLedController : saLedController) {
			aSaLedController.stop();
		}

		for (SocketController aSocketController : socketController) {
			aSocketController.stop();
		}

		logger.info("Shutting down GPIO Controller");
		GpioFactory.getInstance().shutdown();
	}

	private static class ModuleControllerHolder {
		private static final ModuleController INSTANCE = new ModuleController();
	}

}
