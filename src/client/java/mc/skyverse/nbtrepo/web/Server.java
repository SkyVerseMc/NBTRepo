package mc.skyverse.nbtrepo.web;

public class Server {

	public String url;
	
	public Server(String url) {
		
		this.url = (!url.startsWith("http") ? "http://" : "") + url;
	}
}
