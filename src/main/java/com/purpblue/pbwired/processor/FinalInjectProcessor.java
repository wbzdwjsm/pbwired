package com.purpblue.pbwired.processor;

import com.purpblue.pbwired.annotation.FinalInject;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.util.Set;

/**
 * @author Purpblue
 */
class FinalInjectProcessor {

    private static final String VALUE_UTIL_BUILD_METHOD = "com.purpblue.pbwired.util.ValueResolver.getProperty";
    private static final String DOT_CLASS_STRING = ".class";

    private final JavacTrees javacTrees;
    private final TreeMaker treeMaker;
    private final Names names;
    private final Messager messager;
    private final PbMainProcessor mainProcessor;

    FinalInjectProcessor(Messager messager, JavacTrees javacTrees, Names names, TreeMaker treeMaker, PbMainProcessor mainProcessor) {
        this.messager = messager;
        this.names = names;
        this.treeMaker = treeMaker;
        this.javacTrees = javacTrees;
        this.mainProcessor = mainProcessor;
    }

    public void processFinalInjectAnnotation(RoundEnvironment roundEnv) {
        // Process @FinalInject
        processFinalInject(roundEnv);
    }

    private void processFinalInject(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(FinalInject.class);
        elements.forEach(e -> {
            JCTree jcTree = javacTrees.getTree(e);
            FinalInject inject = e.getAnnotation(FinalInject.class);
            jcTree.accept(new JCTree.Visitor() {
                @Override
                public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
                    JCTree.JCFieldAccess methodForValue = (JCTree.JCFieldAccess) mainProcessor.access(VALUE_UTIL_BUILD_METHOD);
                    boolean isPrimitive = Tree.Kind.PRIMITIVE_TYPE.equals(jcVariableDecl.vartype.getKind());
                    jcVariableDecl.init = treeMaker.Apply(
                            null,
                            methodForValue,
                            List.of(treeMaker.Literal(inject.key()),
                                    mainProcessor.access(jcVariableDecl.vartype.toString() + DOT_CLASS_STRING, isPrimitive),
                                    treeMaker.Literal(jcVariableDecl.name.toString())));
                }
            });
        });
    }

}

