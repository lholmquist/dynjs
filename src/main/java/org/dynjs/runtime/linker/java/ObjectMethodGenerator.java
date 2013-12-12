package org.dynjs.runtime.linker.java;

import static me.qmx.jitescript.util.CodegenUtils.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JiteClass;

import org.objectweb.asm.tree.LabelNode;
import org.projectodd.rephract.mop.java.CoercionMatrix;

public class ObjectMethodGenerator extends MethodGenerator {
    
    public void defineMethod(final Method method, final JiteClass jiteClass, final Class<?> superClass) {

        if (method.getName().equals("equals") || method.getName().equals("hashCode") || method.getName().equals("toString")) {
            return;
        }
        final Class<?>[] params = method.getParameterTypes();
        final Class<?>[] signature = new Class<?>[params.length + 1];
        final Class<?> returnType = method.getReturnType();

        for (int i = 1; i < params.length + 1; ++i) {
            signature[i] = params[i - 1];
        }

        signature[0] = returnType;

        LabelNode noImpl = new LabelNode();
        LabelNode complete = new LabelNode();
        CodeBlock codeBlock = new CodeBlock();
        callJavascriptImplementation(method, jiteClass, codeBlock, noImpl);
        // result
        // Coerce our JavaScript values to the appropriate return type
        if (returnType == int.class || returnType == short.class) {
            codeBlock.invokestatic(p(CoercionMatrix.class), "numberToInteger", sig(Integer.class, Number.class));
            codeBlock.invokevirtual(p(Integer.class), "intValue", sig(int.class));
        } else if (returnType == long.class) {
            codeBlock.invokestatic(p(CoercionMatrix.class), "numberToLong", sig(Long.class, Number.class));
            codeBlock.invokevirtual(p(Long.class), "longValue", sig(long.class));
        } else if (returnType == float.class) {
            codeBlock.invokestatic(p(CoercionMatrix.class), "numberToFloat", sig(Float.class, Number.class));
            codeBlock.invokevirtual(p(Float.class), "floatValue", sig(float.class));
        } else if (returnType == double.class) {
            codeBlock.invokestatic(p(CoercionMatrix.class), "numberToDouble", sig(Double.class, Number.class));
            codeBlock.invokevirtual(p(Double.class), "doubleValue", sig(double.class));
        } else if (returnType == Integer.class) {
            codeBlock.invokestatic(p(CoercionMatrix.class), "numberToInteger", sig(Integer.class, Number.class));
        } else if (returnType == Long.class) {
            codeBlock.invokestatic(p(CoercionMatrix.class), "numberToLong", sig(Long.class, Number.class));
        } else if (returnType == Short.class) {
            codeBlock.invokestatic(p(CoercionMatrix.class), "numberToShort", sig(Short.class, Number.class));
        } else if (returnType == Float.class) {
            codeBlock.invokestatic(p(CoercionMatrix.class), "numberToFloat", sig(Float.class, Number.class));
        } else if (returnType == Double.class) {
            codeBlock.invokestatic(p(CoercionMatrix.class), "numberToDouble", sig(Double.class, Number.class));
//        } else if (returnType == boolean.class) {
//            // ensure we return a boolean whether we have a Boolean or boolean here
//            codeBlock.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
//            codeBlock.invokevirtual(p(Boolean.class), "booleanValue", sig(boolean.class));
        }
        codeBlock.go_to(complete);

        codeBlock.label(noImpl);
        // empty
        callSuperImplementation(method, superClass, codeBlock);
        // result

        codeBlock.label(complete);
        // result
        if (returnType == Void.TYPE) {
            codeBlock.voidreturn();
        } else if (returnType == int.class || returnType == boolean.class || returnType == short.class) {
            codeBlock.ireturn();
        } else if (returnType == long.class) {
            codeBlock.lreturn();
        } else if (returnType == float.class) {
            codeBlock.freturn();
        } else if (returnType == double.class) {
            codeBlock.dreturn();
        } else {
            codeBlock.areturn();
        }
        jiteClass.defineMethod(method.getName(), method.getModifiers() & ~Modifier.ABSTRACT, sig(signature), codeBlock);
    }

    @Override
    protected void handleDefaultReturnValue(CodeBlock block) {
        block.aconst_null();
    }


}
