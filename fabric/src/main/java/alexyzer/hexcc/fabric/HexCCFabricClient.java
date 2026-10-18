package alexyzer.hexcc.fabric;

import alexyzer.hexcc.HexCC;
import alexyzer.hexcc.computer.StaffTurtleUpgrade;
import dan200.computercraft.api.client.FabricComputerCraftAPIClient;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import net.fabricmc.api.ClientModInitializer;

public final class HexCCFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HexCC.onClientSetup();
        FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(StaffTurtleUpgrade.TYPE, TurtleUpgradeModeller.flatItem());
    }
}
