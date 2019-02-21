package jadx.tests.integration.trycatch;

import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.InsnNode;
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
        ClassNode cls = getClassNode(TestCls.class);
        List<MethodNode> methods = cls.getMethods();
        ExceptionHandler handler = new ExceptionHandler(5, null);
        methods.get(0).addExceptionHandler(handler);
        TryCatchBlock tcBlock = new TryCatchBlock();
        tcBlock.addHandler(methods.get(0), 5, null);
        tcBlock.addHandler(methods.get(0), 5, null);
        BlockNode block = new BlockNode(5, 5);
        List<InsnNode> instructions = block.getInstructions();
        instructions.add(new InsnNode(InsnType.CONST, 5));
        handler.addBlock(block);
        handler.setTryBlock(tcBlock);
        handler.getTryBlock().getHandlersCount();

        assertFalse(BlockFinallyExtract.extractFinally(methods.get(0), handler));
        CCTool.printReport();
    }
}
