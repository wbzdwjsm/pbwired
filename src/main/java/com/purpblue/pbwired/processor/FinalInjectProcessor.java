package com.purpblue.pbwired.processor;

import com.purpblue.pbwired.annotation.ConstantClass;
import com.purpblue.pbwired.annotation.DefaultValue;
import com.purpblue.pbwired.annotation.FinalInject;
import com.purpblue.pbwired.util.Constants;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author Purpblue
 */
class FinalInjectProcessor {

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

    public void processFinalInjectAndConstantClassAnnotation(RoundEnvironment roundEnv) {
        // Process @FinalInject
        processFinalInject(roundEnv);

        // Process @ConstantClass
        processConstantClass(roundEnv);
    }


    private void processConstantClass(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ConstantClass.class);
        elements.forEach(e -> {
            JCTree jcTree = javacTrees.getTree(e);
            jcTree.accept(new JCTree.Visitor() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    ConstantClass constantClassAnnotation = e.getAnnotation(ConstantClass.class);
                    mainProcessor.print("class:" + jcClassDecl.getSimpleName());
                    String[] modifiersInclude = constantClassAnnotation.modifiersInclude();
                    Set<String> modifiersIncludeSet = new HashSet<>(Arrays.asList(modifiersInclude));
                    String[] fieldsExclude = constantClassAnnotation.fieldsExclude();
                    Set<String> fieldsExcludeSet = new HashSet<>(Arrays.asList(fieldsExclude));
                    String prefix = constantClassAnnotation.prefix();
                    jcClassDecl.defs
                            .stream()
                            .filter(d -> Tree.Kind.VARIABLE.equals(d.getKind()))
                            .map(d -> (JCTree.JCVariableDecl) d)
                            .filter(d -> !fieldsExcludeSet.contains(d.getName().toString()) && d.getModifiers().getFlags().stream().map(Modifier::toString).collect(Collectors.toSet()).containsAll(modifiersIncludeSet))
                            .forEach(v -> {
                                // @FinalInject exists simultaneously
                                if (checkFinalInject(v)) {
                                    return;
                                }
                                String varName = decideValueName(prefix, v);
                                initConstant(v, varName);
                            });
                }
            });
        });
    }

    private boolean checkFinalInject(JCTree.JCVariableDecl v) {
        return v.sym.getAnnotation(FinalInject.class) != null;
    }

    private String getVarClass(JCTree.JCVariableDecl v) {
        JCTree.JCExpression varType = (JCTree.JCExpression) v.getType();
        Tree.Kind vTypeKind = varType.getKind();
        String varClassString = varType.toString();
        if (vTypeKind.equals(Tree.Kind.PARAMETERIZED_TYPE)) {
            JCTree.JCTypeApply jcTypeApply = (JCTree.JCTypeApply) varType;
            varClassString = jcTypeApply.getType().toString();
        }
        return varClassString;
    }

    private StringJoiner prepareAppropriateStringJoiner(JCTree.JCVariableDecl v) {
        StringJoiner joiner;
        if (Constants.MAP_CAPITALIZED.equals(getVarClass(v))) {
            joiner = new StringJoiner("", "#{${", "}}");
        } else {
            joiner = new StringJoiner("", "${", "}");
        }
        return joiner;
    }

    private String decideValueName(String prefix, JCTree.JCVariableDecl v) {
        String varName = v.getName().toString();
        String realPrefix = "".equals(prefix) ? "" : prefix + ".";
        DefaultValue defaultValueAnnotation = v.sym.getAnnotation(DefaultValue.class);
        String defaultValueString = "";
        if (defaultValueAnnotation != null) {
            defaultValueString = ":" + defaultValueAnnotation.value();
        }
        StringJoiner joiner = prepareAppropriateStringJoiner(v);
        joiner.add(realPrefix).add(varName).add(defaultValueString);
        return joiner.toString();
    }

    private void processFinalInject(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(FinalInject.class);
        elements.forEach(e -> {
            JCTree jcTree = javacTrees.getTree(e);
            FinalInject inject = e.getAnnotation(FinalInject.class);
            jcTree.accept(new JCTree.Visitor() {
                @Override
                public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
                    String value = inject.value();
                    if ("".equals(value)) {
                        value = inject.key();
                    }
                    if ("".equals(value)) {
                        // FullName of field, by default
                        Element owner = jcVariableDecl.sym.owner;
                        JCTree.JCClassDecl ownerClass = (JCTree.JCClassDecl) javacTrees.getTree(owner);
                        Name ownerClassFullName = ownerClass.sym.fullname;
                        if (ownerClassFullName != null) {
                            StringJoiner joiner = prepareAppropriateStringJoiner(jcVariableDecl);
                            joiner.add(ownerClassFullName).add(".").add(jcVariableDecl.getName().toString());
                            value = joiner.toString();
                        }
                    }
                    if (!"".equals(value)) {
                        initConstant(jcVariableDecl, value);
                    }
                }
            });
        });
    }

    private void initConstant(JCTree.JCVariableDecl jcVariableDecl, String key) {
        JCTree.JCFieldAccess methodForValue = (JCTree.JCFieldAccess) mainProcessor.access(Constants.VALUE_UTIL_BUILD_METHOD);

        Element parentElement = jcVariableDecl.sym.owner;
        JCTree.JCClassDecl parentClass = (JCTree.JCClassDecl) javacTrees.getTree(parentElement);
        JCTree.JCExpression parentClassName = mainProcessor.access(parentClass.getSimpleName().toString() + Constants.DOT_CLASS_STRING);

        // Add @Value
        JCTree.JCExpression valueExpression = mainProcessor.access(Constants.VALUE_PATH);
        JCTree.JCAnnotation valueAnnotation = treeMaker.Annotation(valueExpression, List.of(
                treeMaker.Assign(mainProcessor.access(Constants.STRING_VALUE), treeMaker.Literal(key))
        ));
        List<JCTree.JCAnnotation> annotations = jcVariableDecl.getModifiers().getAnnotations();
        annotations = annotations.prepend(valueAnnotation);
        jcVariableDecl.getModifiers().annotations = annotations;

        // Initialize method
        jcVariableDecl.init = treeMaker.Apply(
                null,
                methodForValue,
                List.of(parentClassName, treeMaker.Literal(jcVariableDecl.name.toString()))
        );
    }

}

