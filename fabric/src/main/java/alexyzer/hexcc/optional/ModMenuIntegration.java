package alexyzer.hexcc.optional;

import alexyzer.hexcc.HexCCConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        //Use AutoConfig generated Screen
        return parent -> AutoConfig.getConfigScreen(HexCCConfig.class, parent).get();
    }
}
