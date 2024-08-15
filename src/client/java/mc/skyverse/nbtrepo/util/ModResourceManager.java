package mc.skyverse.nbtrepo.util;

import java.io.IOException;
import java.lang.reflect.Field;

import mc.skyverse.nbtrepo.NBTRepoModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public class ModResourceManager {

	public final static String NAMESPACE = "nbtrepo";

	public final static Identifier EXPAND_BUTTON = new Identifier(NAMESPACE, "textures/expand_button");

	public static void load(MinecraftClient mc) {

		TextureManager textureManager = mc.getTextureManager();
		NativeImage img;

		try {
			
			for (Field f : ModResourceManager.class.getDeclaredFields()) {

				if (f.getType() == Identifier.class) {

					Identifier id = (Identifier)f.get(null);

					try {

						img = NativeImage.read(ModResourceManager.class.getResourceAsStream("/assets/" + NAMESPACE + "/" + id.getPath() + ".png"));
						textureManager.registerTexture(id, new NativeImageBackedTexture(img));

						NBTRepoModClient.LOGGER.info("loaded texture '" + id.toString() + "'.");

					} catch (IOException e) {

						NBTRepoModClient.LOGGER.error("Unable to register texture for '" + id.toString() + "'.");
						e.printStackTrace();
					}
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException e) {

			NBTRepoModClient.LOGGER.error("Unable to parse texture fields.");
			e.printStackTrace();
		}
	}
}
