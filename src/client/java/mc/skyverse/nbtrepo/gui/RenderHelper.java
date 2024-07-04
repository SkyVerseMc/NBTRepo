package mc.skyverse.nbtrepo.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;

public class RenderHelper {

	public static void fill(DrawContext context, int bottomLeft, int upperRight) {
		
		context.fill(RenderLayer.getGui(), upperRight, upperRight, upperRight, bottomLeft, upperRight);
	}
}
