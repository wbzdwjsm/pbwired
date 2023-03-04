package com.purpblue.pbwired.processor;

import sun.misc.Unsafe;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

@SupportedAnnotationTypes("*")
public class FakeProcessor extends AbstractProcessor {
    private static final String[] ALL_PKGS = {
        "com.sun.tools.javac.code",
        "com.sun.tools.javac.comp",
        "com.sun.tools.javac.file",
        "com.sun.tools.javac.main",
        "com.sun.tools.javac.model",
        "com.sun.tools.javac.parser",
        "com.sun.tools.javac.processing",
        "com.sun.tools.javac.tree",
        "com.sun.tools.javac.util",
        "com.sun.tools.javac.jvm",
        "com.sun.tools.javac.api"
    };
    private static final Unsafe UNSAFE;
    private static final long OVERRIDE_OFFSET;
    static {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            UNSAFE = (Unsafe) unsafeField.get(null);
            OVERRIDE_OFFSET = UNSAFE.objectFieldOffset(Fake.class.getDeclaredField("override"));
            ModuleLayer moduleLayer = ModuleLayer.boot();
            Module jdkCompilerModule = moduleLayer.findModule("jdk.compiler").get();
            Method openMethod = Module.class.getDeclaredMethod("implAddOpens", String.class);
            UNSAFE.putBoolean(openMethod, OVERRIDE_OFFSET, true);
            for (String pkg : ALL_PKGS) {
                openMethod.invoke(jdkCompilerModule, pkg);
            }
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            throw new Error(e);
        }
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private static class Fake {
        private boolean override;
    }
}
