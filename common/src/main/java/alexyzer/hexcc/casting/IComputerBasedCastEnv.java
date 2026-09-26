package alexyzer.hexcc.casting;

import alexyzer.hexcc.HexCCUtil;
import java.util.List;

public interface IComputerBasedCastEnv {

    double DEFAULT_AMBIT_RADIUS = 16;

    HexCCUtil.DumpableList<String> getRevealBuffer();

    default List<String> dumpRevealBuffer() {
        return getRevealBuffer().dump();
    }
}
