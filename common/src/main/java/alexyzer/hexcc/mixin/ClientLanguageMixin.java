package alexyzer.hexcc.mixin;

import alexyzer.hexcc.util.Misc;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.server.packs.resources.ResourceManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("MissingOrInvalidOpcode")
@Mixin(ClientLanguage.class)
public abstract class ClientLanguageMixin {

    //Injecting into ClientLanguage to make a snapshot of English mishap translations

    /*
    public static ClientLanguage loadFrom(ResourceManager resourceManager, List<String> list, boolean bl) {
        Map<String, String> map = Maps.newHashMap();

        L5: for (String string : list) {
            String string2 = String.format(Locale.ROOT, "lang/%s.json", string);

            L9: for (String string3 : resourceManager.getNamespaces()) {
                try {
                    ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(string3, string2);
                    appendFrom(string, resourceManager.getResourceStack(resourceLocation), map);
                    <GOTO L12> //0
                } catch (Exception exception) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", new Object[]{string3, string2, exception.toString()});
                }
                L12:
                <GOTO L9> //1
            }
        //INJECTING HERE
        <GOTO L5> //2
        }

        return new ClientLanguage(ImmutableMap.copyOf(map), bl);
    }
    */

    @Inject(
            method = "loadFrom",
            at = @At(
                    value = "JUMP",
                    opcode = Opcodes.GOTO,
                    ordinal = 2
            )
    )
    @SuppressWarnings("UnresolvedLocalCapture") //Resolving at runtime
    private static void loadFrom(
            ResourceManager resourceManager, List<String> list, boolean bl, CallbackInfoReturnable<ClientLanguage> cir,
            //Find fist locals of that type, ignore rest
            @Local(ordinal = 0) Map<String, String> translations,
            @Local(ordinal = 0) String language
    ) {
        if (language.equals("en_us")) {
            var snapshot = ImmutableMap.<String, String>builder();
            translations.forEach((k, v) -> {
                //No need for other translations
                if (k.startsWith("hexcasting.mishap"))
                    snapshot.put(k, v);
            });
            Misc.ENGLISH_MISHAPS = snapshot.build();
        }
    }
}
