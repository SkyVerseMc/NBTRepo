package mc.skyverse.nbtrepo.util;

public class NBT {

	public static String getFormatted(String code) {

		return replaceAll(code, new String[][] {
			{"(-|)(\\d+)", "§6$0"},
			{"(true|false)", "§c$1"},
			{"(b|d|f),", "§c$1§§§f,"},
			{"(\\{|\\})", "§f$1§b"},
			{"(\\[|\\])", "§f$1§b"},
		
			{":", "§f: §b"},
			{",", "§f, "},
			{"'", "§f'"},
			{"\"", "§f\"§a"},
			{"minecraft§f: §b", "minecraft:"},
			{"§f, ", "§f, §b"},
			{" §b-", " §f-"}
			
		});
	}
	
	
	/* Internal */
	
	private static String replaceAll(String text, String[][] args) {
		
		for (String[] s : args) {
			
			text = text.replaceAll(s[0], s[1]);
		}
		
		return text;
	}
}
