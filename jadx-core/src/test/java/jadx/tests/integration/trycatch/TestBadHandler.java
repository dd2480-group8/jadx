package jadx.tests.integration.trycatch;

import jadx.core.dex.trycatch.TryCatchBlock;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import jadx.api.CCTool;
import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.core.dex.trycatch.ExceptionHandler;
import jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract;
import jadx.core.dex.nodes.MethodNode;
import sun.jvm.hotspot.opto.Block;

import static jadx.tests.api.utils.JadxMatchers.containsOne;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;

public class TestBadHandler extends IntegrationTest {

    public static class TestCls {
        public static void TestMethod() {
        }
    }

    @Test
    public void test() {
        ExceptionHandler test = new ExceptionHandler(5, null);
        test.setTryBlock(new TryCatchBlock());
        test.getTryBlock().getHandlersCount();
        ClassNode cls = getClassNode(TestCls.class);
        List<MethodNode> methods = cls.getMethods();
        methods.get(0).addExceptionHandler(test);
        assertFalse(BlockFinallyExtract.extractFinally(methods.get(0), test));
        CCTool.printReport();
    }
}
