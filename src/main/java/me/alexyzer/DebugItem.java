package me.alexyzer;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.items.magic.ItemTrinket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class DebugItem extends Item {
    public DebugItem(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (level.isClientSide) return super.use(level, player, interactionHand);

        PlayerBasedCastEnv castEnv = new PlayerBasedCastEnv((ServerPlayer) player, interactionHand) {
            @Override
            protected long extractMediaEnvironment(long cost, boolean simulate) {
                return 0;
            }

            @Override
            public InteractionHand getCastingHand() {
                return null;
            }

            @Override
            public FrozenPigment getPigment() {
                return null;
            }
        };
        castEnv.printMessage(Component.literal("Weh"));
        CastingImage castImage = new CastingImage();
        CastingVM castVM = new CastingVM(castImage,castEnv);
        List<? extends Iota> castingList = List.of(
                new PatternIota(HexPattern.fromAngles("aqqqqq", HexDir.NORTH_EAST)),
                new PatternIota(HexPattern.fromAngles("de", HexDir.NORTH_EAST))
        );
        castVM.queueExecuteAndWrapIotas(castingList, (ServerLevel)level);
        return super.use(level, player, interactionHand);
    }
}
