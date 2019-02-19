package jadx.core.dex.visitors.typeinference;

import java.util.List;

import jadx.api.CCTool;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.IndexInsnNode;
import jadx.core.dex.instructions.InvokeNode;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.LiteralArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.nodes.DexNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;

public class PostTypeInference {

	private PostTypeInference() {
	}

	public static boolean process(MethodNode mth, InsnNode insn) {
		DexNode dex = mth.dex();
		switch (insn.getType()) {
			case CONST:
				CCTool.set("process@PostTypeInference", 0);
				RegisterArg res = insn.getResult();
				LiteralArg litArg = (LiteralArg) insn.getArg(0);
				if (res.getType().isObject()) {
					CCTool.set("process@PostTypeInference", 1);
					long lit = litArg.getLiteral();
					if (lit != 0) {
						CCTool.set("process@PostTypeInference", 2); // goal to reach
						// incorrect literal value for object
						ArgType type = lit == 1 ? ArgType.BOOLEAN : ArgType.INT;
						// can't merge with object -> force it
						litArg.setType(type);
						res.getSVar().setType(type);
						return true;
					} else {
						CCTool.set("process@PostTypeInference", 3);
					}
				} else {
					CCTool.set("process@PostTypeInference", 4);
				}

				return litArg.merge(dex, res);

			case MOVE: {
				CCTool.set("process@PostTypeInference", 5);
				boolean change = false;
				if (insn.getResult().merge(dex, insn.getArg(0))) {
					CCTool.set("process@PostTypeInference", 6);
					change = true;
				} else {
					CCTool.set("process@PostTypeInference", 7);
				}
				if (insn.getArg(0).merge(dex, insn.getResult())) {
					CCTool.set("process@PostTypeInference", 8);
					change = true;
				} else {
					CCTool.set("process@PostTypeInference", 9);
				}
				return change;
			}

			case AGET:
				CCTool.set("process@PostTypeInference", 10);
				return fixArrayTypes(dex, insn.getArg(0), insn.getResult());

			case APUT:
				CCTool.set("process@PostTypeInference", 11);
				return fixArrayTypes(dex, insn.getArg(0), insn.getArg(2));

			case IF: {
				CCTool.set("process@PostTypeInference", 12);
				boolean change = false;
				if (insn.getArg(1).merge(dex, insn.getArg(0))) {
					CCTool.set("process@PostTypeInference", 13);
					change = true;
				} else {
					CCTool.set("process@PostTypeInference", 14);
				}
				if (insn.getArg(0).merge(dex, insn.getArg(1))) {
					CCTool.set("process@PostTypeInference", 15); // goal to reach
					change = true;
				} else {
					CCTool.set("process@PostTypeInference", 16);
				}
				return change;
			}

			// check argument types for overloaded methods
			case INVOKE: {
				CCTool.set("process@PostTypeInference", 17);
				boolean change = false;
				InvokeNode inv = (InvokeNode) insn;
				MethodInfo callMth = inv.getCallMth();
				MethodNode node = mth.dex().resolveMethod(callMth);
				if (node != null && node.isArgsOverload()) {
					CCTool.set("process@PostTypeInference", 18);
					List<ArgType> args = callMth.getArgumentsTypes();
					int j = inv.getArgsCount() - 1;
					for (int i = args.size() - 1; i >= 0; i--) {
						CCTool.set("process@PostTypeInference", 19);
						ArgType argType = args.get(i);
						InsnArg insnArg = inv.getArg(j--);
						if (insnArg.isRegister() && !argType.equals(insnArg.getType())) {
							CCTool.set("process@PostTypeInference", 20);
							insnArg.setType(argType);
							change = true;
						} else {
							CCTool.set("process@PostTypeInference", 21);
						}
					}
				} else {
					CCTool.set("process@PostTypeInference", 22);
				}
				return change;
			}

			case CHECK_CAST: {
				CCTool.set("process@PostTypeInference", 23);
				ArgType castType = (ArgType) ((IndexInsnNode) insn).getIndex();
				RegisterArg result = insn.getResult();
				ArgType resultType = result.getType();
				// don't override generic types of same base class
				boolean skip = castType.isObject() && resultType.isObject()
						&& castType.getObject().equals(resultType.getObject());
				if (!skip) {
					CCTool.set("process@PostTypeInference", 24);
					// workaround for compiler bug (see TestDuplicateCast)
					result.getSVar().setType(castType);
				} else {
					CCTool.set("process@PostTypeInference", 25);
				}
				return true;
			}

			case PHI:
				CCTool.set("process@PostTypeInference", 26);
			case MERGE: {
				CCTool.set("process@PostTypeInference", 27);
				ArgType type = insn.getResult().getType();
				if (!type.isTypeKnown()) {
					CCTool.set("process@PostTypeInference", 28);
					for (InsnArg arg : insn.getArguments()) {
						CCTool.set("process@PostTypeInference", 29);
						if (arg.getType().isTypeKnown()) {
							CCTool.set("process@PostTypeInference", 30); // goal to reach
							type = arg.getType();
							break;
						} else {
							CCTool.set("process@PostTypeInference", 31);
						}
					}
				} else {
					CCTool.set("process@PostTypeInference", 32);
				}
				boolean changed = false;
				if (updateType(insn.getResult(), type)) {
					CCTool.set("process@PostTypeInference", 33); // goal to reach
					changed = true;
				} else {
					CCTool.set("process@PostTypeInference", 34);
				}
				for (int i = 0; i < insn.getArgsCount(); i++) {
					CCTool.set("process@PostTypeInference", 35);
					RegisterArg arg = (RegisterArg) insn.getArg(i);
					if (updateType(arg, type)) {
						CCTool.set("process@PostTypeInference", 36);
						changed = true;
					} else {
						CCTool.set("process@PostTypeInference", 37);
					}
				}
				return changed;
			}

			default:
				CCTool.set("process@PostTypeInference", 38);
				break;
		}
		return false;
	}

	private static boolean updateType(RegisterArg arg, ArgType type) {
		ArgType prevType = arg.getType();
		if (prevType == null || !prevType.equals(type)) {
			arg.setType(type);
			return true;
		}
		return false;
	}

	private static boolean fixArrayTypes(DexNode dex, InsnArg array, InsnArg elem) {
		boolean change = false;
		if (!elem.getType().isTypeKnown() && elem.merge(dex, array.getType().getArrayElement())) {
			change = true;
		}
		if (!array.getType().isTypeKnown() && array.merge(dex, ArgType.array(elem.getType()))) {
			change = true;
		}
		return change;
	}
}
