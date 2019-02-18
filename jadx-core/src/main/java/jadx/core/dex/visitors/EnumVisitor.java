package jadx.core.dex.visitors;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import jadx.api.CCTool;
import jadx.core.codegen.TypeGen;
import jadx.core.deobf.NameMapper;
import jadx.core.dex.attributes.AFlag;
import jadx.core.dex.attributes.nodes.EnumClassAttr;
import jadx.core.dex.attributes.nodes.EnumClassAttr.EnumField;
import jadx.core.dex.info.ClassInfo;
import jadx.core.dex.info.FieldInfo;
import jadx.core.dex.info.MethodInfo;
import jadx.core.dex.instructions.IndexInsnNode;
import jadx.core.dex.instructions.InsnType;
import jadx.core.dex.instructions.args.ArgType;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.InsnWrapArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.instructions.mods.ConstructorInsn;
import jadx.core.dex.nodes.BlockNode;
import jadx.core.dex.nodes.ClassNode;
import jadx.core.dex.nodes.DexNode;
import jadx.core.dex.nodes.FieldNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.utils.ErrorsCounter;
import jadx.core.utils.InsnUtils;
import jadx.core.utils.exceptions.JadxException;

@JadxVisitor(
		name = "EnumVisitor",
		desc = "Restore enum classes",
		runAfter = {CodeShrinker.class, ModVisitor.class}
)
public class EnumVisitor extends AbstractVisitor {

	@Override
	public boolean visit(ClassNode cls) throws JadxException {
		if (!cls.isEnum()) {
			CCTool.set("visit@EnumVisitor", 0);
			return true;
		}
		else {
			CCTool.set("visit@EnumVisitor", 1);
		}
		// search class init method
		MethodNode staticMethod = null;
		for (MethodNode mth : cls.getMethods()) {
			CCTool.set("visit@EnumVisitor", 2);
			MethodInfo mi = mth.getMethodInfo();
			if (mi.isClassInit()) {
				CCTool.set("visit@EnumVisitor", 3);
				staticMethod = mth;
				break;
			}
			else {
				CCTool.set("visit@EnumVisitor", 4);
			}
		}
		if (staticMethod == null) {
			CCTool.set("visit@EnumVisitor", 5);
			ErrorsCounter.classWarn(cls, "Enum class init method not found");
			return true;
		}
		else {
			CCTool.set("visit@EnumVisitor", 6);
		}

		ArgType clsType = cls.getClassInfo().getType();
		String enumConstructor = "<init>(Ljava/lang/String;I)V";
		// TODO: detect these methods by analyzing method instructions
		String valuesOfMethod = "valueOf(Ljava/lang/String;)" + TypeGen.signature(clsType);
		String valuesMethod = "values()" + TypeGen.signature(ArgType.array(clsType));

		// collect enum fields, remove synthetic
		List<FieldNode> enumFields = new ArrayList<>();
		for (FieldNode f : cls.getFields()) {
			CCTool.set("visit@EnumVisitor", 7);
			if (f.getAccessFlags().isEnum()) {
				CCTool.set("visit@EnumVisitor", 8);
				enumFields.add(f);
				f.add(AFlag.DONT_GENERATE);
			} else if (f.getAccessFlags().isSynthetic()) {
				CCTool.set("visit@EnumVisitor", 9);
				f.add(AFlag.DONT_GENERATE);
			}
			else {
				CCTool.set("visit@EnumVisitor", 10);
			}
		}

		// remove synthetic methods
		for (MethodNode mth : cls.getMethods()) {
			CCTool.set("visit@EnumVisitor", 11);
			MethodInfo mi = mth.getMethodInfo();
			if (mi.isClassInit()) {
				CCTool.set("visit@EnumVisitor", 12);
				continue;
			}
			else {
				CCTool.set("visit@EnumVisitor", 13);
			}
			String shortId = mi.getShortId();
			boolean isSynthetic = mth.getAccessFlags().isSynthetic();
			if (mi.isConstructor() && !isSynthetic) {
				CCTool.set("visit@EnumVisitor", 14);
				if (shortId.equals(enumConstructor)) {
					CCTool.set("visit@EnumVisitor", 15);
					mth.add(AFlag.DONT_GENERATE);
				}
				else {
					CCTool.set("visit@EnumVisitor", 16);
				}
			} else if (isSynthetic
					|| shortId.equals(valuesMethod)
					|| shortId.equals(valuesOfMethod)) {
				CCTool.set("visit@EnumVisitor", 17);
				mth.add(AFlag.DONT_GENERATE);
			}
			else {
				CCTool.set("visit@EnumVisitor", 18);
			}
		}

		EnumClassAttr attr = new EnumClassAttr(enumFields.size());
		cls.addAttr(attr);

		attr.setStaticMethod(staticMethod);
		ClassInfo classInfo = cls.getClassInfo();

		// move enum specific instruction from static method to separate list
		BlockNode staticBlock = staticMethod.getBasicBlocks().get(0);
		List<InsnNode> enumPutInsns = new ArrayList<>();
		List<InsnNode> list = staticBlock.getInstructions();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			CCTool.set("visit@EnumVisitor", 19);
			InsnNode insn = list.get(i);
			if (insn.getType() != InsnType.SPUT) {
				CCTool.set("visit@EnumVisitor", 20);
				continue;
			}
			else {
				CCTool.set("visit@EnumVisitor", 21);
			}
			FieldInfo f = (FieldInfo) ((IndexInsnNode) insn).getIndex();
			if (!f.getDeclClass().equals(classInfo)) {
				CCTool.set("visit@EnumVisitor", 22);
				continue;
			}
			else {
				CCTool.set("visit@EnumVisitor", 23);
			}
			FieldNode fieldNode = cls.searchField(f);
			if (fieldNode != null && isEnumArrayField(classInfo, fieldNode)) {
				CCTool.set("visit@EnumVisitor", 24);
				if (i == size - 1) {
					CCTool.set("visit@EnumVisitor", 25);
					staticMethod.add(AFlag.DONT_GENERATE);
				} else {
					CCTool.set("visit@EnumVisitor", 26);
					list.subList(0, i + 1).clear();
				}
				break;
			} else {
				CCTool.set("visit@EnumVisitor", 27);
				enumPutInsns.add(insn);
			}
		}

		for (InsnNode putInsn : enumPutInsns) {
			CCTool.set("visit@EnumVisitor", 28);
			ConstructorInsn co = getConstructorInsn(putInsn);
			if (co == null || co.getArgsCount() < 2) {
				CCTool.set("visit@EnumVisitor", 29);
				continue;
			}
			else {
				CCTool.set("visit@EnumVisitor", 30);
			}
			ClassInfo clsInfo = co.getClassType();
			ClassNode constrCls = cls.dex().resolveClass(clsInfo);
			if (constrCls == null) {
				CCTool.set("visit@EnumVisitor", 31);
				continue;
			}
			else {
				CCTool.set("visit@EnumVisitor", 32);
			}
			if (!clsInfo.equals(classInfo) && !constrCls.getAccessFlags().isEnum()) {
				CCTool.set("visit@EnumVisitor", 33);
				continue;
			}
			else {
				CCTool.set("visit@EnumVisitor", 34);
			}
			FieldInfo fieldInfo = (FieldInfo) ((IndexInsnNode) putInsn).getIndex();
			String name = getConstString(cls.dex(), co.getArg(0));
			if (name != null
					&& !fieldInfo.getAlias().equals(name)
					&& NameMapper.isValidIdentifier(name)) {
				CCTool.set("visit@EnumVisitor", 35);
				// LOG.debug("Rename enum field: '{}' to '{}' in {}", fieldInfo.getName(), name, cls);
				fieldInfo.setAlias(name);
			}
			else {
				CCTool.set("visit@EnumVisitor", 36);
			}

			EnumField field = new EnumField(fieldInfo, co, 2);
			attr.getFields().add(field);

			if (!co.getClassType().equals(classInfo)) {
				CCTool.set("visit@EnumVisitor", 37);
				// enum contains additional methods
				for (ClassNode innerCls : cls.getInnerClasses()) {
					CCTool.set("visit@EnumVisitor", 38);
					processEnumInnerCls(co, field, innerCls);
				}
			}
			else {
				CCTool.set("visit@EnumVisitor", 39);
			}
		}
		return false;
	}

	private static void processEnumInnerCls(ConstructorInsn co, EnumField field, ClassNode innerCls) {
		if (!innerCls.getClassInfo().equals(co.getClassType())) {
			return;
		}
		// remove constructor, because it is anonymous class
		for (MethodNode innerMth : innerCls.getMethods()) {
			if (innerMth.getAccessFlags().isConstructor()) {
				innerMth.add(AFlag.DONT_GENERATE);
			}
		}
		field.setCls(innerCls);
		innerCls.add(AFlag.DONT_GENERATE);
	}

	private boolean isEnumArrayField(ClassInfo classInfo, FieldNode fieldNode) {
		if (fieldNode.getAccessFlags().isSynthetic()) {
			ArgType fType = fieldNode.getType();
			return fType.isArray() && fType.getArrayRootElement().equals(classInfo.getType());
		}
		return false;
	}

	private ConstructorInsn getConstructorInsn(InsnNode putInsn) {
		if (putInsn.getArgsCount() != 1) {
			return null;
		}
		InsnArg arg = putInsn.getArg(0);
		if (arg.isInsnWrap()) {
			return castConstructorInsn(((InsnWrapArg) arg).getWrapInsn());
		}
		if (arg.isRegister()) {
			return castConstructorInsn(((RegisterArg) arg).getAssignInsn());
		}
		return null;
	}

	@Nullable
	private ConstructorInsn castConstructorInsn(InsnNode coCandidate) {
		if (coCandidate != null && coCandidate.getType() == InsnType.CONSTRUCTOR) {
			return (ConstructorInsn) coCandidate;
		}
		return null;
	}

	private String getConstString(DexNode dex, InsnArg arg) {
		if (arg.isInsnWrap()) {
			InsnNode constInsn = ((InsnWrapArg) arg).getWrapInsn();
			Object constValue = InsnUtils.getConstValueByInsn(dex, constInsn);
			if (constValue instanceof String) {
				return (String) constValue;
			}
		}
		return null;
	}
}
