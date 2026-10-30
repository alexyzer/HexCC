package alexyzer.hexcc.computer;

import alexyzer.hexcc.HexCC;
import alexyzer.hexcc.mixin.PatternRegistryManifestAccessor;
import alexyzer.hexcc.util.CompactLookup;
import alexyzer.hexcc.util.LuaOps;
import alexyzer.hexcc.util.Misc;
import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.math.HexSignature;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dan200.computercraft.api.lua.ILuaAPI;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HexLib implements ILuaAPI {

    public final static HexLib INSTANCE = new HexLib();
    public final static String MODULE_NAME = HexCC.MOD_ID+".hexlib";

    public static CompactLookup<String, HexPattern> NAME_TO_PATTERN;
    public static ArrayList<String> ACTIONS_LIST;

    @Override
    public String @NotNull [] getNames() {
        return new String[0];
    }

    @Override
    public @Nullable String getModuleName() {
        //Assigning as module instead
        return MODULE_NAME;
    }

    public static void generatePatternLookup() {
        final var ENGLISH = Misc.getEnglishLanguage.get();
        final var ACTION_REGISTRY = IXplatAbstractions.INSTANCE.getActionRegistry();
        final var NORMAL_ACTION_LOOKUP = PatternRegistryManifestAccessor.getNormalLookup();

        var names2patterns = new Object2ObjectOpenHashMap<String, HexPattern>(NORMAL_ACTION_LOOKUP.size());
        ACTIONS_LIST = new ArrayList<>(NORMAL_ACTION_LOOKUP.size());

        //Don't include great patterns
        NORMAL_ACTION_LOOKUP.forEach((signature, key) -> {
            var entry = ACTION_REGISTRY.get(key);
            var langKey = HexAPI.instance().getActionI18nKey(key);

            var pattern = entry.prototype();
            var name = ENGLISH.getOrDefault(langKey);
            var simpleName = Misc.simplifyName(name);

            ACTIONS_LIST.add(simpleName);
            names2patterns.put(simpleName.toLowerCase(), pattern);
        });

        NAME_TO_PATTERN = new CompactLookup<>(names2patterns);
    }


    //Lua

    @LuaFunction("listActions")
    public final List<String> lua$listActions() {
        return ACTIONS_LIST;
    }

    @LuaFunction("getPatternByAction")
    public final MethodResult lua$getPatternByAction(String name) {
        var pat = NAME_TO_PATTERN.apply(name.toLowerCase());
        if (pat == null) return MethodResult.of();
        return MethodResult.of(
                pat.getSignature().toAnglesString(),
                pat.getOrientation().name()
        );
    }

    @LuaFunction("getActionName")
    public final String lua$getActionName(Object signature) {
        return
                Misc.getEnglishLanguage.get().getOrDefault(
                HexAPI.instance().getActionI18nKey(
                PatternRegistryManifestAccessor.getNormalLookup().get(
                extractSignature(signature)
        )));
    }


    //Util

    public final static Pattern SIGNATURE_EXTRACTOR = Pattern.compile("\\b([aqweds]+)\\b", Pattern.CASE_INSENSITIVE);

    /**
     * Extracts {@link HexSignature} from lua string or table.
     * @param source a string containing angleString, action name, or table of serialized {@link PatternIota}.
     * @return {@link HexSignature} or {@code null} if not successful.
     */
    public static @Nullable HexSignature extractSignature(Object source) {
        try {
            switch (source) {
                case String string -> {
                    string = string.toLowerCase();

                    var actionPattern = NAME_TO_PATTERN.apply(string);
                    if (actionPattern != null) return actionPattern.getSignature();
                    else {
                        var matcher = SIGNATURE_EXTRACTOR.matcher(string);
                        if (matcher.find()) {
                            var signatureString = matcher.group();
                            var signatureBuilder = new HexSignature.Builder(true);
                            for (int i = 0; i < signatureString.length(); i++) {
                                var angle = HexAngle.fromChar(signatureString.charAt(i));
                                assert angle != null; //Already ruled out by SIGNATURE_EXTRACTOR and toLowerCase()
                                signatureBuilder.addAngle(angle);
                            }
                            return signatureBuilder.build();
                        }
                    }
                }
                case Map<?, ?> luaIota -> {
                    var iotaResult = IotaType.TYPED_CODEC.decode(LuaOps.INSTANCE, luaIota);
                    if (iotaResult.isSuccess()) {
                        var iota = iotaResult.getOrThrow().getFirst();
                        if (iota instanceof PatternIota pattern)
                            return pattern.getPattern().getSignature();
                    }
                }
                case null, default -> {}
            }
        } catch (IllegalStateException ignored) { }
        return null;
    }
}
