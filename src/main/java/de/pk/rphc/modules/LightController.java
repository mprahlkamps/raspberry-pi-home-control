package de.pk.rphc.modules;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.wiringpi.Gpio;

public class LightController {

	private final int pulseLength = 350;
	private final int repeatTransmit = 10;

	private Pin transmitterGpio;

	private GpioPinDigitalOutput transmitterPin;

	public LightController(Pin transmitterGpio) {
		this.transmitterGpio = transmitterGpio;

		final GpioController gpio = GpioFactory.getInstance();
		this.transmitterPin = gpio.provisionDigitalOutputPin(transmitterGpio);

		switchOffDIP("11011", "10000");
		switchOnDIP("11011", "10000");

	}

	public void switchOnDIP(String group, String device) {
		sendTriState(getCodeWordA(group, device, true));
	}

	public void switchOffDIP(String group, String device) {
		sendTriState(getCodeWordA(group, device, false));
	}

	private String getCodeWordA(String group, String device, boolean status) {
		char[] sReturn = new char[13];
		int nReturnPos = 0;

		for (int i = 0; i < 5; i++) {
			sReturn[nReturnPos++] = (group.charAt(i) == '0') ? 'F' : '0';
		}

		for (int i = 0; i < 5; i++) {
			sReturn[nReturnPos++] = (device.charAt(i) == '0') ? 'F' : '0';
		}

		sReturn[nReturnPos++] = status ? '0' : 'F';
		sReturn[nReturnPos++] = status ? 'F' : '0';
		sReturn[nReturnPos] = '\0';

		return new String(sReturn);
	}

	private void sendTriState(String codeWord) {
		for (int nRepeat = 0; nRepeat < repeatTransmit; nRepeat++) {
			for (int i = 0; i < codeWord.length(); ++i) {
				switch (codeWord.charAt(i)) {
					case '0':
						this.sendT0();
						break;
					case 'F':
						this.sendTF();
						break;
					case '1':
						this.sendT1();
						break;
				}
			}
			this.sendSync();
		}
	}

	private void sendSync() {
		this.transmit(1, 31);
	}

	private void sendT0() {
		this.transmit(1, 3);
		this.transmit(1, 3);
	}

	private void sendT1() {
		this.transmit(3, 1);
		this.transmit(3, 1);
	}

	private void sendTF() {
		this.transmit(1, 3);
		this.transmit(3, 1);
	}

	private void transmit(int nHighPulses, int nLowPulses) {
		if (this.transmitterPin != null) {
			this.transmitterPin.high();
			Gpio.delayMicroseconds(this.pulseLength * nHighPulses);

			this.transmitterPin.low();
			Gpio.delayMicroseconds(this.pulseLength * nLowPulses);
		}
	}
}
