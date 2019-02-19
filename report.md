# Report for assignment 3

## Project

Name: JADX

URL: https://github.com/dd2480-group8/jadx

A CLI tool and GUI tool to generate  Java source code from Android DEX and APK files. A decompiler basically.

**We are focusing on the core module, at /jadx-core with 38K LOC.**

## Onboarding experience

The project builds easily and as documented. It uses gradle which is self-contained with its own wrapper. Since it's a Java project, it requires a JDK (at least version 8). Gradle will run the tests when running **./gradle dist** and **./gradle build**, as instructed in the README.md. The project builds and tests without any errors. It is also easy to list all the possible build actions with **./gradle tasks**. It worked well on both Windows and Linux. 

## Complexity

1. What are your results for the ten most complex functions? (If ranking is not easily possible: ten complex functions)? **Answer: See table below.**
   * Did all tools/methods get the same result? **Answer: Yes, once we didn't miss anything while counting the complexity by hand. We used Lizard to compuate the complexity as well. Our calculations are the same.**
   * Are the results clear? **Answer: Yes. Especially when looking into the most complex ones.**
2. Are the functions just complex, or also long? **Answer: Some of them are. Generally though they are very nested with branches.**
3. What is the purpose of the functions? **Answer: See table below. Some of them are related to the high CC. With many switch cases, because it needs to handle many different types of scenarios (OP codes). Although it could be simplified.**
4. Are exceptions taken into account in the given measurements? **Answer: Our program does not use exceptions in the functions.**
5. Is the documentation clear w.r.t. all the possible outcomes? **Answer: Not all of the functions. Some are.**

**Results from Lizard**

File column contains the class, method name, line numbers (interval) of the code and source file path in the core module (/jadx-core). This output is from running: **lizard -s cyclomatic_complexity**.

| Complexity | LOC | File | Purpose |
|---|---|---|---|
| 60 | 255 | makeInsnBody@233-523@[InsnGen.java](./jadx-core/src/main/java/jadx/core/codegen/InsnGen.java) | Generates code from instructions. |
| 34 | 125 | processSwitch@700-844@[RegionMaker.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/RegionMaker.java) | Handles a switch code block. |
| 33 | 105 | visit@41-161@[EnumVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/EnumVisitor.java) | Process enums. |
| 31 | 119 | extractFinally@117-254@[BlockFinallyExtract.java](./jadx-core/src/main/java/jadx/core/dex/visitors/blocksmaker/BlockFinallyExtract.java) | Extracts a finally code block.  |
| 31 | 98 | process@21-131@[PostTypeInference.java](./jadx-core/src/main/java/jadx/core/dex/visitors/typeinference/PostTypeInference.java) | Handle type inference. |
| 26 | 67 | mergeInternal@481-549@[ArgType.java](./jadx-core/src/main/java/jadx/core/dex/instructions/args/ArgType.java) | Merges types. |
| 25 | 74 | checkArrayForEach@128-206@[LoopRegionVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/LoopRegionVisitor.java) | Checks the (foreach) loop type. |
| 25 | 80 | visit@170-264@[ProcessVariables.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/ProcessVariables.java) | Process variables. |
| 24 | 112 | process@56-179@[DebugInfoParser.java](./jadx-core/src/main/java/jadx/core/dex/nodes/parser/DebugInfoParser.java) | Process different debug info. |
| 23 | 77 | fixTypes@155-241@[ConstInlineVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/ConstInlineVisitor.java) | Similiar to PostTypeInference.process, but handles const inlines. |

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


| File | Complexity (Conditionals, Loops, Cases |
|---|---|
| processSwitch@700-844@[RegionMaker.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/RegionMaker.java) | 34 (26, 7, 0) |
| visit@41-161@[EnumVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/EnumVisitor.java) | 33 (26, 6, 0) |
| extractFinally@117-254@[BlockFinallyExtract.java](./jadx-core/src/main/java/jadx/core/dex/visitors/blocksmaker/BlockFinallyExtract.java) | 31 (23, 7, 0) |
| process@21-131@[PostTypeInference.java](./jadx-core/src/main/java/jadx/core/dex/visitors/typeinference/PostTypeInference.java) | 31 (17, 3, 10) |
| mergeInternal@481-549@[ArgType.java](./jadx-core/src/main/java/jadx/core/dex/instructions/args/ArgType.java) | 26 (24, 1, 0) |

Our calculated results line up with each other and also with the results from Lizard, our tool. One thing that is rather unclear is whether to take exceptions into account when making the calculations or not. It would make sense to do so since an exception would affect the control flow graph, especially if you think of them as a possible branch. On the other hand an exception (in Java) often end up in a non-succesfully terminated program, so taking them into account might interfere with the complexity of the wanted outcome. In the calculated results above we have chosen to exclude exceptions. 

Observing the LOC of the functions woth high CC we see that most of them are long. Given the results from the Lizard, the length don't seem to have any linear correlation to the complexity considering the follwoing pairs of CC and it's corresponding LOC (CC,LOC): (32, 39), (34, 125), (25, 80). However, if we look at more distinct CC values we notice some correlation: (34, 125), (60, 255), (222, 407). One reason could be that in general, if you write more lines of code you're more likely to introduce complexity along the way, but other aspects such as the functionality of the code may also be a key factor. Which would explain why we see a clear increasement of CC with high difference in LOC, but there also exist a variety of CC in codes that are more similar in length. For example decode@86-583@[InsnDecoder.java] obviously need to handle a variety of cases and conditionals which explains the high complexity value. 

Documentation for the induced outcome of branches in the functions are rather non-existent in this project. 

## Coverage

### Tools

As instructed in the assignment, we implemented a manual coverage tool in the code. It was pretty tricky due to the size of the project and the nature of how the tests are run. Because the project is dealing with decompilation, much of the tests are run on code that is compiled. There was no entry point for all the tests, so the instrumentation had to be enforced in many places. The tool itself is small and can be seen here: [CCTool](https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/src/main/java/jadx/api/CCTool.java). The tool is used in the following manner.

Additionally, there was no ability to know when all the tests have been run. So instrumention had to be put in to store the intermediate results between test runs at the end of every test (**printReport**). A way to fix this would be to upgrade the JUnit framework to a later version, which has a richer support for annotations (e.g. @AfterAllMethods).

1. Introduce branch flags in the method that we want to measure the code coverage for. Example: [https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/src/main/java/jadx/core/dex/visitors/regions/RegionMaker.java#L709](https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/src/main/java/jadx/core/dex/visitors/regions/RegionMaker.java#L709).
1. Count the number of flags and allocate a HashMap for the flags in the CCTool **initialize** method. Example: [https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/src/main/java/jadx/api/CCTool.java#L62](https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/src/main/java/jadx/api/CCTool.java#L62).
1. Save the measurements after each test (console and file). Example: [https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/src/test/java/jadx/tests/integration/enums/TestSwitchOverEnum2.java#L58](https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/src/test/java/jadx/tests/integration/enums/TestSwitchOverEnum2.java#L58).
1. Run **./gradle build**.
1. View the **cc_report.txt** file in the jadx-core folder. Example [https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/cc_report.txt](https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/cc_report.txt).

In addition to our manual tool. The project runs Codecov which displays the code coverage as well. This can be explored here: [https://codecov.io/gh/skylot/jadx](https://codecov.io/gh/skylot/jadx). Our tool is a bit more granular as we measure individual methods, while Codecov measures the entire source file. However, both our tool and Codecov will discover areas that are not covered by tests.

### DIY

The patch is easiest displayed by multiple diff commands. 
1. Display the CCTool itself (which is self-initialized upon invokation): **git diff master cc jadx-core/src/main/java/jadx/api/CCTool.java**
1. Display the instrumented code in a unit test: **git diff master cc jadx-core/src/test/java/jadx/tests/integration/enums/TestEnums.java**. 
1. Display the instrumented code in the function (for reference): **git diff master cc jadx-core/src/main/java/jadx/core/dex/visitors/EnumVisitor.java**.

**The ten methods measured are listed above.**

Our tool supports measuring any branch where a flag is put in (with a unique branch ID). It is heavily dependant on the programmers accuracy, to not miss branches (such as inline conditionals). The output seems very accurate when comparing with the results from the Codecov tool.

### Evaluation

Report of old coverage: [https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/cc_report.txt](https://github.com/dd2480-group8/jadx/blob/cc/jadx-core/cc_report.txt)

Report of new coverage: [https://github.com/dd2480-group8/jadx/blob/cc-improve/jadx-core/cc_report.txt](https://github.com/dd2480-group8/jadx/blob/cc-improve/jadx-core/cc_report.txt)

Test cases added:

| File | git diff |
|---|---|
| [TestBadEnum.java](https://github.com/dd2480-group8/jadx/blob/cc-improve/jadx-core/src/test/java/jadx/tests/integration/enums/TestBadEnum.java) | git diff cc cc-improve jadx-core/src/test/java/jadx/tests/integration/enums/TestBadEnum.java |
| [TestBadSwitch.java](https://github.com/dd2480-group8/jadx/blob/cc-improve/jadx-core/src/test/java/jadx/tests/integration/switches/TestBadSwitch.java) | git diff cc cc-improve jadx-core/src/test/java/jadx/tests/integration/switches/TestBadSwitch.java |
| [TestUnknownTypes.java](https://github.com/dd2480-group8/jadx/blob/cc-improve/jadx-core/src/test/java/jadx/tests/integration/types/TestUnknownTypes.java) | git diff cc cc-improve jadx-core/src/test/java/jadx/tests/integration/types/TestUnknownTypes.java |
| [TestBadHandler.java](https://github.com/dd2480-group8/jadx/blob/cc-improve/jadx-core/src/test/java/jadx/tests/integration/trycatch/TestBadHandler.java) | git diff cc cc-improve jadx-core/src/test/java/jadx/tests/integration/trycatch/TestBadHandler.java |


Run above git diff commands for specific files or simply run **git diff cc cc-improve** for the entire branch.

## Refactoring

Plan for refactoring complex code:

Carried out refactoring (optional)

git diff ...

## Effort spent

For each team member, how much time was spent in

1. plenary discussions/meetings;
- Jonathan: 3h
- Ludvig: 
- Michelle: 
- Simon: 3h
- Shapour: - 

2. discussions within parts of the group;
- Jonathan: 0.5h
- Ludvig: 
- Michelle: 
- Simon: 0.5h
- Shapour: 0.5h

3. reading documentation;
- Jonathan: 1h
- Ludvig: 
- Michelle: 
- Simon: 4h
- Shapour: 1h

4. configuration;
- Jonathan: 0.5h
- Ludvig: 
- Michelle: 
- Simon: 1h
- Shapour: -

5. analyzing code/output;
- Jonathan: 0.5h
- Ludvig: 
- Michelle: 
- Simon: 0.5h
- Shapour: 

6. writing documentation;
- Jonathan: 0.2h
- Ludvig: 
- Michelle: 
- Simon: 0.5h
- Shapour: 0.2h

7. writing code;
- Jonathan: 3h
- Ludvig: 
- Michelle: 
- Simon: 4h
- Shapour: 2h

8. running code?
- Jonathan: 1h
- Ludvig: 
- Michelle: 
- Simon: 1h
- Shapour: 0.5h

## Overall experience

What are your main take-aways from this project? What did you learn?

Is there something special you want to mention here?
