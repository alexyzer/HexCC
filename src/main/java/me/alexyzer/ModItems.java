package me.alexyzer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItems {
    public static Item register(Item item, String id) {
        ResourceLocation itemID = new ResourceLocation(HexCC.MOD_ID, id);
        return Registry.register(BuiltInRegistries.ITEM, itemID, item);
    }
    public static void initialize() {
        register(new DebugItem(new Item.Properties()),"debug_item");
    }
}
