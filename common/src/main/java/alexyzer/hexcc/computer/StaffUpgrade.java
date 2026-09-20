package alexyzer.hexcc.computer;

import alexyzer.hexcc.HexCC;
import at.petrak.hexcasting.common.lib.HexItems;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeBase;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

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

    public @Nullable IPeripheral createPeripheral() {
        return new StaffPeripheral();
    }
}
