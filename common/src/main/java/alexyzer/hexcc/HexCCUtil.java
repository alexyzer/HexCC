package alexyzer.hexcc;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.lib.HexDamageTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HexCCUtil {

    public static List<ItemStack> getItems(Container container) {
        var stackArr = new ItemStack[container.getContainerSize()];
        for (int i = 0; i < container.getContainerSize(); i++) {
            stackArr[i] = container.getItem(i);
        }
        return Arrays.asList(stackArr);
    }

    public static class DumpableList<T> extends ArrayList<T> {
        public List<T> dump() {
            var copy = new ArrayList<>(this);
            this.clear();
            return copy;
        }
    }

    /**
     * Adapted from {@link MediaHelper#scanPlayerForMediaStuff(ServerPlayer)}.<p>
     * Works with any inventory.
     * @param optionalSize size of inventory, if unknown set optionalSize <= 0.
     */
    public static List<ADMediaHolder> scanItemsForMediaStuff(Iterable<ItemStack> items, int optionalSize) {
        List<ADMediaHolder> sources = optionalSize > 0 ? new ArrayList<>(optionalSize) : new ArrayList<>();

        items.forEach(item -> {
            var holder = HexAPI.instance().findMediaHolder(item);
            if (holder!= null && holder.canProvide()) {
                sources.add(holder);
            }
        });

        sources.sort((a, b) -> {
            int itemPriority = Integer.compare(
                    b.getConsumptionPriority(),
                    a.getConsumptionPriority()
            );
            if (itemPriority != 0) return itemPriority;

            return Long.compare(
                    b.withdrawMedia(-1, true),
                    a.withdrawMedia(-1, true)
            );
        });
        //No .reversed(), included into .sort()
        
        return sources;
    }

    /**
     * Adapted from {@link PlayerBasedCastEnv#extractMediaFromInventory(long, boolean, boolean)} as a static method.<p>
     * Works with any inventory.
     * @param damageableEntity entity to damage if {@code allowOvercast} is set.
     * @param optionalSize size of inventory, if unknown set optionalSize <= 0.
     */
    @SuppressWarnings("JavadocReference") //I hope javadoc working just in source is fine.
    public static long extractMediaFromInventory(Iterable<ItemStack> inventory, long cost, boolean allowOvercast, boolean simulate, @Nullable LivingEntity damageableEntity, int optionalSize) {

        List<ADMediaHolder> sources = HexCCUtil.scanItemsForMediaStuff(inventory, optionalSize);

        for(ADMediaHolder source : sources) {
            long found = MediaHelper.extractMedia(source, cost, false, simulate);
            cost -= found;
            if (cost <= 0L) {
                break;
            }
        }

        if (cost > 0L && allowOvercast && damageableEntity != null) {
            double mediaToHealth = HexConfig.common().mediaToHealthRate();
            double healthToRemove = Math.max((double)cost / mediaToHealth, 0.5F);

            if (simulate) {
                long simulatedRemovedMedia = Mth.ceil(Math.min(damageableEntity.getHealth(), healthToRemove) * mediaToHealth);
                if (damageableEntity.isInvulnerableTo(damageableEntity.damageSources().source(HexDamageTypes.OVERCAST))) {
                    simulatedRemovedMedia = 0L;
                }

                cost -= simulatedRemovedMedia;
            } else {
                double mediaAbleToCastFromHP = (double)damageableEntity.getHealth() * mediaToHealth;
                Mishap.trulyHurt(damageableEntity, damageableEntity.damageSources().source(HexDamageTypes.OVERCAST), (float)healthToRemove);
                int actuallyTaken = Mth.ceil(mediaAbleToCastFromHP - (double)damageableEntity.getHealth() * mediaToHealth);
                cost -= actuallyTaken;
            }
        }

        return cost;
    }
}