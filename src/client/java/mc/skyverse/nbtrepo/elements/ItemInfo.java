package mc.skyverse.nbtrepo.elements;

import java.util.Date;

import net.minecraft.item.ItemStack;

public class ItemInfo {

	final String id, name, author;
	final Version version;
	final int downloads;
	final long date;
	
	public ItemInfo(String id, ItemStack stack, String name, Version version, String author, int downloads, long date) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.author = author;
		this.downloads = downloads;
		this.date = date;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
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

	public long getDate() {
		return date;
	}
}
