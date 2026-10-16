package alexyzer.hexcc.util;

import alexyzer.hexcc.HexCC;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StaffSaveComponent implements DataComponentType<CastingImage> {

    public static final StaffSaveComponent INSTANCE = new StaffSaveComponent();
    public static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath(HexCC.MOD_ID, "staff_image");

    @Override
    public @Nullable Codec<CastingImage> codec() {
        return CastingImage.getCODEC();
    }

    public static final StreamCodec<ByteBuf, CastingImage> NOOP_CODEC =
            new Misc.NoOpStreamCodec<>(new CastingImage());

    @Override
    public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, CastingImage> streamCodec() {
        return NOOP_CODEC;
    }

    @Override
    public boolean isTransient() {
        return false;
    }
}
