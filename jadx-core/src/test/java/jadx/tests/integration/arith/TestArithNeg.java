package jadx.tests.integration.arith;

import jadx.api.CCTool;
import org.junit.Test;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class TestArithNeg extends IntegrationTest {

    public static class TestCls {

        public void method(int a) {
            a = -a;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsString("a = -a;"));
        CCTool.printReport();

    }
}
