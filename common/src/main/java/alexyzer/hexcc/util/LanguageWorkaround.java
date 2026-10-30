package alexyzer.hexcc.util;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

import java.util.function.Function;
import java.util.function.Supplier;

public class LanguageWorkaround {

    public static Language CLIENT_ENGLISH;
    public static final ThreadLocal<Boolean> useClientEnglish = ThreadLocal.withInitial(() -> false);

    public static String getClientEnglishComponentString(Component component) {
        try {
            useClientEnglish.set(true);
            return component.getString();
        } finally {
            useClientEnglish.set(false);
        }
    }

    public static Supplier<Language> getEnglishLanguage = Language::getInstance;
    public static Function<Component, String> getEnglishComponentString = Component::getString;
}
