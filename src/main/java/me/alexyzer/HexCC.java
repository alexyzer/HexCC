package me.alexyzer;

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser;
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser;
import me.alexyzer.computer.StaffPocketUpgrade;
import me.alexyzer.computer.StaffTurtleUpgrade;
import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexCC implements ModInitializer {
	public static final String MOD_ID = "hex-cc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModItems.initialize();
        Registry.register(
                (Registry<TurtleUpgradeSerialiser<?>>) BuiltInRegistries.REGISTRY.get(TurtleUpgradeSerialiser.registryId().location()),
                new ResourceLocation(MOD_ID, "staff"),
                StaffTurtleUpgrade.SERIALIZER
        );
        Registry.register(
                (Registry<PocketUpgradeSerialiser<?>>) BuiltInRegistries.REGISTRY.get(PocketUpgradeSerialiser.registryId().location()),
                new ResourceLocation(MOD_ID, "staff"),
                StaffPocketUpgrade.SERIALIZER
        );
	}
}