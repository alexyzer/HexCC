package alexyzer.hexcc.computer;

import alexyzer.hexcc.HexCC;
import at.petrak.hexcasting.common.lib.HexItems;
import dan200.computercraft.api.upgrades.UpgradeBase;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class StaffUpgrade implements UpgradeBase {

    public final static String TYPE_STR = "staff";
    public final static ResourceLocation RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath(HexCC.MOD_ID, TYPE_STR);

    @Override
    public Component getAdjective() {
        return Component.translatable("upgrade.hexcc.staff.adjective");
    }

    @Override
    public ItemStack getCraftingItem() {
        return HexItems.STAFF_MINDSPLICE.get().getDefaultInstance();
    }
}
