package jadx.tests.integration.annotations;

import jadx.api.CCTool;
import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import static jadx.tests.api.utils.JadxMatchers.containsOne;
import static org.junit.Assert.assertThat;

public class TestAnnotations3 extends IntegrationTest {

	public static class TestCls {

		private static @interface C {
			char c();
		}

		@C(c = 'c')
		public void methodC1() {
		}

		private static @interface D {
			double d();
		}

		@D(d = -22.33)
		public void methodD1() {
		}

		private static @interface S {
			short s();
		}

		@S(s = 14)
		public void methodS1() {
		}

		private static @interface B {
			byte b();
		}

		@B(b = 100)
		public void methodB1() {
		}
	}

	@Test
	public void test() {
		ClassNode cls = getClassNode(TestCls.class);
		String code = cls.getCode().toString();

		assertThat(code, containsOne("char c();"));
		assertThat(code, containsOne("double d();"));
		assertThat(code, containsOne("short s();"));
		assertThat(code, containsOne("byte b();"));
		CCTool.printReport();
	}
}
