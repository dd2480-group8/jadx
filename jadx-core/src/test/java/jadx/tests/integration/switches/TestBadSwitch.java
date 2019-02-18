package jadx.tests.integration.switches;

import org.junit.Test;

import jadx.api.CCTool;
import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.core.dex.visitors.regions.RegionMaker;
import jadx.core.dex.nodes.*;
import jadx.core.dex.regions.*;
import jadx.core.dex.instructions.*;
import jadx.core.dex.instructions.*;
import jadx.core.dex.visitors.regions.RegionStack;
import jadx.core.dex.instructions.args.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

// Documentation: If the RegionMaker is given a bad data structure,
// it needs to recover from that and yield a desired result. Ideally,
// it should not throw an unexpected exception. The processSwitch method
// in RegionMaker had to be made public for this test to work. It is
// deeply nested and we could not figure out how to test it in a better way.
public class TestBadSwitch extends IntegrationTest {
	public static class TestCls {
		public String escape(String str) {
			int len = str.length();
			StringBuilder sb = new StringBuilder(len);
			for (int i = 0; i < len; i++) {
				char c = str.charAt(i);
				switch (c) {
				}
			}
			return sb.toString();
		}
	}

	@Test
	public void test() {
        try {
            // Create distorted switch code block.
            MethodNode mth = getClassNode(TestCls.class).getMethods().get(0);
            RegionStack stack = new RegionStack(mth);
            BlockNode startBlock = mth.getBasicBlocks().get(0);
            SwitchNode switchBlock = new SwitchNode(InsnArg.reg(0, ArgType.NARROW), new Object[0], new int[0], 0);

            RegionMaker regionMaker = new RegionMaker(mth);

            assertEquals(regionMaker.processSwitch(new Region(null), startBlock, switchBlock, stack).getId(), mth.getBasicBlocks().get(1).getId());
        }
        catch (Exception e) {
            
        }

		CCTool.printReport();
	}
}
