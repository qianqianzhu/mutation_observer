package org.qzhu.mutationObserver.callgraph;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;


/**
 * @author Qianqian Zhu
 * Class copied with modifications from java-callgraph: https://github.com/gousiosg/java-callgraph
 */
public class MethodVisitor extends EmptyVisitor {

    JavaClass visitedClass;
    private MethodGen mg;
    private ConstantPoolGen cp;
    private String format;

    public MethodVisitor(MethodGen m, JavaClass jc) {
        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();
        format = "M:" + visitedClass.getClassName() + ":" + mg.getName() +"(" + argumentList(mg.getArgumentTypes()) + ")"
            + " " + "(%s)%s:%s(%s)";
    }

    private String argumentList(Type[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(arguments[i].toString());
        }
        return sb.toString();
    }

    public void start() {
        if (mg.isAbstract() || mg.isNative())
            return;
        for (InstructionHandle ih = mg.getInstructionList().getStart(); 
                ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            
            if (!visitInstruction(i))
                i.accept(this);
        }
    }

    private boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();
        return ((InstructionConst.getInstruction(opcode) != null)
                && !(i instanceof ConstantPushInstruction) 
                && !(i instanceof ReturnInstruction));
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
        System.out.println(String.format(format,"M",i.getReferenceType(cp),i.getMethodName(cp),argumentList(i.getArgumentTypes(cp))));
    }

    @Override
    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
        System.out.println(String.format(format,"I",i.getReferenceType(cp),i.getMethodName(cp),argumentList(i.getArgumentTypes(cp))));
    }

    @Override
    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
        System.out.println(String.format(format,"O",i.getReferenceType(cp),i.getMethodName(cp),argumentList(i.getArgumentTypes(cp))));
    }

    @Override
    public void visitINVOKESTATIC(INVOKESTATIC i) {
        System.out.println(String.format(format,"S",i.getReferenceType(cp),i.getMethodName(cp),argumentList(i.getArgumentTypes(cp))));
    }

    @Override
    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
        System.out.println(String.format(format,"D",i.getType(cp),i.getMethodName(cp),
                argumentList(i.getArgumentTypes(cp))));
    }
}
