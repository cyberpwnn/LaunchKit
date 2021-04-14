package ninja.bytecode.shuriken.tools;

import ninja.bytecode.shuriken.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Paste to the web
 * 
 * @author cyberpwn
 */
public class HasteBin
{
	/**
	 * Paste to hastebin.com
	 * 
	 * @param s
	 *            the paste text (use newline chars for new lines)
	 * @return the url to access the paste
	 * @throws Exception
	 *             shit happens
	 */
	public static String paste(String s) throws Exception
	{
		URL url = new URL("https://hastebin.com/documents");
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("POST");
		httpCon.getOutputStream().write(s.getBytes());
		BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
		JSONObject jso = new JSONObject(in.readLine());

		return "https://hastebin.com/" + jso.getString("key");
	}
}