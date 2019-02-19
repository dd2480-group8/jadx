package jadx.tests.integration.loops;

import org.junit.Test;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;

import static jadx.tests.api.utils.JadxMatchers.containsLines;
import static org.junit.Assert.assertThat;
import jadx.api.CCTool;

public class TestArrayForEach3 extends IntegrationTest {

    public static class TestCls {

        private int test(int[] a) {
            int sum = 0;
            for (int n : a) {
                sum++;
            }
            return sum;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsLines(2,
                "int sum = 0;",
                "for (int n : a) {",
                indent(1) + "sum++;",
                "}",
                "return sum;"
        ));

        CCTool.printReport();
    }
}
