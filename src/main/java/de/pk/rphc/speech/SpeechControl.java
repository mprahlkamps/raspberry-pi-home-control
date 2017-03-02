package de.pk.rphc.speech;

import de.pk.rphc.modules.ModuleController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeechControl implements Runnable {

	private Logger logger;
	private ModuleController moduleController;
	private Thread runner;

	public SpeechControl() {
		logger = LoggerFactory.getLogger(SpeechControl.class);
		moduleController = ModuleController.getInstance();
	}

	public void start() {
		logger.info("Starting Speech Control");

		if (runner != null) {
			throw new IllegalStateException("SpeechControl runner already running");
		}
		runner = new Thread(this);
		runner.start();
	}

	public void run() {

		while (!runner.isInterrupted()) {


			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
