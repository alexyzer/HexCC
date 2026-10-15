package alexyzer.hexcc.neoforge;

import alexyzer.hexcc.HexCC;
import alexyzer.hexcc.HexCCConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = HexCC.MOD_ID, dist = Dist.CLIENT)
public class HexCCNeoForgeClient {
    public HexCCNeoForgeClient(ModContainer container) {
        //Use AutoConfig generated Screen
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> AutoConfig.getConfigScreen(HexCCConfig.class, parent).get());
    }
}
