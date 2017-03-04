package de.pk.rphc.modules;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.wiringpi.Gpio;
import de.pk.rphc.model.Socket;

public class LightController {

	private final int pulseLength = 350;
	private final int repeatTransmit = 10;

	private String name;
	private Pin transmitterGpio;
	private Socket[] sockets;

	private GpioPinDigitalOutput transmitterPin;

	public LightController(String name, Pin transmitterGpio, Socket[] sockets) {
		this.name = name;
		this.transmitterGpio = transmitterGpio;
		this.sockets = sockets;
	}

	void initialize() {
		final GpioController gpio = GpioFactory.getInstance();
		transmitterPin = gpio.provisionDigitalOutputPin(transmitterGpio);
	}

	public void switchOn(int id) {
		switchOnDIP(sockets[id].group, sockets[id].device);
	}

	public void switchOff(int id) {
		switchOffDIP(sockets[id].group, sockets[id].device);
	}

	private void switchOnDIP(String group, String device) {
		sendTriState(getCodeWordA(group, device, true));
	}

	private void switchOffDIP(String group, String device) {
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

	public String getName() {
		return name;
	}

	public Socket[] getSockets() {
		return sockets;
	}
}
