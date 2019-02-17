# Report for assignment 3

## Project

Name: JADX

URL: https://github.com/dd2480-group8/jadx

A CLI tool and GUI tool to generate  Java source code from Android DEX and APK files. A decompiler basically.

**We are focusing on the core module, at /jadx-core with 38K LOC.**

## Onboarding experience

The project builds easily and as documented. It uses gradle which is self-contained with its own wrapper. Since it's a Java project, it requires a JDK (at least version 8). Gradle will run the tests when running **./gradle dist** and **./gradle build**, as instructed in the README.md. The project builds and tests without any errors. It is also easy to list all the possible build actions with **./gradle tasks**. It worked well on both Windows and Linux. 

## Complexity

1. What are your results for the ten most complex functions? (If ranking
is not easily possible: ten complex functions)?
   * Did all tools/methods get the same result?
   * Are the results clear?
2. Are the functions just complex, or also long?
3. What is the purpose of the functions?
4. Are exceptions taken into account in the given measurements?
5. Is the documentation clear w.r.t. all the possible outcomes?

**Results from Lizard**

File column contains the class, method name, line numbers (interval) of the code and source file path in the core module (/jadx-core). This output is from running: **lizard -s cyclomatic_complexity**.

| Complexity | LOC | File | Purpose |
|---|---|---|---|
| 222 | 407 | decode@86-583@[InsnDecoder.java](./jadx-core/src/main/java/jadx/core/dex/instructions/InsnDecoder.java) | Decodes a binary instruction (by OP code, parse parameters). |
| 60 | 255 | makeInsnBody@233-523@[InsnGen.java](./jadx-core/src/main/java/jadx/core/codegen/InsnGen.java) | Generates code from instructions. |
| 34 | 125 | processSwitch@700-844@[RegionMaker.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/RegionMaker.java) | Handles a switch code block. |
| 33 | 105 | visit@41-161@[EnumVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/EnumVisitor.java) | Process enums (?). |
| 32 | 39 | TestCls::test@15-53@[TestConditions15.java](./jadx-core/src/test/java/jadx/tests/integration/conditions/TestConditions15.java) | Test class for conditions. |
| 31 | 119 | extractFinally@117-254@[BlockFinallyExtract.java](./jadx-core/src/main/java/jadx/core/dex/visitors/blocksmaker/BlockFinallyExtract.java) | Extracts a finally code block.  |
| 31 | 98 | process@21-131@[PostTypeInference.java](./jadx-core/src/main/java/jadx/core/dex/visitors/typeinference/PostTypeInference.java) | Handle type inference. |
| 26 | 67 | mergeInternal@481-549@[ArgType.java](./jadx-core/src/main/java/jadx/core/dex/instructions/args/ArgType.java) | Merges types. |
| 25 | 74 | checkArrayForEach@128-206@[LoopRegionVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/LoopRegionVisitor.java) | Checks the (foreach) loop type. |
| 25 | 80 | visit@170-264@[ProcessVariables.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/ProcessVariables.java) | Process variables (?). |

**Calculations by Simon**

Remember to count inline conditionals (foo ? ... : ...). Conditionals are within if-statements (where ... && ... is counted as separate conditionals), loops are for-loops and while-loops and cases are within switch statements.

| File | Complexity (Conditionals, Loops, Cases) |
|---|---|
| processSwitch@700-844@[RegionMaker.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/RegionMaker.java) | 34 (26, 7, 0) |
| visit@41-161@[EnumVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/EnumVisitor.java) | 33 (26, 6, 0) |
| extractFinally@117-254@[BlockFinallyExtract.java](./jadx-core/src/main/java/jadx/core/dex/visitors/blocksmaker/BlockFinallyExtract.java) | 31 (23, 7, 0) |
| process@21-131@[PostTypeInference.java](./jadx-core/src/main/java/jadx/core/dex/visitors/typeinference/PostTypeInference.java) | 31 (17, 3, 10) |
| mergeInternal@481-549@[ArgType.java](./jadx-core/src/main/java/jadx/core/dex/instructions/args/ArgType.java) | 26 (24, 1, 0) |

**Calculations by Michelle**


| File | Complexity  |
|---|---|
| processSwitch@700-844@[RegionMaker.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/RegionMaker.java) | |
| visit@41-161@[EnumVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/EnumVisitor.java) | |
| extractFinally@117-254@[BlockFinallyExtract.java](./jadx-core/src/main/java/jadx/core/dex/visitors/blocksmaker/BlockFinallyExtract.java) |  |
| process@21-131@[PostTypeInference.java](./jadx-core/src/main/java/jadx/core/dex/visitors/typeinference/PostTypeInference.java) |  |
| mergeInternal@481-549@[ArgType.java](./jadx-core/src/main/java/jadx/core/dex/instructions/args/ArgType.java) |  |

## Coverage

### Tools

Document your experience in using a "new"/different coverage tool.

How well was the tool documented? Was it possible/easy/difficult to
integrate it with your build environment?

### DYI

Show a patch that show the instrumented code in main (or the unit
test setup), and the ten methods where branch coverage is measured.

The patch is probably too long to be copied here, so please add
the git command that is used to obtain the patch instead:

git diff ...

What kinds of constructs does your tool support, and how accurate is
its output?

### Evaluation

Report of old coverage: [link]

Report of new coverage: [link]

Test cases added:

git diff ...

## Refactoring

Plan for refactoring complex code:

Carried out refactoring (optional)

git diff ...

## Effort spent

For each team member, how much time was spent in

1. plenary discussions/meetings;

2. discussions within parts of the group;

3. reading documentation;

4. configuration;

5. analyzing code/output;

6. writing documentation;

7. writing code;

8. running code?

## Overall experience

What are your main take-aways from this project? What did you learn?

Is there something special you want to mention here?
