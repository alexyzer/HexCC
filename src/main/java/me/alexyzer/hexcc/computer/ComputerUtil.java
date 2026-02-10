package me.alexyzer.hexcc.computer;

import me.alexyzer.HexCC;
import net.minecraft.nbt.*;

import java.util.*;

public class ComputerUtil {
    public static Object tagToLua(Tag tag){
        return switch(tag.getId()){
            case Tag.TAG_BYTE->((ByteTag)tag).getAsByte();
            case Tag.TAG_SHORT->((ShortTag)tag).getAsShort();
            case Tag.TAG_INT->((IntTag)tag).getAsInt();
            case Tag.TAG_LONG->((LongTag)tag).getAsLong();
            case Tag.TAG_FLOAT->((FloatTag)tag).getAsFloat();
            case Tag.TAG_DOUBLE->((DoubleTag)tag).getAsDouble();
            case Tag.TAG_STRING-> tag.getAsString();
            case Tag.TAG_BYTE_ARRAY-> ((ByteArrayTag)tag).getAsByteArray(); //Let it be a string
            case Tag.TAG_INT_ARRAY-> Arrays.stream(((IntArrayTag)tag).getAsIntArray()).boxed().toList();
            case Tag.TAG_LONG_ARRAY-> Arrays.stream(((LongArrayTag)tag).getAsLongArray()).boxed().toList();
            case Tag.TAG_LIST-> {
                var list = ((ListTag)tag);
                var size = list.size();
                List<Object> result = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    result.add(i,tagToLua(list.get(i)));
                }
                yield result;
            }
            case Tag.TAG_COMPOUND-> {
                var compound = ((CompoundTag)tag);
                Map<String,Object> result = new LinkedHashMap<>(compound.size());
                for (String key: compound.getAllKeys()){result.put(key,tagToLua(compound.get(key)));}
                yield result;
            }
            default-> {
                HexCC.LOGGER.error("Failed to serialize {}",tag);
                yield null;
            }
        };
    }
}
