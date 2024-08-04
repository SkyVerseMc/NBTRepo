package mc.skyverse.nbtrepo;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import mc.skyverse.nbtrepo.elements.Version;
import mc.skyverse.nbtrepo.gui.screen.RepoScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtHelper;

public class NBTRepoModClient implements ClientModInitializer {
	
	public final Version MC_VERSION = new Version("1.20.4");
	
	private static NBTRepoModClient instance;
	private static KeyBinding keyBinding;
	private MinecraftClient mc;
	
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
			
			while (keyBinding.wasPressed()) {
			
				this.mc.player.getHandItems().forEach((e) -> {

					if (e.getNbt() != null) {

						this.mc.player.sendMessage(NbtHelper.toPrettyPrintedText(e.getNbt()));
					}

				});
				this.mc.setScreen(new RepoScreen(this.mc.currentScreen));
		    }
		});
	}
	
	public NBTRepoModClient getInstance() {
		
		return instance;
	}
	
	public Logger getLogger() {
		
		return LogUtils.getLogger();
	}
}