package alexyzer.hexcc.neoforge;

import alexyzer.hexcc.computer.StaffPocketUpgrade;
import alexyzer.hexcc.computer.StaffTurtleUpgrade;
import alexyzer.hexcc.computer.StaffUpgrade;
import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import alexyzer.hexcc.HexCC;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(HexCC.MOD_ID)
@EventBusSubscriber(modid = HexCC.MOD_ID)
public final class HexCCNeoForge {
    public HexCCNeoForge() {
        // Run our common setup.
        HexCC.init();
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(
                ITurtleUpgrade.typeRegistry(),
                StaffUpgrade.RESOURCE_LOCATION,
                () -> StaffTurtleUpgrade.TYPE
        );
        event.register(
                IPocketUpgrade.typeRegistry(),
                StaffUpgrade.RESOURCE_LOCATION,
                () -> StaffPocketUpgrade.TYPE
        );
    }

    @SubscribeEvent
    public static void registerTurtleModellers(RegisterTurtleModellersEvent event) {
        event.register(StaffTurtleUpgrade.TYPE, TurtleUpgradeModeller.flatItem());
    }
}
