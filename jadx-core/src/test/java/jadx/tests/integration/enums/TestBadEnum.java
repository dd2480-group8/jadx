package jadx.tests.integration.enums;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jadx.api.CCTool;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.visitors.EnumVisitor;
import jadx.tests.api.IntegrationTest;

import static jadx.tests.api.utils.JadxMatchers.containsLines;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TestBadEnum extends IntegrationTest {

	public static class TestCls {

		public enum TestEnum {
			PLUS {
				public int apply(int x, int y) {
					return x + y;
				}
			},
			MINUS {
				public int apply(int x, int y) {
					return x - y;
				}
			};

			public abstract int apply(int x, int y);
		}

		public static void TestMethod() {
		}
	}

	@Test
	public void test() {
		ClassNode cls = getClassNode(TestCls.TestEnum.class);

		// Distort the method nodes of the class node.
		List<MethodNode> methods = cls.getMethods();
		methods.clear();
		methods.add(getClassNode(TestCls.class).getMethods().get(1));

		try {
			EnumVisitor enumVisitor = new EnumVisitor();
			assertTrue(enumVisitor.visit(cls));
		}
		catch (Exception e) {

		}

		CCTool.printReport();
	}
}
