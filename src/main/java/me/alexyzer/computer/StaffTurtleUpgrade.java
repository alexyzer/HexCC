package me.alexyzer.computer;

import at.petrak.hexcasting.common.lib.HexItems;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class StaffTurtleUpgrade extends AbstractTurtleUpgrade {
    public static final TurtleUpgradeSerialiser<StaffTurtleUpgrade> SERIALIZER = TurtleUpgradeSerialiser.simple(StaffTurtleUpgrade::new);
    protected StaffTurtleUpgrade(ResourceLocation id) {
        //At registering it as an upgrade
        super(id, TurtleUpgradeType.PERIPHERAL,"Casting", new ItemStack(HexItems.STAFF_MINDSPLICE));
    }
    @Override public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        //When assembling peripheral
        return new StaffPeripheral(turtle, side);
    }
}
