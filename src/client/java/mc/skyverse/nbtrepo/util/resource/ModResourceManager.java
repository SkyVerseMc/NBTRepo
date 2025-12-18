package mc.skyverse.nbtrepo.util.resource;

import java.lang.reflect.Field;

import mc.skyverse.nbtrepo.NBTRepoModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

public class ModResourceManager {

	public final static String NAMESPACE = "nbtrepo";

	public final static ResourceItem EXPAND_BUTTON =  new ResourceItem(Folder.TEXTURES, "expand_button", "png");
	
	protected static enum Folder {
		
		TEXTURES("textures"),
		OTHER(""),
		;

		String name;
		
		Folder (String name) {
		
			this.name = name;
		}
		
		public Folder getByFolderName(String name) {
			
			for (Folder f : Folder.values()) {
				
				if (f.name.equalsIgnoreCase(name)) return f;
			}
			return OTHER;
		}
	}

	public static void load(MinecraftClient mc) {

		try {
			
			for (Field f : ModResourceManager.class.getDeclaredFields()) {

				if (f.getType() == ResourceItem.class) {

					ResourceItem item = (ResourceItem)f.get(null);
					
					switch (item.getFolder()) {
					
					case TEXTURES:
						registerTexture(mc, item);
						break;
					
					default:
						NBTRepoModClient.LOGGER.error("Skipped resource object '" + item.toString() + "'.");
						break;
					}
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {

			NBTRepoModClient.LOGGER.error("Unable to parse texture fields.");
			e.printStackTrace();
		}
	}
	
	private static void registerTexture(MinecraftClient mc, ResourceItem resourceItem) {
		
		try {

			NativeImage img = NativeImage.read(ModResourceManager.class.getResourceAsStream("/assets/" + resourceItem.getAbsolutePath(true)));
			mc.getTextureManager().registerTexture(resourceItem.toIdentifier(), new NativeImageBackedTexture(img));

			NBTRepoModClient.LOGGER.info("Loaded texture '" + resourceItem.toString() + "'.");

		} catch (Exception e) {

			NBTRepoModClient.LOGGER.error("Unable to register texture for '" + resourceItem.toString() + "'.");
			e.printStackTrace();
		}
	}
}
