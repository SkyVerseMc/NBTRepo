package mc.skyverse.nbtrepo;

public class Test {

	public static void main(String[] args) {
		
        String code = "{id:\"minecraft:stone\",CustomModelData:0,display:{Name:\"Stone\"},Enchantments:[],NBTin:true}";
        String colorizedCode = colorizeCode(code);
        System.out.println(colorizedCode);
	}

    public static String colorizeCode(String code) {
    	
        code = code.replace("{", "§f{");
        code = code.replace("}", "}§b");
        code = code.replace(":", "§f:");
        code = code.replace(",", "§f,");
        code = code.replace("[", "§f[");
        code = code.replace("]", "]§f");
        code = code.replace("'", "§f'");
        code = code.replace("\"", "§f\"");

        // Color numbers in yellow
        code = code.replaceAll("\\b\\d+\\b", "§6$0§b");

        // Color letters between quotes or apostrophes in green
        code = code.replaceAll("['\"](.*?)['\"]", "§a'$1'§b");

        return code;
    }
}
