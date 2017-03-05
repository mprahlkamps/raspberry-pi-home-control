package de.pk.rphc;

import de.pk.rphc.modules.ModuleController;
import de.pk.rphc.server.ServerControl;
import de.pk.rphc.speech.SpeechControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Properties;

public class App {

	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(App.class);
		Properties config = new Properties();

		try {
			InputStream in = new FileInputStream("config.properties");
			config.load(in);
		} catch (FileNotFoundException e) {
			logger.error("config.properties not found", e);
		} catch (IOException e) {
			logger.error("Error while reading config.properties", e);
		}

		final ModuleController moduleController = ModuleController.getInstance();
		moduleController.loadModules();

		int port = Integer.parseInt(config.getProperty("server_port", "9999"));
		final ServerControl server = new ServerControl(new InetSocketAddress(port));

		server.start();

//		if (config.getProperty("enable_speech_control", "true").equals("true")) {
//			SpeechControl speech = new SpeechControl();
//			speech.start();
//		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					server.stop();
					moduleController.stop();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
