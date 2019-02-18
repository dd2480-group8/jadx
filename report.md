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
4. Are exceptions taken into account in the given measurements? **Answer: Our program does not use exception in the functions.**
5. Is the documentation clear w.r.t. all the possible outcomes? **Answer: Not all of the functions. Some are.**

**Results from Lizard**

File column contains the class, method name, line numbers (interval) of the code and source file path in the core module (/jadx-core). This output is from running: **lizard -s cyclomatic_complexity**.

| Complexity | LOC | File | Purpose |
|---|---|---|---|
| 60 | 255 | makeInsnBody@233-523@[InsnGen.java](./jadx-core/src/main/java/jadx/core/codegen/InsnGen.java) | Generates code from instructions. |
| 34 | 125 | processSwitch@700-844@[RegionMaker.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/RegionMaker.java) | Handles a switch code block. |
| 33 | 105 | visit@41-161@[EnumVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/EnumVisitor.java) | Process enums (?). |
| 31 | 119 | extractFinally@117-254@[BlockFinallyExtract.java](./jadx-core/src/main/java/jadx/core/dex/visitors/blocksmaker/BlockFinallyExtract.java) | Extracts a finally code block.  |
| 31 | 98 | process@21-131@[PostTypeInference.java](./jadx-core/src/main/java/jadx/core/dex/visitors/typeinference/PostTypeInference.java) | Handle type inference. |
| 26 | 67 | mergeInternal@481-549@[ArgType.java](./jadx-core/src/main/java/jadx/core/dex/instructions/args/ArgType.java) | Merges types. |
| 25 | 74 | checkArrayForEach@128-206@[LoopRegionVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/LoopRegionVisitor.java) | Checks the (foreach) loop type. |
| 25 | 80 | visit@170-264@[ProcessVariables.java](./jadx-core/src/main/java/jadx/core/dex/visitors/regions/ProcessVariables.java) | Process variables (?). |
| 24 | 112 | process@56-179@[DebugInfoParser.java](./jadx-core/src/main/java/jadx/core/dex/nodes/parser/DebugInfoParser.java) |  |
| 23 | 77 | fixTypes@155-241@[ConstInlineVisitor.java](./jadx-core/src/main/java/jadx/core/dex/visitors/ConstInlineVisitor.java) |  |

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
