package de.pk.rphc.model;

public class Socket {

	public String name;
	public String type;
	public String group;
	public String device;

	public Socket(String name) {
		this.name = name;
	}

	public Socket(String name, String group, String device) {
		this.name = name;
		this.group = group;
		this.device = device;
	}

}