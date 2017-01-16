package orange.labs.iot.computational.storage.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {

	private static final String resource = "Grouping.properties"; 
	
	private static Properties properties = new Properties();
	
	public static String getPropertyValue(String property) throws IOException{
		String result = "";

		Properties prop = loadProperties();

		result = prop.getProperty(property);

		return result;
	}

	public static String getProperties() throws IOException{
		String result = "";

		Properties prop = loadProperties();

		for (Object key : prop.keySet()) {
			result += key + "=";
			result += prop.getProperty((String) key) + "\n";
		}
		
		return result;
	}

	public static void setPropertyValue(String property, String value) throws IOException {

		Properties prop = loadProperties();
		prop.setProperty(property, value);

	}

	public static Properties loadProperties() throws IOException {

		if (properties.isEmpty()) {

			InputStream inputStream = PropertyManager.class.getClassLoader().getResourceAsStream(resource);

			if (inputStream != null) {
				properties.load(inputStream);
			}
			inputStream.close();
		}

		return properties;
	}
}
