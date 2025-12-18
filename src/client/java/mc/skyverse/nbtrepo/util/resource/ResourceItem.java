package mc.skyverse.nbtrepo.util.resource;

import mc.skyverse.nbtrepo.util.resource.ModResourceManager.Folder;
import net.minecraft.util.Identifier;

public class ResourceItem {
	
	private final Folder folder;
	private final String namespace, path, fileFormat;

	protected ResourceItem(Folder folder, String path, String fileFormat) {
		
		this(ModResourceManager.NAMESPACE, folder, path, fileFormat);
	}
	
	protected ResourceItem(String namespace, Folder folder, String path, String fileFormat) {
		
		this.namespace = namespace;
		this.folder = folder;
		this.path = path;
		this.fileFormat = fileFormat;
	}

	public Folder getFolder() {
		
		return folder;
	}

	public String getPath() {
		
		return path;
	}

	public String getFileFormat() {
		
		return fileFormat;
	}
	
	public String getNamespace() {
		
		return namespace;
	}
	
	public String toString() {
		
		return namespace + ":" + folder.name + "/" + path;
	}
	
	public String getAbsolutePath(boolean includeFileFormat) {
		
		return namespace + "/" + folder.name + "/" + path + (includeFileFormat ? "." + fileFormat : "");
	}
	
	public Identifier toIdentifier() {
		
		return new Identifier(toString());
	}
}
