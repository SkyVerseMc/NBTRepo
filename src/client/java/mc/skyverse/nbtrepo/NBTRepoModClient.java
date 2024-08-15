package mc.skyverse.nbtrepo;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.logging.LogUtils;

import mc.skyverse.nbtrepo.elements.Version;
import mc.skyverse.nbtrepo.gui.screen.RepoScreen;
import mc.skyverse.nbtrepo.util.ModResourceManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtHelper;

public class NBTRepoModClient implements ClientModInitializer {
	
	public static final Version MC_VERSION = new Version("1.20.4");
	public static final Logger LOGGER = LoggerFactory.getLogger("nbtrepo");
	
	private static NBTRepoModClient instance;
	private static KeyBinding keyBinding;
	private MinecraftClient mc;
	private boolean texturesLoaded = false;
	
	@Override
	public void onInitializeClient() {
		
		instance = this;
		
		mc = MinecraftClient.getInstance();
		
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.nbtrepo.open",
			InputUtil.Type.KEYSYM, // KEYSYM (keyboard) , MOUSE (mouse).
			GLFW.GLFW_KEY_M,
			"category.nbtrepo"
		));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			
			loadResources();
			while (keyBinding.wasPressed()) {
			
				mc.player.getHandItems().forEach((e) -> {

					if (e.getNbt() != null) {

						mc.player.sendMessage(NbtHelper.toPrettyPrintedText(e.getNbt()));
					}
				});
				mc.setScreen(new RepoScreen(mc));
		    }
		});
	}
	
	private void loadResources() {
		
		if (texturesLoaded) return;

		ModResourceManager.load(mc);
		texturesLoaded = true;
	}
	
	public NBTRepoModClient getInstance() {
		
		return instance;
	}
	
	public Logger getLogger() {
		
		return LogUtils.getLogger();
	}
}