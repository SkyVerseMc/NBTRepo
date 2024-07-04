package mc.skyverse.nbtrepo.gui;

import java.awt.Color;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.input.KeyCodes;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;

public class ItemCard extends PressableWidget {
	
	private boolean initialized = false;
	
    public ItemCard(Text text) {
    	
		super(0, 0, 0, 0, text);
	}
    
    public void init(int[] coordinates, int width, int height) {
    	
    	this.setX(coordinates[0]);
    	this.setY(coordinates[1]);
    	
    	this.setWidth(width);
    	this.setHeight(height);
    	
    	this.initialized = true;
    }
    
    @Override
    public void onPress() {};
    
    public void renderF(DrawContext context, int mouseX, int mouseY, float delta) {
    	
    	this.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    	
    	if (!initialized) return;
    	
        context.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        
        context.fill(RenderLayer.getGui(), getX(), getY(), getX() + getWidth(), getY() + getHeight(), new Color(140, 101, 69).getRGB());
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
    	
        this.drawScrollableText(context, textRenderer, 2, color);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
    	
        this.onPress();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    	
        if (!initialized || !this.active || !this.visible) {
            return false;
        }
        if (KeyCodes.isToggle(keyCode)) {
        	
            this.playDownSound(MinecraftClient.getInstance().getSoundManager());
            this.onPress();
            return true;
        }
        return false;
    }

	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder var1) {}
}