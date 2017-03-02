package de.pk.rphc.modules;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModuleController {

	private Logger logger;
	private Properties moduleProperties;

	private LedController ledController;
	private LightController lightController;
	private MusicController musicController;

	private ModuleController() {
		logger = LoggerFactory.getLogger(ModuleController.class);
	}

	public void loadModules() {
		loadProperties();

		logger.info("Loading Modules...");

		if (moduleProperties.getProperty("enable_led_controller").equals("true")) {
			logger.info("Enabling LedController");
			ledController = new LedController(moduleProperties);
			ledController.initLeds();
		}

		if (moduleProperties.getProperty("enable_light_controller").equals("true")) {
			logger.info("Enabling LightController");
			lightController = new LightController();
			// TODO: init light controller
		}

		logger.info("Done loading Modules!");
	}

	public JSONObject getAvailableModules() {
		JSONObject availableModules = new JSONObject();

		return availableModules;
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
