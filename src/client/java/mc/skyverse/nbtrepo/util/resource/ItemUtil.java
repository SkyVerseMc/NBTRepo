package mc.skyverse.nbtrepo.util.resource;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

public class ItemUtil {

	public static Item getById(String name) {
		
		String id;
		
		for (Item item : Registries.ITEM) {
			
			id = Registries.ITEM.getId(item).toString();
			
			if (name.contains(":") && id.equalsIgnoreCase(name)) return item;
			
			if (id.substring(id.indexOf(':') + 1).equalsIgnoreCase(name)) return item;
		}
		return Items.AIR;
	}
}
