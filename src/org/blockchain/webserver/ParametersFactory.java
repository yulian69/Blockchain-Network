package org.blockchain.webserver;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ParametersFactory {
	
	public static Parameters parsePostData(String postData) {
		Parameters parameters = new Parameters();
		
		try {									
			String[] items = postData.split("\\&");
			for (String item : items) {
				String[] parts = item.split("=");
				if ( parts.length > 1 ) {
					String name = URLDecoder.decode(parts[0], StandardCharsets.UTF_8.name()).trim();
					String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8.name()).trim();
					parameters.addValue(name, value);
				} else {
					String name = URLDecoder.decode(item, StandardCharsets.UTF_8.name()).trim();
					parameters.addValue(name, "");
				}
			}
		} catch (IOException e) {}
    	
    	return parameters;
    }
}
