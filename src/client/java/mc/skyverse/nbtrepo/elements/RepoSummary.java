package mc.skyverse.nbtrepo.elements;

import java.util.Date;

public class RepoSummary {
	
	public String id;
	public String name;
	public String author;
	public String last_update;
	public String version;
	public int downloads;
	
	public RepoSummary(String id, String name, String author, String version, long timestamp, int downloads) {
		
		this.id = id;
		this.name = name;
		this.author = author;
		this.version = version;
		
		Date d = new Date(timestamp);
		this.last_update = d.getYear() + "/" + (d.getMonth() + 1 < 10 ? "0" : "") + (d.getMonth() + 1) + "/" + (d.getDate() < 10 ? "0" : "") + d.getDate();
	
		this.downloads = downloads;
	}
	
	public String getId() {
		
		return this.id;
	}
	
	public String getName() {
		
		return this.name;
	}

	public String getAuthor() {
		
		return this.author;
	}
	
	public String getLastUpdate() {
		
		return this.last_update;
	}
	
	public int getDownloadsCount() {
		
		return this.downloads;
	}
}
