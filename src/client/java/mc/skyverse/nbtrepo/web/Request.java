package mc.skyverse.nbtrepo.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request {

	private String url;
	private String[][] headers;
	private String payload;

	public Method method;

	public static enum Method {

		POST(false, "POST"),
		GET(true, "GET"),
		;

		private boolean needPayload;
		private String text;

		Method(boolean b, String name) {

			this.needPayload = b;
			this.text = name;
		}
	}

	public Request(Method method, String url, String[][] headers, String payload) {

		this.method = method;
		this.url = url;
		this.headers = headers;
		this.payload = payload;
	}

	public static Request prefill(Method method, String url) {

		return new Request(method, url, null, null);
	}

	public Object[] send(Server server) {

		if (!this.canSend()) return new Object[] {-1, "Not enough parameters for the request", "[{\"id\":01234,\"name\":\"Offline test item NBT\",\"author\":\"Mwa\",\"last_update\":0,\"version\":\"1.20.*\",\"downloads\":69}]"};

		try {

			HttpURLConnection con = (HttpURLConnection) new URL(server.url + this.url).openConnection();

			con.setRequestMethod(this.method.text);

			for (String[] s : this.headers) {

				con.setRequestProperty(s[0], s[1]);
			}
			
			if (this.method == Method.POST) {
				
				con.setRequestProperty("Content-Length", String.valueOf(this.payload.getBytes().length));
				
				con.setDoOutput(true);

	            OutputStream os = con.getOutputStream();
	            os.write(this.payload.getBytes());
	            os.flush();
	            os.close();
			}

			int responseCode = con.getResponseCode();

			if (responseCode == 200) {

				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {

					response.append(inputLine);
				}
				in.close();
				
				System.out.println("Réponse : " + response.toString());

				return new Object[] {responseCode, con.getResponseMessage(), response.toString()};

			} else {
				
				return new Object[] {responseCode, con.getResponseMessage()};
			}

		} catch (IOException e) {

			e.printStackTrace();
			return new Object[] {-1, e.getMessage()};
		}
	}

	public boolean canSend() {

//		return true;
		return this.url != null && this.headers != null && (this.payload != null && this.method.needPayload);
	}
}
