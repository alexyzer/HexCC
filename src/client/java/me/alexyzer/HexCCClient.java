package me.alexyzer;

import dan200.computercraft.api.client.FabricComputerCraftAPIClient;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import me.alexyzer.hexcc.computer.upgrade.StaffTurtleUpgrade;
import net.fabricmc.api.ClientModInitializer;

public class HexCCClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(
            StaffTurtleUpgrade.SERIALIZER,
            TurtleUpgradeModeller.flatItem()
        );
	}
}