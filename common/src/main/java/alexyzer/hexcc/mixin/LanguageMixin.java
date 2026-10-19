package alexyzer.hexcc.mixin;

import alexyzer.hexcc.client.LanguageWorkaround;
import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Language.class)
public class LanguageMixin {

    @Inject(method = "getInstance", at = @At("HEAD"), cancellable = true)
    private static void getInstance(CallbackInfoReturnable<Language> cir) {
        if (LanguageWorkaround.useEnglish.get()) {
            cir.setReturnValue(LanguageWorkaround.ENGLISH_MISHAPS);
            cir.cancel();
        }
    }
}
