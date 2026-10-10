package alexyzer.hexcc.fabric;

import alexyzer.hexcc.HexCC;
import alexyzer.hexcc.computer.StaffPocketUpgrade;
import alexyzer.hexcc.computer.StaffTurtleUpgrade;
import alexyzer.hexcc.computer.StaffUpgrade;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.upgrades.UpgradeType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class HexCCFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        HexCC.onCommonSetup();

        @SuppressWarnings("unchecked")
        var TURTLE_UPGRADE_SERIALIZERS = (Registry<UpgradeType<? extends ITurtleUpgrade>>) BuiltInRegistries.REGISTRY.get(ITurtleUpgrade.typeRegistry().location());
        assert TURTLE_UPGRADE_SERIALIZERS != null;

        @SuppressWarnings("unchecked")
        var POCKET_UPGRADE_SERIALIZERS = (Registry<UpgradeType<? extends IPocketUpgrade>>) BuiltInRegistries.REGISTRY.get(IPocketUpgrade.typeRegistry().location());
        assert POCKET_UPGRADE_SERIALIZERS != null;

        Registry.register(TURTLE_UPGRADE_SERIALIZERS, StaffUpgrade.RESOURCE_LOCATION, StaffTurtleUpgrade.TYPE);
        Registry.register(POCKET_UPGRADE_SERIALIZERS, StaffUpgrade.RESOURCE_LOCATION, StaffPocketUpgrade.TYPE);

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleResourceReloadListener<Void>() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.fromNamespaceAndPath(HexCC.MOD_ID, "reload");
            }

            @Override
            public CompletableFuture<Void> load(ResourceManager resourceManager, ProfilerFiller profilerFiller, Executor executor) {
                return CompletableFuture.runAsync(() -> HexCC.onReloadAsync(resourceManager), executor);
            }

            @Override
            public CompletableFuture<Void> apply(Void o, ResourceManager resourceManager, ProfilerFiller profilerFiller, Executor executor) {
                //Do nothing since all the work is done asynchronously
                return CompletableFuture.completedFuture(null);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(HexCC::onServerStopping);
    }
}
