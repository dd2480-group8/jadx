package jadx.core.dex.visitors.regions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jadx.api.CCTool;
import jadx.core.dex.attributes.AFlag;
import jadx.core.dex.attributes.AType;
import jadx.core.dex.attributes.nodes.DeclareVariablesAttr;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.instructions.args.VarName;
import jadx.core.dex.nodes.IBlock;
import jadx.core.dex.nodes.IContainer;
import jadx.core.dex.nodes.IRegion;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.dex.regions.loops.ForLoop;
import jadx.core.dex.regions.loops.LoopRegion;
import jadx.core.dex.regions.loops.LoopType;
import jadx.core.dex.visitors.AbstractVisitor;
import jadx.core.utils.RegionUtils;
import jadx.core.utils.exceptions.JadxException;

public class ProcessVariables extends AbstractVisitor {

	private static class Variable {
		private final int regNum;
		private final ArgType type;

		public Variable(RegisterArg arg) {
			this.regNum = arg.getRegNum();
			this.type = arg.getType();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Variable variable = (Variable) o;
			return regNum == variable.regNum && type.equals(variable.type);
		}

		@Override
		public int hashCode() {
			return 31 * regNum + type.hashCode();
		}

		@Override
		public String toString() {
			return "r" + regNum + ":" + type;
		}
	}

	private static class Usage {
		private RegisterArg arg;
		private VarName varName;
		private IRegion argRegion;
		private final Set<IRegion> uses = new LinkedHashSet<>(2);
		private final Set<IRegion> assigns = new LinkedHashSet<>(2);

		public void setArg(RegisterArg arg) {
			this.arg = arg;
		}

		public RegisterArg getArg() {
			return arg;
		}

		public VarName getVarName() {
			return varName;
		}

		public void setVarName(VarName varName) {
			this.varName = varName;
		}

		public void setArgRegion(IRegion argRegion) {
			this.argRegion = argRegion;
		}

		public IRegion getArgRegion() {
			return argRegion;
		}

		public Set<IRegion> getAssigns() {
			return assigns;
		}

		public Set<IRegion> getUseRegions() {
			return uses;
		}

		@Override
		public String toString() {
			return arg + ", a:" + assigns + ", u:" + uses;
		}
	}

	private static class CollectUsageRegionVisitor extends TracedRegionVisitor {
		private final List<RegisterArg> args;
		private final Map<Variable, Usage> usageMap;

		public CollectUsageRegionVisitor(Map<Variable, Usage> usageMap) {
			this.usageMap = usageMap;
			this.args = new ArrayList<>();
		}

		@Override
		public void processBlockTraced(MethodNode mth, IBlock container, IRegion curRegion) {
			regionProcess(curRegion);
			int len = container.getInstructions().size();
			for (int i = 0; i < len; i++) {
				InsnNode insn = container.getInstructions().get(i);
				if (insn.contains(AFlag.SKIP)) {
					continue;
				}
				args.clear();
				processInsn(insn, curRegion);
			}
		}

		private void regionProcess(IRegion region) {
			if (region instanceof LoopRegion) {
				LoopRegion loopRegion = (LoopRegion) region;
				LoopType loopType = loopRegion.getType();
				if (loopType instanceof ForLoop) {
					ForLoop forLoop = (ForLoop) loopType;
					processInsn(forLoop.getInitInsn(), region);
					processInsn(forLoop.getIncrInsn(), region);
				}
			}
		}

		void processInsn(InsnNode insn, IRegion curRegion) {
			if (insn == null) {
				return;
			}
			// result
			RegisterArg result = insn.getResult();
			if (result != null && result.isRegister()) {
				Usage u = addToUsageMap(result, usageMap);
				if (u.getArg() == null) {
					u.setArg(result);
					u.setArgRegion(curRegion);
				}
				u.getAssigns().add(curRegion);
			}
			// args
			args.clear();
			insn.getRegisterArgs(args);
			for (RegisterArg arg : args) {
				Usage u = addToUsageMap(arg, usageMap);
				u.getUseRegions().add(curRegion);
			}
		}
	}

	@Override
	public void visit(MethodNode mth) throws JadxException {
		if (mth.isNoCode()) {
			CCTool.set("visit@ProcessVariables", 0);
			return;
		} else {
			CCTool.set("visit@ProcessVariables", 1);
		}
		List<RegisterArg> mthArguments = mth.getArguments(true);

		Map<Variable, Usage> usageMap = new LinkedHashMap<>();
		for (RegisterArg arg : mthArguments) {
			CCTool.set("visit@ProcessVariables", 2);
			addToUsageMap(arg, usageMap);
		}

		// collect all variables usage
		IRegionVisitor collect = new CollectUsageRegionVisitor(usageMap);
		DepthRegionTraversal.traverse(mth, collect);

		// reduce assigns map
		for (RegisterArg arg : mthArguments) {
			CCTool.set("visit@ProcessVariables", 3);
			usageMap.remove(new Variable(arg));
		}

		Iterator<Entry<Variable, Usage>> umIt = usageMap.entrySet().iterator();
		while (umIt.hasNext()) {
			CCTool.set("visit@ProcessVariables", 4);
			Entry<Variable, Usage> entry = umIt.next();
			Usage u = entry.getValue();
			// if no assigns => remove
			if (u.getAssigns().isEmpty()) {
				CCTool.set("visit@ProcessVariables", 5);
				umIt.remove();
				continue;
			} else {
				CCTool.set("visit@ProcessVariables", 6);
			}

			// variable declared at 'catch' clause
			InsnNode parentInsn = u.getArg().getParentInsn();
			if (parentInsn == null || parentInsn.getType() == InsnType.MOVE_EXCEPTION) {
				CCTool.set("visit@ProcessVariables", 7);
				umIt.remove();
			} else {
				CCTool.set("visit@ProcessVariables", 8);
			}
		}
		if (usageMap.isEmpty()) {
			CCTool.set("visit@ProcessVariables", 9);
			return;
		} else {
			CCTool.set("visit@ProcessVariables", 10);
		}

		for (Iterator<Entry<Variable, Usage>> it = usageMap.entrySet().iterator(); it.hasNext(); ) {
			CCTool.set("visit@ProcessVariables", 11);
			Entry<Variable, Usage> entry = it.next();
			Usage u = entry.getValue();
			// check if variable can be declared at current assigns
			for (IRegion assignRegion : u.getAssigns()) {
				CCTool.set("visit@ProcessVariables", 12);
				if (u.getArgRegion() == assignRegion
						&& canDeclareInRegion(u, assignRegion)
						&& declareAtAssign(u)) {
					CCTool.set("visit@ProcessVariables", 13);
					it.remove();
					break;
				} else {
					CCTool.set("visit@ProcessVariables", 14);
				}
			}
		}
		if (usageMap.isEmpty()) {
			CCTool.set("visit@ProcessVariables", 15);
			return;
		} else {
			CCTool.set("visit@ProcessVariables", 16);
		}

		// apply
		for (Entry<Variable, Usage> entry : usageMap.entrySet()) {
			CCTool.set("visit@ProcessVariables", 17);
			Usage u = entry.getValue();

			// find region which contain all usage regions
			Set<IRegion> set = u.getUseRegions();
			for (Iterator<IRegion> it = set.iterator(); it.hasNext(); ) {
				CCTool.set("visit@ProcessVariables", 18);
				IRegion r = it.next();
				IRegion parent = r.getParent();
				if (parent != null && set.contains(parent)) {
					CCTool.set("visit@ProcessVariables", 19);
					it.remove();
				} else {
					CCTool.set("visit@ProcessVariables", 20);
				}
			}
			IRegion region = null;
			if (!set.isEmpty()) {
				CCTool.set("visit@ProcessVariables", 21);
				region = set.iterator().next();
			} else if (!u.getAssigns().isEmpty()) {
				CCTool.set("visit@ProcessVariables", 22);
				region = u.getAssigns().iterator().next();
			} else {
				CCTool.set("visit@ProcessVariables", 23);
			}
			if (region == null) {
				CCTool.set("visit@ProcessVariables", 24);
				continue;
			} else {
				CCTool.set("visit@ProcessVariables", 25);
			}
			IRegion parent = region;
			boolean declared = false;
			while (parent != null) {
				CCTool.set("visit@ProcessVariables", 26);
				if (canDeclareInRegion(u, region)) {
					CCTool.set("visit@ProcessVariables", 27);
					declareVar(region, u.getArg());
					declared = true;
					break;
				} else {
					CCTool.set("visit@ProcessVariables", 28);
				}
				region = parent;
				parent = region.getParent();
			}
			if (!declared) {
				CCTool.set("visit@ProcessVariables", 29);
				declareVar(mth.getRegion(), u.getArg());
			} else {
				CCTool.set("visit@ProcessVariables", 30);
			}
		}
	}

	private static Usage addToUsageMap(RegisterArg arg, Map<Variable, Usage> usageMap) {
		Variable varId = new Variable(arg);
		Usage usage = usageMap.computeIfAbsent(varId, v -> new Usage());
		// merge variables names
		if (usage.getVarName() == null) {
			VarName argVN = arg.getSVar().getVarName();
			if (argVN == null) {
				argVN = new VarName();
				arg.getSVar().setVarName(argVN);
			}
			usage.setVarName(argVN);
		} else {
			arg.getSVar().setVarName(usage.getVarName());
		}
		return usage;
	}

	private static boolean declareAtAssign(Usage u) {
		RegisterArg arg = u.getArg();
		InsnNode parentInsn = arg.getParentInsn();
		if (parentInsn == null) {
			return false;
		}
		if (!arg.equals(parentInsn.getResult())) {
			return false;
		}
		parentInsn.add(AFlag.DECLARE_VAR);
		return true;
	}

	private static void declareVar(IContainer region, RegisterArg arg) {
		DeclareVariablesAttr dv = region.get(AType.DECLARE_VARIABLES);
		if (dv == null) {
			dv = new DeclareVariablesAttr();
			region.addAttr(dv);
		}
		dv.addVar(arg);
	}

	private static boolean canDeclareInRegion(Usage u, IRegion region) {
		// workaround for declare variables used in several loops
		if (region instanceof LoopRegion) {
			for (IRegion r : u.getAssigns()) {
				if (!RegionUtils.isRegionContainsRegion(region, r)) {
					return false;
				}
			}
		}
		// can't declare in else-if chain between 'else' and next 'if'
		if (region.contains(AFlag.ELSE_IF_CHAIN)) {
			return false;
		}
		// TODO: make index for faster search
		return isAllRegionsAfter(region, u.getAssigns())
				&& isAllRegionsAfter(region, u.getUseRegions());
	}

	private static boolean isAllRegionsAfter(IRegion region, Set<IRegion> others) {
		for (IRegion r : others) {
			if (!RegionUtils.isRegionContainsRegion(region, r)) {
				return false;
			}
		}
		return true;
	}
}
