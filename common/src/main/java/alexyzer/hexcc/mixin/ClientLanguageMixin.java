package alexyzer.hexcc.mixin;

import alexyzer.hexcc.util.CompactLookup;
import alexyzer.hexcc.util.LanguageWorkaround;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(ClientLanguage.class)
public abstract class ClientLanguageMixin {

    //Injecting into ClientLanguage to make a snapshot of English mishap translations

    /*
    public static ClientLanguage loadFrom(ResourceManager resourceManager, List<String> list, boolean bl) {
        Map<String, String> map = Maps.newHashMap();

        for(String string : list) {
            String string2 = String.format(Locale.ROOT, "lang/%s.json", string);

          * Iterator<String> instance = getNamespaces().iterator();
          * while (wrapIterator(instance, Iterator::hasNext)) {
          *     String string3 = instance.next()
                try {
                    ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(string3, string2);
                    appendFrom(string, resourceManager.getResourceStack(resourceLocation), map);
                } catch (Exception exception) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", new Object[]{string3, string2, exception.toString()});
                }
            }
        }

        return new ClientLanguage(ImmutableMap.copyOf(map), bl);
    }
    */

    @WrapOperation(
            method = "loadFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Iterator;hasNext()Z",
                    ordinal = 1 //Inner loop
            )
    )
    @SuppressWarnings("LocalMayUseName") //No it can't
    private static boolean wrapIterator(
            Iterator<String> instance, Operation<Boolean> original,
            //Using ordinal to combat differing mappings on different architectures
            @Local(ordinal = 0) List<String> languages,
            @Local(ordinal = 0) Map<String, String> translations,
            @Local(ordinal = 0) String language
    ) {
        var hasNext = original.call(instance);
        //After end of inner iterator
        if (!hasNext && language.equals("en_us")) {
            //If the client language is not just English, use the workaround
            if (languages.size() != 1) {
                if (LanguageWorkaround.CLIENT_ENGLISH == null) {
                    var snapshot = new CompactLookup.Map<>(translations);
                    LanguageWorkaround.CLIENT_ENGLISH = new ClientLanguage(snapshot, false);
                }
                LanguageWorkaround.getEnglishLanguage = () -> LanguageWorkaround.CLIENT_ENGLISH;
                LanguageWorkaround.getEnglishComponentString = LanguageWorkaround::getClientEnglishComponentString;
            } else {
                LanguageWorkaround.getEnglishLanguage = Language::getInstance;
                LanguageWorkaround.getEnglishComponentString = Component::getString;
            }
        }
        return hasNext;
    }
}
