package de.pk.rphc.modules;

import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.Gpio;
import de.pk.rphc.model.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketController {

	private final int pulseLength = 350;
	private final int repeatTransmit = 10;
	private final Logger logger;

	private String name;
	private Pin transmitterGpio;
	private Socket[] sockets;

	private GpioPinDigitalOutput transmitterPin;

	public SocketController(String name, Pin transmitterGpio, Socket[] sockets) {
		this.name = name;
		this.transmitterGpio = transmitterGpio;
		this.sockets = sockets;
		this.logger = LoggerFactory.getLogger(ModuleController.class);
	}

	void initialize() {
		final GpioController gpio = GpioFactory.getInstance();
		transmitterPin = gpio.provisionDigitalOutputPin(transmitterGpio);
		transmitterPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
	}

	public void switchDip(int id, boolean state) {
		sendTriState(getCodeWordA(sockets[id].group, sockets[id].device, state));
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

	public void stop() {
		logger.info("Stopping Socket Controller (" + name + ")");
	}

	public String getName() {
		return name;
	}

	public Socket[] getSockets() {
		return sockets;
	}
}
