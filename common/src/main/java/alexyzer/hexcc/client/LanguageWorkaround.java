package alexyzer.hexcc.client;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import net.minecraft.locale.Language;
import org.jetbrains.annotations.Nullable;

public class LanguageWorkaround {

    public static Language ENGLISH_MISHAPS;

    static final Object LANGUAGE_LOCK = new Object();
    public static @Nullable String getEnglishMishapMessage(CastingEnvironment env, Mishap mishap, Mishap.Context ctx) {
        var component = mishap.errorMessageWithName(env, ctx);
        if (component == null) return null;

        //Language.instance is volatile, yet this function may be used asynchronously in a computer thread
        synchronized (LANGUAGE_LOCK) {
            var original = Language.getInstance();
            try {
                Language.inject(ENGLISH_MISHAPS);
                return component.getString();
            } finally {
                Language.inject(original);
            }
        }
    }
}
