package alexyzer.hexcc.fabric;

import alexyzer.hexcc.computer.StaffPocketUpgrade;
import alexyzer.hexcc.computer.StaffTurtleUpgrade;
import alexyzer.hexcc.computer.StaffUpgrade;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.fabricmc.api.ModInitializer;

import alexyzer.hexcc.HexCC;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class HexCCFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        HexCC.init();

        @SuppressWarnings("unchecked")
        var TURTLE_UPGRADE_SERIALIZERS = (Registry<UpgradeType<? extends ITurtleUpgrade>>) BuiltInRegistries.REGISTRY.get(ITurtleUpgrade.typeRegistry().location());
        assert TURTLE_UPGRADE_SERIALIZERS != null;

        @SuppressWarnings("unchecked")
        var POCKET_UPGRADE_SERIALIZERS = (Registry<UpgradeType<? extends IPocketUpgrade>>) BuiltInRegistries.REGISTRY.get(IPocketUpgrade.typeRegistry().location());
        assert POCKET_UPGRADE_SERIALIZERS != null;

        Registry.register(TURTLE_UPGRADE_SERIALIZERS, StaffUpgrade.RESOURCE_LOCATION, StaffTurtleUpgrade.TYPE);
        Registry.register(POCKET_UPGRADE_SERIALIZERS, StaffUpgrade.RESOURCE_LOCATION, StaffPocketUpgrade.TYPE);
    }
}
