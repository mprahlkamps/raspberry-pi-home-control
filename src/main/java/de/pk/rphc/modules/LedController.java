package de.pk.rphc.modules;

import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Properties;

public class LedController {

	private Logger logger;

	private final GpioController gpio;

	private Properties moduleProperties;

	private Pin redPinNumber;
	private Pin greenPinNumber;
	private Pin bluePinNumber;

	private GpioPinPwmOutput redPin;
	private GpioPinPwmOutput greenPin;
	private GpioPinPwmOutput bluePin;

	private Object pinLock = new Object();
	private Object enableLock = new Object();
	private Object colorLock = new Object();

	/**
	 * Whether the LEDs are enabled or not.
	 */
	private boolean enabled;

	/**
	 * Color of the LEDs when they are {@link #enabled}
	 */
	private Color currentColor;

	public LedController(Properties moduleProperties) {
		this.moduleProperties = moduleProperties;

		logger = LoggerFactory.getLogger(LedController.class);
		gpio = GpioFactory.getInstance();
	}

	/**
	 * <p>
	 * Set outputmode for each pin (r,g,b) to SoftPwmOut
	 * </p>
	 * <p>
	 * Set pwmRange for each pin to 100
	 * </p>
	 */
	public void initLeds() {
		logger.info("Initializing LEDs...");

		int redGPIO = Integer.parseInt(moduleProperties.getProperty("led_gpio_r", "0"));
		int greenGPIO = Integer.parseInt(moduleProperties.getProperty("led_gpio_g", "2"));
		int blueGPIO = Integer.parseInt(moduleProperties.getProperty("led_gpio_b", "3"));

		redPinNumber = RaspiPin.getPinByAddress(redGPIO);
		greenPinNumber = RaspiPin.getPinByAddress(greenGPIO);
		bluePinNumber = RaspiPin.getPinByAddress(blueGPIO);

		redPin = gpio.provisionSoftPwmOutputPin(redPinNumber, "red");
		greenPin = gpio.provisionSoftPwmOutputPin(greenPinNumber, "green");
		bluePin = gpio.provisionSoftPwmOutputPin(bluePinNumber, "blue");

		redPin.setPwmRange(100);
		greenPin.setPwmRange(100);
		bluePin.setPwmRange(100);

		setLedColor(Color.blue);
		enableLeds();
	}

	/**
	 * Enables the LEDs
	 */
	public void enableLeds() {
		logger.debug("Enabling LEDs");

		synchronized (enableLock) {
			if (!enabled) {
				enabled = true;
				updateLeds();
			}
		}
	}

	/**
	 * Disables the LEDs
	 */
	public void disableLeds() {
		logger.debug("Disabling LEDs");

		synchronized (enableLock) {
			if (enabled) {
				enabled = false;
				updateLeds();
			}
		}
	}

	/**
	 * <p>
	 * If {@link #enabled} == true - sets currentColor to pin outputs.
	 * </p>
	 * <p>
	 * If {@link #enabled} == false - sets pin outputs to 0.
	 * </p>
	 */
	private void updateLeds() {

		if (enabled) {

			Color c = getLedColor();
			// normalize
			int r = (int) (c.getRed() / 255.0 * 100.0);
			int g = (int) (c.getGreen() / 255.0 * 100.0);
			int b = (int) (c.getBlue() / 255.0 * 100.0);

			synchronized (pinLock) {
				redPin.setPwm(r);
				greenPin.setPwm(g);
				bluePin.setPwm(b);
			}

		} else {

			synchronized (pinLock) {
				redPin.setPwm(0);
				greenPin.setPwm(0);
				bluePin.setPwm(0);
			}

		}

	}

	/**
	 * Sets the color of the LEDs. (Even if LEDs are disabled) Color is set when
	 * they are enabled the next time.
	 *
	 * @param color
	 */
	public void setLedColor(Color color) {
		logger.debug("Setting LED color (" + color + ")");

		synchronized (colorLock) {
			currentColor = color;
		}

		updateLeds();
	}

	/**
	 * Returns the last set Color. (Even if LEDs are disabled)
	 *
	 * @return
	 */
	public Color getLedColor() {
		synchronized (colorLock) {
			return currentColor;
		}
	}

	/**
	 * Whether the LEDs are enabled or not
	 *
	 * @return true if enabled
	 */
	public boolean isEnabled() {
		synchronized (enableLock) {
			return enabled;
		}
	}

}