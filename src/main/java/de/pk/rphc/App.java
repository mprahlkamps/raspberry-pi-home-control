package de.pk.rphc;

import de.pk.rphc.server.ControlServer;

import java.net.InetSocketAddress;

public class App {

	public static void main(String[] args) {

        ControlServer server = new ControlServer(new InetSocketAddress(9999));
        server.start();

	}

}
