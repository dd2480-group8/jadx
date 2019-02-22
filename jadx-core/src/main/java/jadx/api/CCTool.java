package jadx.api;

import java.io.FileWriter;
import java.util.HashMap;

public final class CCTool {
    private static boolean initialized = false;
    private static HashMap<String, HashMap<Integer, Boolean>> cc;

    public static void set(String methodName, Integer branchId) {
        if (!initialized) {
            initialize();
        }

        cc.get(methodName).put(branchId, true);
    }

    public static void printReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------------------------\n");
        sb.append("Code Coverage Report\n");

        for (String methodName : cc.keySet()) {
			HashMap<Integer, Boolean> map = cc.get(methodName);
			int covered = 0;
			int expected = map.size();

            // Count covered branches.
            StringBuilder branchSb = new StringBuilder();
            for (Integer branchId : map.keySet()) {
                Boolean value = map.get(branchId);
				if (value) {
					covered++;
                }
                branchSb.append("\tBranch: " + branchId + ": " + (value ? "Covered" : "Missed") + "\n");
            }

            sb.append("Method: " + methodName + ": " + (covered / (double)expected * 100) + "\n");
            sb.append(branchSb.toString() + "\n");
        }

        sb.append("-----------------------------------------------------------\n");
        System.out.println(sb.toString());

        // Write report to file.
        try {
            FileWriter fileWriter = new FileWriter("cc_report.txt");
            fileWriter.write(sb.toString());
            fileWriter.close();
        } catch (Exception e) {
        }
    }

    private static void initialize() {
        // Each map will contain a string key that identifies the method
        // being tested. It has another map which contains integer keys for
        // every branch in the function, with booleans initially set to false -
        // indicating that the branch hasn't been covered. The function's job
        // is to set that boolean to true if the branch is visited.
        cc = new HashMap<String, HashMap<Integer, Boolean>>();

        // makeInsnBody in InsnGen
        HashMap<Integer, Boolean> makeInsnBodyMap = new HashMap<>();
        for (int i = 0; i < 69; i++) {
            makeInsnBodyMap.put(i, false);
        }
        cc.put("makeInsnBody@InsnGen", makeInsnBodyMap);

        // processSwitch in RegionMaker
        HashMap<Integer, Boolean> processSwitchMap = new HashMap<>();
        for (int i = 0; i < 48; i++) {
            processSwitchMap.put(i, false);
        }
        cc.put("processSwitch@RegionMaker", processSwitchMap);

        // visit in EnumVisitor
        HashMap<Integer, Boolean> visitMap = new HashMap<>();
        for (int i = 0; i < 40; i++) {
            visitMap.put(i, false);
        }
        cc.put("visit@EnumVisitor", visitMap);

        // extractFinally in BlockFinallyExtract
        HashMap<Integer, Boolean> extractFinallyMap = new HashMap<>();
        for (int i = 0; i < 49; i++) {
            extractFinallyMap.put(i, false);
        }
        cc.put("extractFinally@BlockFinallyExtract", extractFinallyMap);


        // mergeInternal in ArgType
        HashMap<Integer, Boolean> mergeInternalMap = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            mergeInternalMap.put(i, false);
        }
        cc.put("mergeInternal@ArgType", mergeInternalMap);

        // visit in ProcessVariables
        HashMap<Integer, Boolean> visitProcVarsMap = new HashMap<>();
        for (int i = 0; i < 31; i++) {
            visitProcVarsMap.put(i, false);
        }
        cc.put("visit@ProcessVariables", visitProcVarsMap);


        // process in PostTypeInference
        HashMap<Integer, Boolean> processMap = new HashMap<>();
        for (int i = 0; i < 39; i++) {
            processMap.put(i, false);
        }
        cc.put("process@PostTypeInference", processMap);

        // checkArrayForEach in LoopRegionVisitor
        HashMap<Integer, Boolean> checkArrayForEachMap = new HashMap<>();
        for (int i = 0; i < 31; i++) {
            checkArrayForEachMap.put(i, false);
        }
        cc.put("checkArrayForEach@LoopRegionVisitor", checkArrayForEachMap);

        // process in DebugInfoParser
        HashMap<Integer, Boolean> debugInfoParserMap = new HashMap<>();
        for (int i = 0; i < 32; i++) {
            debugInfoParserMap.put(i, false);
        }
        cc.put("process@DebugInfoParser", debugInfoParserMap);

        // fixTypes in ConstInlineVisitor
        HashMap<Integer, Boolean> fixTypesMap = new HashMap<>();
        for (int i = 0; i < 29; i++) {
            fixTypesMap.put(i, false);
        }
        cc.put("fixTypes@ConstInlineVisitor", fixTypesMap);

        // encodeValue in AnnotationGen;
        HashMap<Integer, Boolean> encodeValueMap = new HashMap<>();
        for (int i = 0; i < 19; i++) {
            encodeValueMap.put(i, false);
        }
        cc.put("encodeValue@AnnotationsGen", encodeValueMap);

        initialized = true;
    }
}
