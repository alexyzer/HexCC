package me.alexyzer;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import me.alexyzer.hexcc.computer.upgrade.StaffPocketUpgrade;
import me.alexyzer.hexcc.computer.upgrade.StaffTurtleUpgrade;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexCC implements ModInitializer {
	public static final String MOD_ID = "hex-cc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModItems.initialize();
        Registry.register(
                (Registry<TurtleUpgradeSerialiser<?>>) Registries.REGISTRIES.get(TurtleUpgradeSerialiser.registryId().getValue()),
                new Identifier(HexCC.MOD_ID, "staff"),
                StaffTurtleUpgrade.SERIALIZER
        );
        Registry.register(
                (Registry<PocketUpgradeSerialiser<?>>) Registries.REGISTRIES.get(PocketUpgradeSerialiser.registryId().getValue()),
                new Identifier(HexCC.MOD_ID, "staff"),
                StaffPocketUpgrade.SERIALIZER
        );
        ComputerCraftAPI.registerAPIFactory(HexCCLuaAPI::new);
    }
}