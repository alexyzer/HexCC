package alexyzer.hexcc.fabric;

import alexyzer.hexcc.computer.StaffTurtleUpgrade;
import dan200.computercraft.api.client.FabricComputerCraftAPIClient;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import net.fabricmc.api.ClientModInitializer;

public final class HexCCFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(StaffTurtleUpgrade.TYPE, TurtleUpgradeModeller.flatItem());
    }
}
