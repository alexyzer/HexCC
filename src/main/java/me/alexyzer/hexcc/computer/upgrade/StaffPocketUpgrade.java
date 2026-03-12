package me.alexyzer.hexcc.computer.upgrade;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.AbstractPocketUpgrade;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import me.alexyzer.hexcc.computer.peripheral.StaffPocketPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class StaffPocketUpgrade extends AbstractPocketUpgrade{
    public static final PocketUpgradeSerialiser<StaffPocketUpgrade> SERIALIZER =
            PocketUpgradeSerialiser.simpleWithCustomItem(StaffPocketUpgrade::new);

    protected StaffPocketUpgrade(Identifier id, ItemStack stack) {
        //At registering it as an upgrade
        super(id, stack);
    }
    @Override public IPeripheral createPeripheral(@NotNull IPocketAccess pocketAccess) {
        //When assembling peripheral
        return new StaffPocketPeripheral(pocketAccess);
    }
}
