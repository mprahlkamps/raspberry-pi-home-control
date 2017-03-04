package de.pk.rphc.modules;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import de.pk.rphc.model.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ModuleController {

	private Logger logger;

	private LedController[] ledController;
	private LightController[] lightController;
	private MusicController musicController;

	private ModuleController() {
		logger = LoggerFactory.getLogger(ModuleController.class);
	}

	public void loadModules() {
		logger.info("Loading Modules...");

		JsonObject configuration = loadModuleConfiguration();

		if (configuration.has("led_controller")) {
			JsonArray ledStripArray = configuration.getAsJsonArray("led_controller");
			ledController = new LedController[ledStripArray.size()];

			for (int i = 0; i < ledStripArray.size(); i++) {
				JsonObject ledStrip = (JsonObject) ledStripArray.get(i);
				if (ledStrip.get("enabled").getAsBoolean()) {
					logger.info("Activating " + ledStrip.get("name").getAsString());

					Pin redPin = RaspiPin.getPinByAddress(ledStrip.get("gpio_red").getAsInt());
					Pin greenPin = RaspiPin.getPinByAddress(ledStrip.get("gpio_green").getAsInt());
					Pin bluePin = RaspiPin.getPinByAddress(ledStrip.get("gpio_blue").getAsInt());

					ledController[i] = new LedController(ledStrip.get("name").getAsString(), redPin, greenPin, bluePin);
					ledController[i].initialize();
				}
			}
		}

		if (configuration.has("remote_socket_controller")) {
			JsonArray transmitterArray = configuration.getAsJsonArray("remote_socket_controller");
			lightController = new LightController[transmitterArray.size()];

			for (int i = 0; i < transmitterArray.size(); i++) {
				JsonObject transmitter = (JsonObject) transmitterArray.get(i);
				if (transmitter.get("enabled").getAsBoolean()) {
					logger.info("Activating " + transmitter.get("name").getAsString());

					Pin transmitterPin = RaspiPin.getPinByAddress(transmitter.get("gpio_transmit").getAsInt());

					JsonArray socketArray = transmitter.getAsJsonArray("sockets");
					Socket[] sockets = new Socket[socketArray.size()];

					for (int j = 0; j < socketArray.size(); j++) {
						JsonObject socket = (JsonObject) socketArray.get(j);
						sockets[j] = new Socket(socket.get("name").getAsString(), socket.get("group").getAsString(), socket.get("device").getAsString());
					}

					lightController[i] = new LightController(transmitter.get("name").getAsString(), transmitterPin, sockets);
					lightController[i].initialize();
				}
			}
		}

		logger.info("Done loading Modules!");
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

	public JsonObject getAvailableModules() {
		JsonObject modules = new JsonObject();
		JsonArray ledStripModules = new JsonArray();

		for (int i = 0; i < ledController.length; i++) {
			if (ledController[i] != null) {
				JsonObject ledStrip = new JsonObject();
				ledStrip.addProperty("name", ledController[i].getName());
				ledStripModules.add(ledStrip);
			}
		}

		modules.add("led_controller", ledStripModules);

		JsonArray transmitterModules = new JsonArray();

		for (int i = 0; i < lightController.length; i++) {
			if (lightController[i] != null) {
				JsonObject transmitter = new JsonObject();
				transmitter.addProperty("name", lightController[i].getName());

				JsonArray socketArray = new JsonArray();
				Socket[] sockets = lightController[i].getSockets();
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
		return ledController[index];
	}

	public LightController getLightController(int index) {
		return lightController[index];
	}

	public static ModuleController getInstance() {
		return ModuleControllerHolder.INSTANCE;
	}

	private static class ModuleControllerHolder {
		private static final ModuleController INSTANCE = new ModuleController();
	}

}
