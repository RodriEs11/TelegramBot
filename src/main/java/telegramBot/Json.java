package telegramBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Json {
	
	public static String extractContent(String urlString)
	         throws MalformedURLException, IOException {
	      URL url = new URL(urlString);
	      URLConnection urlConnection = url.openConnection();
	      InputStream is = urlConnection.getInputStream();
	      BufferedReader br = new BufferedReader(new InputStreamReader(is));
	      String content = "";
	      String linea = br.readLine();
	      while (null != linea) {
	         content += linea;
	         linea = br.readLine();
	      }
	      return content;
	   }
	
	

}
