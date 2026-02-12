package me.alexyzer.hexcc.casting;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.utils.MediaHelper;
import java.util.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class HexcastingUtil {
    private static final Comparator<ADMediaHolder> PRIORITY_COMPARATOR =
            (a, b) -> b.getConsumptionPriority() - a.getConsumptionPriority();

    public static List<ADMediaHolder> scanContainerForMediaStuff(Inventory inventory) {
        int size = inventory.size();
        List<ADMediaHolder> holders = new ArrayList<>(Math.min(size, 16));

        for (int i = 0; i < size; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()||!MediaHelper.isMediaItem(stack)) continue;
            ADMediaHolder holder = HexAPI.instance().findMediaHolder(stack);
            if (holder.canProvide()) holders.add(holder);
        }

        holders.sort(PRIORITY_COMPARATOR);
        return holders;
    }

    public static List<ADMediaHolder> scanItemsForMediaStuff(Iterable<ItemStack> items) {
        List<ADMediaHolder> mediaHolders = new ArrayList<>(16);

        for (ItemStack item: items) {
            if(item.isEmpty() || !MediaHelper.isMediaItem(item)) continue;
            ADMediaHolder holder = HexAPI.instance().findMediaHolder(item);
            if (holder.canProvide()) mediaHolders.add(holder);
        }

        mediaHolders.sort(PRIORITY_COMPARATOR);
        return mediaHolders;
    }
}