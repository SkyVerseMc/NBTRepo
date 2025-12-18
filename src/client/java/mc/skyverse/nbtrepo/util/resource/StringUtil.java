package mc.skyverse.nbtrepo.util.resource;

import net.minecraft.client.font.TextRenderer;

public class StringUtil {

	public static String fit(String text, TextRenderer textRenderer, int width) {
		
		for (int i = 0; i < text.length(); i++) {
			
			if (textRenderer.getWidth(text.substring(0, i)) > width) return text.substring(0, i - 1);
		}
		return text;
	}
	
	public static String[] splitInLines(String text, TextRenderer textRenderer, int width, int linesCount) {
		
		String[] lines = new String[linesCount];
		
		for (int i = 0; i < linesCount; i++) {
			
			lines[i] = "";
		}
	
		String[] words = text.split("[^a-zA-Z0-9§]");
		
		if (textRenderer.getWidth(text) < width) {
			
			words[0] = text;
			return words;
		}

		int i = 0;
		String last = "";

		for (String s : words) {
			
			if (s.contains("§")) last = "§" + s.charAt(s.lastIndexOf('§') + 1);
			
			if (textRenderer.getWidth(lines[i]) + textRenderer.getWidth(s + " ") > width - 10) i++;
			
			if (i > 1) break;
			
			lines[i] += (i > 0 ? last : "") + s + " ";
		}

		return lines;
	}
}
