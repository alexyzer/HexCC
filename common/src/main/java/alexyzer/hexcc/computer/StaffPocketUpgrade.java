package alexyzer.hexcc.computer;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import org.jspecify.annotations.Nullable;

public class StaffPocketUpgrade extends StaffUpgrade implements IPocketUpgrade {

    public static final StaffPocketUpgrade INSTANCE = new StaffPocketUpgrade();
    public static final UpgradeType<IPocketUpgrade> TYPE = UpgradeType.simple(INSTANCE);

    @Override
    public UpgradeType<? extends IPocketUpgrade> getType() {
        return TYPE;
    }

    @Override
    public @Nullable IPeripheral createPeripheral(IPocketAccess access) {
        return createPeripheral();
    }
}
