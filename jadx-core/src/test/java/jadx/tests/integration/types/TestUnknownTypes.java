package jadx.tests.integration.types;

import jadx.core.dex.instructions.args.PrimitiveType;
import org.junit.Test;

import jadx.api.CCTool;
import static org.junit.Assert.assertEquals;
import jadx.core.dex.instructions.args.ArgType;


public class TestUnknownTypes {
    @Test
    public void test() {
        PrimitiveType[] types = new PrimitiveType[1];
        PrimitiveType[] types2 = new PrimitiveType[0];
        ArgType a = ArgType.unknown(types);
        ArgType b = ArgType.unknown(types2);

        assertEquals(null, ArgType.merge(null, a, b));
        CCTool.printReport();
    }
}
