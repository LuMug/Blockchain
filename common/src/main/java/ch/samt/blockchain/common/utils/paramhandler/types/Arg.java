package ch.samt.blockchain.common.utils.paramhandler.types;

import ch.samt.blockchain.common.utils.paramhandler.Property;

public class Arg {
	
	private Property[] properties;
	private boolean mandatory;
	private String value, type, def, name;

	public Arg(boolean mandatory, String type, String def, Property... properties) {
		this.mandatory = mandatory;
		this.type = type;
		this.def = def;
		this.properties = properties == null ? new Property[0] : properties;
	}

	public Arg(boolean mandatory, String type, Property... properties) {
		this(mandatory, type, null, properties);
	}

	public String getValue() {
		return isNull() ? def : value;
	}

	public boolean isNull() {
		return value == null;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getDefault() {
		return def;
	}

	public String getType() {
		return type;
	}

	public Property[] getProperties() {
		return properties;	
	}

	public boolean hasProperties() {
		return properties.length != 0;
	}

}