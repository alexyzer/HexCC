package me.alexyzer.hexcc.computer.peripheral;

import dan200.computercraft.api.pocket.IPocketAccess;
import me.alexyzer.hexcc.casting.PocketCastingEnvironment;

public class StaffPocketPeripheral extends StaffPeripheral {
    public StaffPocketPeripheral(IPocketAccess pocketAccess) {
        super(new PocketCastingEnvironment(pocketAccess),pocketAccess.getUpgradeNBTData());
    }
}
