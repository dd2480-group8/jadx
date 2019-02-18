package jadx.core.dex.nodes.parser;

import jadx.api.CCTool;
import java.util.List;

import com.android.dex.Dex.Section;

import jadx.core.deobf.NameMapper;
import jadx.core.dex.attributes.nodes.SourceFileAttr;
import jadx.core.dex.instructions.args.InsnArg;
import jadx.core.dex.instructions.args.RegisterArg;
import jadx.core.dex.instructions.args.SSAVar;
import jadx.core.dex.nodes.DexNode;
import jadx.core.dex.nodes.InsnNode;
import jadx.core.dex.nodes.MethodNode;
import jadx.core.utils.exceptions.DecodeException;

public class DebugInfoParser {

	private static final int DBG_END_SEQUENCE = 0x00;
	private static final int DBG_ADVANCE_PC = 0x01;
	private static final int DBG_ADVANCE_LINE = 0x02;
	private static final int DBG_START_LOCAL = 0x03;
	private static final int DBG_START_LOCAL_EXTENDED = 0x04;
	private static final int DBG_END_LOCAL = 0x05;
	private static final int DBG_RESTART_LOCAL = 0x06;
	private static final int DBG_SET_PROLOGUE_END = 0x07;
	private static final int DBG_SET_EPILOGUE_BEGIN = 0x08;
	private static final int DBG_SET_FILE = 0x09;

	// the smallest special opcode
	private static final int DBG_FIRST_SPECIAL = 0x0a;
	// the smallest line number increment
	private static final int DBG_LINE_BASE = -4;
	// the number of line increments represented
	private static final int DBG_LINE_RANGE = 15;

	private final MethodNode mth;
	private final Section section;
	private final DexNode dex;

	private final LocalVar[] locals;
	private final InsnArg[] activeRegisters;
	private final InsnNode[] insnByOffset;

	public DebugInfoParser(MethodNode mth, int debugOffset, InsnNode[] insnByOffset) {
		this.mth = mth;
		this.dex = mth.dex();
		this.section = dex.openSection(debugOffset);

		int regsCount = mth.getRegsCount();
		this.locals = new LocalVar[regsCount];
		this.activeRegisters = new InsnArg[regsCount];
		this.insnByOffset = insnByOffset;
	}

	public void process() throws DecodeException {
		int addr = 0;
		int line = section.readUleb128();

		int paramsCount = section.readUleb128();
		List<RegisterArg> mthArgs = mth.getArguments(false);
		for (int i = 0; i < paramsCount; i++) {
			CCTool.set("process@DebugInfoParser", 0);
			int id = section.readUleb128() - 1;
			if (id != DexNode.NO_INDEX) {
				CCTool.set("process@DebugInfoParser", 1);
				String name = dex.getString(id);
				if (i < mthArgs.size()) {
					CCTool.set("process@DebugInfoParser", 2);
					mthArgs.get(i).setName(name);
				}else{
					CCTool.set("process@DebugInfoParser", 3);
				}
			}else{
				CCTool.set("process@DebugInfoParser", 4);
			}
		}

		for (RegisterArg arg : mthArgs) {
			CCTool.set("process@DebugInfoParser", 5);
			int rn = arg.getRegNum();
			locals[rn] = new LocalVar(arg);
			activeRegisters[rn] = arg;
		}

		// process '0' instruction
		addrChange(-1, 1, line);
		setLine(addr, line);

		boolean varsInfoFound = false;

		int c = section.readByte() & 0xFF;
		while (c != DBG_END_SEQUENCE) {
			CCTool.set("process@DebugInfoParser", 6);
			switch (c) {
				case DBG_ADVANCE_PC: {
					CCTool.set("process@DebugInfoParser", 7);
					int addrInc = section.readUleb128();
					addr = addrChange(addr, addrInc, line);
					setLine(addr, line);
					break;
				}
				case DBG_ADVANCE_LINE: {
					CCTool.set("process@DebugInfoParser", 8);
					line += section.readSleb128();
					break;
				}

				case DBG_START_LOCAL: {
					CCTool.set("process@DebugInfoParser", 9);
					int regNum = section.readUleb128();
					int nameId = section.readUleb128() - 1;
					int type = section.readUleb128() - 1;
					LocalVar var = new LocalVar(dex, regNum, nameId, type, DexNode.NO_INDEX);
					startVar(var, addr, line);
					varsInfoFound = true;
					break;
				}
				case DBG_START_LOCAL_EXTENDED: {
					CCTool.set("process@DebugInfoParser", 10);
					int regNum = section.readUleb128();
					int nameId = section.readUleb128() - 1;
					int type = section.readUleb128() - 1;
					int sign = section.readUleb128() - 1;
					LocalVar var = new LocalVar(dex, regNum, nameId, type, sign);
					startVar(var, addr, line);
					varsInfoFound = true;
					break;
				}
				case DBG_RESTART_LOCAL: {
					CCTool.set("process@DebugInfoParser", 11);
					int regNum = section.readUleb128();
					LocalVar var = locals[regNum];
					if (var != null) {
						CCTool.set("process@DebugInfoParser", 12);
						if (var.end(addr, line)) {
							CCTool.set("process@DebugInfoParser", 13);
							setVar(var);
						}else{
							CCTool.set("process@DebugInfoParser", 14);
						}
						var.start(addr, line);
					}else{
						CCTool.set("process@DebugInfoParser", 15);
					}
					varsInfoFound = true;
					break;
				}
				case DBG_END_LOCAL: {
					CCTool.set("process@DebugInfoParser", 16);
					int regNum = section.readUleb128();
					LocalVar var = locals[regNum];
					if (var != null) {
						CCTool.set("process@DebugInfoParser", 17);
						var.end(addr, line);
						setVar(var);
					}else{
						CCTool.set("process@DebugInfoParser", 18);
					}
					varsInfoFound = true;
					break;
				}

				case DBG_SET_PROLOGUE_END:
					CCTool.set("process@DebugInfoParser", 19);
				case DBG_SET_EPILOGUE_BEGIN:
					CCTool.set("process@DebugInfoParser", 20);
					// do nothing
					break;

				case DBG_SET_FILE: {
					CCTool.set("process@DebugInfoParser", 21);
					int idx = section.readUleb128() - 1;
					if (idx != DexNode.NO_INDEX) {
						CCTool.set("process@DebugInfoParser", 22);
						String sourceFile = dex.getString(idx);
						mth.addAttr(new SourceFileAttr(sourceFile));
					}else{
						CCTool.set("process@DebugInfoParser", 23);
					}
					break;
				}

				default: {
					CCTool.set("process@DebugInfoParser", 24);
					if (c >= DBG_FIRST_SPECIAL) {
						CCTool.set("process@DebugInfoParser", 25);
						int adjustedOpcode = c - DBG_FIRST_SPECIAL;
						int addrInc = adjustedOpcode / DBG_LINE_RANGE;
						addr = addrChange(addr, addrInc, line);
						line += DBG_LINE_BASE + adjustedOpcode % DBG_LINE_RANGE;
						setLine(addr, line);
					} else {
						CCTool.set("process@DebugInfoParser", 26);
						throw new DecodeException("Unknown debug insn code: " + c);
					}
					break;
				}
			}
			c = section.readByte() & 0xFF;
		}

		if (varsInfoFound) {
			CCTool.set("process@DebugInfoParser", 27);
			for (LocalVar var : locals) {
				CCTool.set("process@DebugInfoParser", 28);
				if (var != null && !var.isEnd()) {
					CCTool.set("process@DebugInfoParser", 29);
					var.end(mth.getCodeSize() - 1, line);
					setVar(var);
				}else{
					CCTool.set("process@DebugInfoParser", 30);
				}
			}
		}else{
			CCTool.set("process@DebugInfoParser", 31);
		}
		setSourceLines(addr, insnByOffset.length, line);
	}

	private int addrChange(int addr, int addrInc, int line) {
		int newAddr = addr + addrInc;
		int maxAddr = insnByOffset.length - 1;
		newAddr = Math.min(newAddr, maxAddr);
		for (int i = addr + 1; i <= newAddr; i++) {
			InsnNode insn = insnByOffset[i];
			if (insn == null) {
				continue;
			}
			for (InsnArg arg : insn.getArguments()) {
				if (arg.isRegister()) {
					activeRegisters[((RegisterArg) arg).getRegNum()] = arg;
				}
			}
			RegisterArg res = insn.getResult();
			if (res != null) {
				activeRegisters[res.getRegNum()] = res;
			}
		}
		setSourceLines(addr, newAddr, line);
		return newAddr;
	}

	private void setSourceLines(int start, int end, int line) {
		for (int offset = start + 1; offset < end; offset++) {
			setLine(offset, line);
		}
	}

	private void setLine(int offset, int line) {
		InsnNode insn = insnByOffset[offset];
		if (insn != null) {
			insn.setSourceLine(line);
		}
	}

	private void startVar(LocalVar var, int addr, int line) {
		int regNum = var.getRegNum();
		LocalVar prev = locals[regNum];
		if (prev != null && !prev.isEnd()) {
			prev.end(addr, line);
			setVar(prev);
		}
		InsnArg activeReg = activeRegisters[var.getRegNum()];
		if (activeReg instanceof RegisterArg) {
			SSAVar ssaVar = ((RegisterArg) activeReg).getSVar();
			if (ssaVar != null && ssaVar.getStartAddr() != -1) {
				InsnNode parentInsn = ssaVar.getAssign().getParentInsn();
				if (parentInsn != null && parentInsn.getOffset() >= 0) {
					addr = parentInsn.getOffset();
				}
			}
		}
		var.start(addr, line);
		locals[regNum] = var;
	}

	private void setVar(LocalVar var) {
		int start = var.getStartAddr();
		int end = var.getEndAddr();

		for (int i = start; i <= end; i++) {
			InsnNode insn = insnByOffset[i];
			if (insn != null) {
				fillLocals(insn, var);
			}
		}
		merge(activeRegisters[var.getRegNum()], var);
	}

	private static void fillLocals(InsnNode insn, LocalVar var) {
		merge(insn.getResult(), var);
		for (InsnArg arg : insn.getArguments()) {
			merge(arg, var);
		}
	}

	private static void merge(InsnArg arg, LocalVar var) {
		if (arg == null || !arg.isRegister()) {
			return;
		}
		RegisterArg reg = (RegisterArg) arg;
		if (var.getRegNum() != reg.getRegNum()) {
			return;
		}
		boolean mergeRequired = false;

		SSAVar ssaVar = reg.getSVar();
		if (ssaVar != null) {
			int ssaEnd = ssaVar.getEndAddr();
			int ssaStart = ssaVar.getStartAddr();
			int localStart = var.getStartAddr();
			int localEnd = var.getEndAddr();

			boolean isIntersected = !(localEnd < ssaStart || ssaEnd < localStart);
			if (isIntersected && ssaEnd <= localEnd) {
				mergeRequired = true;
			}
		} else {
			mergeRequired = true;
		}

		if (mergeRequired) {
			applyDebugInfo(reg, var);
		}
	}

	private static void applyDebugInfo(RegisterArg reg, LocalVar var) {
		String varName = var.getName();
		if (NameMapper.isValidIdentifier(varName)) {
			reg.mergeDebugInfo(var.getType(), varName);
		}
	}
}
