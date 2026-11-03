package ham.citybuildersim;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

/**
 * Scratch (not in the tree): a java agent that times, inclusively, the methods named in its argument, "Class.method,Class.method"
 * (every overload; Class is the simple name in ham.citybuildersim, a nested class as Outer$Inner), through HhTimer.
 * Needs --add-exports java.base/jdk.internal.org.objectweb.asm=ALL-UNNAMED and .commons at run time.
 */
public final class HhAgent {

    public static void premain(String args, Instrumentation inst) {
        Map<String, Map<String, Integer>> wanted = new HashMap<>();
        for (String spec : args.split(",")) {
            spec = spec.trim();
            if (spec.isEmpty()) continue;
            int dot = spec.lastIndexOf('.');
            String cls = "ham/citybuildersim/" + spec.substring(0, dot), method = spec.substring(dot + 1);
            wanted.computeIfAbsent(cls, k -> new HashMap<>()).put(method, HhTimer.register(spec));
        }
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> redefined, ProtectionDomain pd, byte[] bytes) {
                Map<String, Integer> methods = className == null ? null : wanted.get(className);
                if (methods == null) return null;
                try {
                    ClassReader cr = new ClassReader(bytes);
                    ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
                    cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {
                        @Override
                        public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                            MethodVisitor mv = super.visitMethod(access, name, desc, sig, ex);
                            Integer id = methods.get(name);
                            if (id == null || (access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) != 0) return mv;
                            final int ident = id;
                            return new AdviceAdapter(Opcodes.ASM9, mv, access, name, desc) {
                                @Override protected void onMethodEnter() {
                                    push(ident);
                                    visitMethodInsn(Opcodes.INVOKESTATIC, "ham/citybuildersim/HhTimer", "enter", "(I)V", false);
                                }
                                @Override protected void onMethodExit(int opcode) {
                                    push(ident);
                                    visitMethodInsn(Opcodes.INVOKESTATIC, "ham/citybuildersim/HhTimer", "exit", "(I)V", false);
                                }
                            };
                        }
                    }, ClassReader.EXPAND_FRAMES);
                    return cw.toByteArray();
                } catch (Throwable t) {
                    System.err.println("HhAgent: could not instrument " + className + ": " + t);
                    return null;
                }
            }
        });
    }
}
