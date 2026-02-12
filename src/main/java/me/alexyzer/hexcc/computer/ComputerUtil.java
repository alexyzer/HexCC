package me.alexyzer.hexcc.computer;

import me.alexyzer.HexCC;
import net.minecraft.nbt.*;

import java.util.*;

public class ComputerUtil {
    public static Object tagToLua(NbtElement tag){
        return switch(tag.getType()){
            case NbtElement.BYTE_TYPE->((NbtByte)tag).byteValue();
            case NbtElement.SHORT_TYPE->((NbtShort)tag).shortValue();
            case NbtElement.INT_TYPE->((NbtInt)tag).intValue();
            case NbtElement.LONG_TYPE->((NbtLong)tag).longValue();
            case NbtElement.FLOAT_TYPE->((NbtFloat)tag).floatValue();
            case NbtElement.DOUBLE_TYPE->((NbtDouble)tag).doubleValue();
            case NbtElement.STRING_TYPE-> tag.asString();
            case NbtElement.BYTE_ARRAY_TYPE-> ((NbtByteArray)tag).getByteArray(); //Let it be a string
            case NbtElement.INT_ARRAY_TYPE-> Arrays.stream(((NbtIntArray)tag).getIntArray()).boxed().toList();
            case NbtElement.LONG_ARRAY_TYPE-> Arrays.stream(((NbtLongArray)tag).getLongArray()).boxed().toList();
            case NbtElement.LIST_TYPE-> {
                var list = ((NbtList)tag);
                var size = list.size();
                List<Object> result = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    result.add(i,tagToLua(list.get(i)));
                }
                yield result;
            }
            case NbtElement.COMPOUND_TYPE-> {
                var compound = ((NbtCompound)tag);
                Map<String,Object> result = new LinkedHashMap<>(compound.getSize());
                for (String key: compound.getKeys()){result.put(key,tagToLua(compound.get(key)));}
                yield result;
            }
            default-> {
                HexCC.LOGGER.error("Failed to serialize {}",tag);
                yield null;
            }
        };
    }
}
