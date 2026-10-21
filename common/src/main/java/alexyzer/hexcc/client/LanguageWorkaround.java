package alexyzer.hexcc.client;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import net.minecraft.locale.Language;
import org.jetbrains.annotations.Nullable;

public class LanguageWorkaround {

    public static Language ENGLISH_MISHAPS;
    public static ThreadLocal<Boolean> useEnglish = ThreadLocal.withInitial(()->false);

    public static @Nullable String getEnglishMishapMessage(CastingEnvironment env, Mishap mishap, Mishap.Context ctx) {
        var component = mishap.errorMessageWithName(env, ctx);
        if (component == null) return null;

        try {
            useEnglish.set(true);
            return component.getString();
        } finally {
            useEnglish.set(false);
        }
    }
}
