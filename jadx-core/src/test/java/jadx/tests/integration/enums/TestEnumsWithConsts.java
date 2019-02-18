package jadx.tests.integration.enums;

import org.junit.Test;

import jadx.api.CCTool;
import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;

import static jadx.tests.api.utils.JadxMatchers.containsLines;
import static org.junit.Assert.assertThat;

public class TestEnumsWithConsts extends IntegrationTest {

	public static class TestCls {

		public static final int C1 = 1;
		public static final int C2 = 2;
		public static final int C4 = 4;

		public static final String S = "NORTH";

		public enum Direction {
			NORTH,
			SOUTH,
			EAST,
			WEST
		}
	}

	@Test
	public void test() {
		ClassNode cls = getClassNode(TestCls.class);
		String code = cls.getCode().toString();

		assertThat(code, containsLines(1, "public enum Direction {",
				indent(1) + "NORTH,",
				indent(1) + "SOUTH,",
				indent(1) + "EAST,",
				indent(1) + "WEST",
				"}"));

				CCTool.printReport();
	}
}
