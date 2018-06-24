package org.blockchain.webserver;

import java.util.Map;
import java.util.TreeMap;

public class Parameters {	
	
	private Map<String, String> map = new TreeMap<>();

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}
	
	public void addValue(String name, String value) {
		map.put(name, value);
	}
	
	public void addValue(String name, int value) {
		map.put(name, ""+value);
	}
	
	public void addValue(String name, long value) {
		map.put(name, ""+value);
	}
	
	public void addValue(String name, Double value) {
		map.put(name, ""+value);
	}
	
	public String getValue(String name) {
		return map.get(name);
	}
	
	public String getValueNotNull(String name) {
		String value = map.get(name);
		if ( value == null ) {
			value = "";
		}
		return value;
	}
	
	public boolean getValueAsBool(String name) {
		try {
			return Boolean.parseBoolean(map.get(name));
		} catch (Exception e) {}
		return false;
	}
	
	public long getValueAsLong(String name) {
		try {
			return Long.parseLong(map.get(name).replaceAll(",", ""));
		} catch (Exception e) {}
		return 0L;
	}
	
	public int getValueAsInt(String name) {
		try {
			return Integer.parseInt(map.get(name).replaceAll(",", ""));
		} catch (Exception e) {}
		return 0;
	}
	
	public double getValueAsDouble(String name) {
		try {
			return Double.parseDouble(map.get(name).replaceAll(",", ""));
		} catch (Exception e) {}
		return 0.0;
	}
	
	public String[] getNames() {
		String[] names = new String[map.size()];
		return map.keySet().toArray(names);
	}
	
	public Parameters clone() {
		Parameters parameters = new Parameters();
		for (String name : map.keySet()) {
			parameters.addValue(name, map.get(name));
		}
		return parameters;
	}
}
