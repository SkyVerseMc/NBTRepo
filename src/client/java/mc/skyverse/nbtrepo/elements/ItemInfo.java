package mc.skyverse.nbtrepo.elements;

import java.util.Date;

public class ItemInfo {

	final String id, name, item, comment, author;
	final Version version;
	final int downloads;
	final long timestamp;
	
	public ItemInfo(String id, String item, String name, Version version, String comment, String author, int downloads, long timestamp) {
		this.id = id;
		this.name = name;
		this.item = item;
		this.version = version;
		this.comment = comment;
		this.author = author;
		this.downloads = downloads;
		this.timestamp = timestamp;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getItem() {
		return item;
	}
	
	public String getComment() {
		return comment;
	}

	public String getAuthor() {
		return author;
	}

	public Version getVersion() {
		return version;
	}

	public int getDownloads() {
		return downloads;
	}

	public long getDateLong() {
		return timestamp;
	}
	
	public String getDateString() {
		
		Date d = new Date(timestamp);
		return (d.getYear() + 1900)+ "/" + (d.getMonth() + 1 < 10 ? "0" : "") + (d.getMonth() + 1) + "/" + (d.getDate() < 10 ? "0" : "") + d.getDate();
	}
}
