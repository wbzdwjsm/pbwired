package com.purpblue.pbwired.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Set;

/**
 * Main annotation processor.
 *
 * @author Purpblue
 */
@SupportedAnnotationTypes("*")
public class PbMainProcessor extends AbstractProcessor {

    private JavacTrees javacTrees;
    private TreeMaker treeMaker;
    private Names names;
    private Messager messager;
    private Utils utils;

    private PbwiredProcessor pbwiredProcessor;
    private FinalInjectProcessor finalInjectProcessor;
    private ConfigurableAnnotationProcessor configurableAnnotationProcessor;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        messager = processingEnv.getMessager();
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.javacTrees = JavacTrees.instance(processingEnv);
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        utils = new Utils();
        pbwiredProcessor = new PbwiredProcessor(messager, javacTrees, names, treeMaker, this);
        finalInjectProcessor = new FinalInjectProcessor(messager, javacTrees, names, treeMaker, this);
        configurableAnnotationProcessor = new ConfigurableAnnotationProcessor(messager, javacTrees, names, treeMaker, this);
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        pbwiredProcessor.processPbwiredAndPbvalue(roundEnv);
        finalInjectProcessor.processFinalInjectAndConstantClassAnnotation(roundEnv);
        configurableAnnotationProcessor.simpleProcessConfigurableAnnotation(roundEnv);
        return true;
    }

    // ----------------------Utils------------------------------
    JCTree.JCExpression access(String path) {
        return utils.access(path);
    }

    JCTree.JCExpression access(String path, boolean firstPrimitive) {
        return utils.access(path, firstPrimitive);
    }

    void print(Object o) {
        utils.print(o);
    }

    void printWarning(Object o) {
        utils.print(o);
    }

    TypeTag getAppropriateTypeTag(String primitiveName) {
        return utils.getAppropriateTypeTag(primitiveName);
    }

    class Utils {
        private void printWarning(Object o) {
            messager.printMessage(Diagnostic.Kind.WARNING, String.valueOf(o));
        }

        private void print(Object o) {
            messager.printMessage(Diagnostic.Kind.NOTE, String.valueOf(o));
        }

        private JCTree.JCExpression access(String path) {
            return access(path, false);
        }

        private JCTree.JCExpression access(String path, boolean firstPrimitive) {
            String[] paths = path.split("\\.");
            JCTree.JCExpression expression;
            if (!firstPrimitive) {
                expression = treeMaker.Ident(names.fromString(paths[0]));
            } else {
                expression = treeMaker.TypeIdent(getAppropriateTypeTag(paths[0]));
            }
            for (int i = 1; i < paths.length; i++) {
                expression = treeMaker.Select(expression, names.fromString(paths[i]));
            }
            return expression;
        }

        private TypeTag getAppropriateTypeTag(String primitiveName) {
            return Arrays.stream(TypeTag.values()).filter(t -> t.toString().toLowerCase().equals(primitiveName)).findFirst().get();
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
