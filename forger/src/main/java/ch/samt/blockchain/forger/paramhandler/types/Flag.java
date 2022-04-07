package ch.samt.blockchain.forger.paramhandler.types;

import ch.samt.blockchain.forger.paramhandler.Property;

public class Flag {
	
	private Property[] properties;
	private boolean value;

	public Flag(Property... properties) {
		this.value = false;
		this.properties = properties == null ? new Property[0] : properties;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	public Property[] getProperties() {
		return properties;
	}

	public boolean hasProperties() {
		return properties.length != 0;
	}

}