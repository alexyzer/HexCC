package me.alexyzer.computer;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class StaffPocketUpgrade extends AbstractPocketUpgrade{
    public static final PocketUpgradeSerialiser<StaffPocketUpgrade> SERIALIZER =
            PocketUpgradeSerialiser.simpleWithCustomItem(StaffPocketUpgrade::new);

    protected StaffPocketUpgrade(ResourceLocation id, ItemStack stack) {
        //At registering it as an upgrade
        super(id, stack);
    }
    @Override public IPeripheral createPeripheral(IPocketAccess pocketAccess) {
        //When assembling peripheral
        return new StaffPeripheral(pocketAccess);
    }
}
