package de.pk.rphc.modules;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SaLedController {

	private Logger logger;
	private String name;
	private int ledCount;

	private ByteBuffer colorBuffer;

	private static SpiDevice spi = null;
	private Color ledColor;

	public SaLedController(String name, int ledCount) {
		this.name = name;
		this.ledCount = ledCount;

		logger = LoggerFactory.getLogger(LedController.class);
	}

	public void initialize() {
		logger.info("Initializing SA Led Controller \"" + name + "\"");

		try {
			spi = SpiFactory.getInstance(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		colorBuffer = ByteBuffer.allocate(ledCount * 3);

		setColor(Color.RED);
		show();
	}

	public void show() {
		try {
			spi.write(colorBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		colorBuffer.clear();
	}

	public void setColor(int index, Color color) {
		colorBuffer.put(index * 3, (byte) color.getRed());
		colorBuffer.put((index * 3) + 1, (byte) color.getGreen());
		colorBuffer.put((index * 3) + 2, (byte) color.getBlue());
	}

	public void setColor(Color ledColor) {
		for(int i = 0; i < ledCount; i++) {
			setColor(i, ledColor);
		}
	}

	public String getName() {
		return name;
	}

	public void stop() {
		logger.info("Stopping Sa Led Controller (" + name + ")");
		setColor(Color.black);
	}
}
