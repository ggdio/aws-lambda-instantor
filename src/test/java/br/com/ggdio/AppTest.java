package br.com.ggdio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class AppTest {
	
	public static void main(String[] args) throws Exception {
		Properties props = getProperties("data.properties");
		
		URL url = new URL((String) props.get("url"));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod("POST");
	    conn.setDoOutput(true);
	    
	    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	    writer.write("");
	    writer.flush();
	    writer.close();
	    conn.disconnect();
	}

	private static Properties getProperties(String propFileName) throws IOException, FileNotFoundException {
		Properties prop = new Properties();
		InputStream is = AppTest.class.getClassLoader().getResourceAsStream(propFileName);
		if (is != null) {
			prop.load(is);
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}
		is.close();
		
		return prop;
	}
}
