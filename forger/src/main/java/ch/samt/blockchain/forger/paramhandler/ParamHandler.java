package ch.samt.blockchain.forger.paramhandler;

import java.util.HashMap;
import java.util.LinkedHashMap;

import ch.samt.blockchain.forger.paramhandler.types.Arg;
import ch.samt.blockchain.forger.paramhandler.types.Flag;

/**
 * @author Paolo Bettelini
 * @version 1.1	
 */
public class ParamHandler {
	
	private final String PREFIX;
	private HashMap<String, Arg> args;
	private HashMap<String, Flag> flags;

	public ParamHandler(String prefix) {
		args = new LinkedHashMap<>();
		flags = new LinkedHashMap<>();
		PREFIX = prefix;
	}

	public ParamHandler() {
		this("-");
	}

	public void parse(String... params)
			throws IllegalArgumentException {
		String last = null;
		for (String arg : params) {
			if (last != null) {
				if (!isNull(last)) {
					throw new IllegalArgumentException("Duplicated argument: " + last);
				}
				args.get(last).setValue(arg);
				last = null;
			} else if (arg.startsWith(PREFIX)) {
				String name = arg.substring(PREFIX.length());
				if (flags.containsKey(name)) {
					flags.get(name).setValue(true);
				} else {
					if (!args.containsKey(name)) {
						throw new IllegalArgumentException("Illegal argument: " + name);
					}
					last = name;
				}
			}
		}
	}

	public String getStatus() {
		if (isComplete()) {
			return "No missing arguments";
		}

		StringBuilder builder = new StringBuilder();

		for (String name : args.keySet()) {
			Arg arg = args.get(name);
			if (arg.isMandatory() && arg.isNull()) {
				builder.append(PREFIX + name + ", ");
			}
		}

		return "Missing mandatory arguments: {" +
			builder.substring(0, builder.length() - 2).concat("}");
	}

	public boolean isComplete() {
		for (Arg arg : args.values()) {
			if (arg.isMandatory() && arg.isNull()) {
				return false;
			}
		}

		return true;
	}

	public static Property propertyOf(String property, String value) {
		return new Property(property, value);
	}

	public ParamHandler addArg(String argName, boolean mandatory, String type, Property... properties) {
		args.putIfAbsent(argName, new Arg(mandatory, type, properties));
		return this;
	}

	public ParamHandler addArg(String argName, boolean mandatory, String type, String def, Property... properties) {
		args.putIfAbsent(argName, new Arg(mandatory, type, def, properties));
		return this;
	}

	public ParamHandler addFlag(String flagName, Property... properties) {
		flags.putIfAbsent(flagName, new Flag(properties));
		return this;
	}

	public boolean isNull(String argName) {
		return args.get(argName).isNull();
	}

	public String getArg(String argName) {
		return args.get(argName).getValue();
	}

	public boolean getFlag(String flagName) {
		return flags.get(flagName).getValue();
	}

	public boolean setArg(String name, String value) {
		if (!args.containsKey(name)) {
			return false;
		}

		args.get(name).setValue(value);

		return true;
	}

	public boolean setFlag(String name, boolean value) {
		if (!flags.containsKey(name)) {
			return false;
		}

		flags.get(name).setValue(value);

		return true;
	}

	public String help(String cmd, String description) {
		StringBuilder builder = new StringBuilder();

		builder.append(cmd);
		builder.append("\n\nDESCRIPTION\n\t" + description);
		builder.append("\n\nSYNTAX\n\t" + cmd + " ");

		args.forEach((k, v) -> {
			builder.append(
				(v.isMandatory() ? "" : "[") +
				PREFIX + k +
				" <" + v.getType() + ">" +
				(v.isMandatory() ? " " : "] ")
			);
		});

		flags.forEach((k, v) -> {
			builder.append(
				"[" + PREFIX + k + "]"
			);
		});

		builder.append("\n\nPARAMETERS");
		
		args.forEach((k, v) -> {
			builder.append("\n\t" + k + ":");

			int max = 8; // "Default:".length()

			if (v.hasProperties()) {
				Property[] properties = v.getProperties();
				
				for (Property property : properties) {
					if (property.property().length() > max) {
						max = property.property().length();
					}
				}

				++max;

				for (Property property : properties) {
					int len = property.property().length() + 1;

					builder.append("\n\t\t" + property.property() + ":" + " ".repeat(4 + max - len) + property.value());
				}
			}

			builder.append("\n\t\tType:" + " ".repeat(max - 1) + v.getType());
			builder.append("\n\t\tDefault:" + " ".repeat(max - 4) + (v.getDefault() == null ? "None" : v.getDefault()));
		});

		flags.forEach((k, v) -> {
			builder.append("\n\t" + k + ":");

			int max = 0;

			if (v.hasProperties()) {
				Property[] properties = v.getProperties();
				
				for (Property property : properties) {
					if (property.property().length() > max) {
						max = property.property().length();
					}
				}

				for (Property property : properties) {
					int len = property.property().length() + 1;
					builder.append("\n\t\t" + property.property() + ":" + " ".repeat(4 + max - len) + property.value());
				}
			}
		});

		return builder.toString();
	}

}