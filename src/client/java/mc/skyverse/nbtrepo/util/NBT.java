package mc.skyverse.nbtrepo.util;

import java.util.Iterator;
import java.util.List;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

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
	
//	
//    private static String getNBTString(DataCommandObject object, NbtPathArgumentType.NbtPath path) {
//        int i;
//        NbtElement nbtElement = DataCommand.getNbt(path, object);
//        if (nbtElement instanceof AbstractNbtNumber) {
//            i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue());
//        } else if (nbtElement instanceof AbstractNbtList) {
//            i = ((AbstractNbtList)nbtElement).size();
//        } else if (nbtElement instanceof NbtCompound) {
//            i = ((NbtCompound)nbtElement).getSize();
//        } else if (nbtElement instanceof NbtString) {
//            i = nbtElement.asString().length();
//        } else {
//            //throw GET_UNKNOWN_EXCEPTION.create(path.toString());
//        }
//        
//        return object.feedbackQuery(nbtElement);
//    }
	
    public static NbtElement getNbt(NbtPathArgumentType.NbtPath path, DataCommandObject object) throws CommandSyntaxException {
        List<NbtElement> collection = path.get(object.getNbt());
        Iterator iterator = collection.iterator();
        NbtElement nbtElement = (NbtElement)iterator.next();
        if (iterator.hasNext()) {
//            throw GET_MULTIPLE_EXCEPTION.create();
            return null;
        }
        return nbtElement;
    }
//    
    private static Text executeGet(DataCommandObject object, NbtPathArgumentType.NbtPath path, double scale) throws CommandSyntaxException {
        NbtElement nbtElement = DataCommand.getNbt(path, object);
        if (!(nbtElement instanceof AbstractNbtNumber)) {
//            throw GET_INVALID_EXCEPTION.create(path.toString());
        	return Text.literal("invalid");
        }
        int i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue() * scale);
        
        return object.feedbackGet(path, scale, i);
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, double scale) throws CommandSyntaxException {
        NbtElement nbtElement = DataCommand.getNbt(path, object);
        if (!(nbtElement instanceof AbstractNbtNumber)) {
//            throw GET_INVALID_EXCEPTION.create(path.toString());
        }
        int i = MathHelper.floor(((AbstractNbtNumber)nbtElement).doubleValue() * scale);
        source.sendFeedback(() -> object.feedbackGet(path, scale, i), false);
        return i;
    }

    private static int executeGet(ServerCommandSource source, DataCommandObject object) throws CommandSyntaxException {
        NbtCompound nbtCompound = object.getNbt();
        source.sendFeedback(() -> object.feedbackQuery(nbtCompound), false);
        return 1;
    }
}
