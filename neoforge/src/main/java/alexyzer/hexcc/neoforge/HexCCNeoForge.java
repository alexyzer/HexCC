package alexyzer.hexcc.neoforge;

import net.neoforged.fml.common.Mod;

import alexyzer.hexcc.HexCC;

@Mod(HexCC.MOD_ID)
public final class HexCCNeoForge {
    public HexCCNeoForge() {
        // Run our common setup.
        HexCC.init();
    }
}
