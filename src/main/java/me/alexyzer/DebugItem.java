package me.alexyzer;

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DebugItem extends Item {
    public DebugItem(Settings properties) {
        super(properties);
    }
    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand interactionHand) {
        if (level.isClient) return super.use(level, player, interactionHand);

        PlayerBasedCastEnv castEnv = new PlayerBasedCastEnv((ServerPlayerEntity) player, interactionHand) {
            @Override
            protected long extractMediaEnvironment(long cost, boolean simulate) {
                return 0;
            }

            @Override
            public Hand getCastingHand() {
                return null;
            }

            @Override
            public FrozenPigment getPigment() {
                return null;
            }
        };
        castEnv.printMessage(Text.literal("Weh"));
        CastingImage castImage = new CastingImage();
        CastingVM castVM = new CastingVM(castImage,castEnv);
        List<? extends Iota> castingList = List.of(
                new PatternIota(HexPattern.fromAngles("aqqqqq", HexDir.NORTH_EAST)),
                new PatternIota(HexPattern.fromAngles("de", HexDir.NORTH_EAST))
        );
        castVM.queueExecuteAndWrapIotas(castingList, (ServerWorld)level);
        return super.use(level, player, interactionHand);
    }
}
