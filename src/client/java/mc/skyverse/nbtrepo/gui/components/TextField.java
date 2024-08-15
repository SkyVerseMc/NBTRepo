package mc.skyverse.nbtrepo.gui.components;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class TextField extends TextFieldWidget {
	
	private Listener listener = new Listener();

	public TextField(TextRenderer textRenderer, int width, int height, Text text) {

		this(textRenderer, 0, 0, width, height, text);
	}

	public TextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {

		this(textRenderer, x, y, width, height, null, text);
	}

	public TextField(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextField copyFrom, Text text) {

		super(textRenderer, x, y, width, height, text);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

		if (!this.isNarratable() || !this.isFocused()) return false;

		switch (keyCode) {

		case 235:
		case 257:
			listener.onUpdate(this);
			return true;

		case 258:
		case 256:
			unfocus();
			return true;

		default:
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}
	
	public void setListener(Listener listener) {
		
		this.listener = listener;
	}
	
	public void unfocus() {

		if (!getText().equals("http://") && !getText().equals("https://")) return;
		
		setFocused(false);
		setText("");
	}
}
