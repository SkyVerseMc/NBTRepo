package mc.skyverse.nbtrepo;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import mc.skyverse.nbtrepo.gui.screen.RepoScreen;
import mc.skyverse.nbtrepo.util.NBT;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class NBTRepoModClient implements ClientModInitializer {
	
	MinecraftClient mc;
	private static NBTRepoModClient instance;
	private static KeyBinding keyBinding;

	@Override
	public void onInitializeClient() {
		
		instance = this;
		
		this.mc = MinecraftClient.getInstance();
		
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			    "key.nbtrepo.open",
			    InputUtil.Type.KEYSYM, // KEYSYM (keyboard) , MOUSE (mouse).
			    GLFW.GLFW_KEY_M,
			    "category.nbtrepo"
			));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			
			while (keyBinding.wasPressed()) {
			
				this.mc.player.getHandItems().forEach((e) -> {
					
					if (e.getNbt() != null) this.mc.player.sendMessage(Text.literal(NBT.getFormatted(e.getNbt().asString())));
					
				});
				this.mc.setScreen(new RepoScreen(this.mc.currentScreen));
		    }
		});
		
		System.out.println("Loaded.");
	}
	
	public NBTRepoModClient getInstance() {
		
		return instance;
	}
	
	public Logger getLogger() {
		
		return LogUtils.getLogger();
	}
}