package alexyzer.hexcc.mixin;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.HexSignature;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ConcurrentMap;

@org.spongepowered.asm.mixin.Mixin(at.petrak.hexcasting.common.casting.PatternRegistryManifest.class)
public interface PatternRegistryManifestAccessor {
    @Accessor("NORMAL_ACTION_LOOKUP")
    static ConcurrentMap<HexSignature, ResourceKey<ActionRegistryEntry>> getNormalLookup() {
        throw new UnsupportedOperationException();
    }
}
