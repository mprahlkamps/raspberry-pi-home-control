package de.pk.rphc.modules;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModuleController {

	private static final int MAX_LED_STRIPS = 5;
	private static final int MAX_SA_LED_STRIPS = 10;

	private Logger logger;
	private Properties moduleProperties;

	private LedController[] ledController;
	private SaLedController[] saLedController;
	private LightController lightController;
	private MusicController musicController;

	private ModuleController() {
		logger = LoggerFactory.getLogger(ModuleController.class);
		ledController = new LedController[MAX_LED_STRIPS];
		saLedController = new SaLedController[MAX_SA_LED_STRIPS];
	}

	public void loadModules() {
		loadProperties();

		logger.info("Loading Modules...");

		for (int i = 0; i < MAX_LED_STRIPS; i++) {
			int configCount = i + 1;

			if (moduleProperties.containsKey("enable_led_controller_" + configCount)) {
				if (moduleProperties.getProperty("enable_led_controller_" + configCount).equals("true")) {
					logger.info("Enabling LedController " + configCount);

					int redGPIO = Integer.parseInt(moduleProperties.getProperty("gpio_led_r_" + configCount, "0"));
					int greenGPIO = Integer.parseInt(moduleProperties.getProperty("gpio_led_g_" + configCount, "2"));
					int blueGPIO = Integer.parseInt(moduleProperties.getProperty("gpio_led_b_" + configCount, "3"));

					Pin redPin = RaspiPin.getPinByAddress(redGPIO);
					Pin greenPin = RaspiPin.getPinByAddress(greenGPIO);
					Pin bluePin = RaspiPin.getPinByAddress(blueGPIO);

					ledController[i] = new LedController(redPin, greenPin, bluePin);
					ledController[i].initLeds();
				}
			}
		}

		for (int i = 0; i < MAX_SA_LED_STRIPS; i++) {
			int configCount = i + 1;

			if (moduleProperties.containsKey("enable_sa_led_controller_" + configCount)) {
				if (moduleProperties.getProperty("enable_sa_led_controller_" + configCount).equals("true")) {
					logger.warn("SA LED Controller not implemented yet!");
				}
			}
		}

		if (moduleProperties.getProperty("enable_light_controller").equals("true")) {
			logger.info("Enabling LightController");

			int transmitterGpio = Integer.parseInt(moduleProperties.getProperty("gpio_light_transmitter", "1"));
			lightController = new LightController(RaspiPin.getPinByAddress(transmitterGpio));
		}

		logger.info("Done loading Modules!");
	}

	public JSONArray getAvailableModules() {
		return new JSONArray();
	}

	private void loadProperties() {
		moduleProperties = new Properties();

		try {
			InputStream in = new FileInputStream("modules.properties");
			moduleProperties.load(in);
		} catch (FileNotFoundException e) {
			logger.error("Error loading modules.moduleProperties", e);
		} catch (IOException e) {
			logger.error("Error loading modules.moduleProperties", e);
		}
	}

	public static ModuleController getInstance() {
		return ModuleControllerHolder.INSTANCE;
	}

	private static class ModuleControllerHolder {
		private static final ModuleController INSTANCE = new ModuleController();
	}

}
