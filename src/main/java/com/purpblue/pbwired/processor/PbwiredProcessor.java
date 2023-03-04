/*
 * Copyright(c) 2020 Purpblue. All Rights Reserved
 */
package com.purpblue.pbwired.processor;

import com.purpblue.pbwired.annotation.Pbvalue;
import com.purpblue.pbwired.annotation.Pbwired;
import com.purpblue.pbwired.util.Constants;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.Resource;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Locale.ENGLISH;

/**
 * A processor used for processing the annotations - {@link Pbwired @Pbwired} and {@link Pbvalue @Pbvalue}.
 *
 * @see Pbwired
 * @see Pbvalue
 *
 * @author Purpblue
 */

class PbwiredProcessor {

    private final JavacTrees javacTrees;
    private final TreeMaker treeMaker;
    private final Names names;
    private final Messager messager;
    private final PbMainProcessor mainProcessor;

    PbwiredProcessor(Messager messager, JavacTrees javacTrees, Names names, TreeMaker treeMaker, PbMainProcessor mainProcessor) {
        this.messager = messager;
        this.names = names;
        this.treeMaker = treeMaker;
        this.javacTrees = javacTrees;
        this.mainProcessor = mainProcessor;
    }

    public void processPbwiredAndPbvalue(RoundEnvironment roundEnv) {
        //process @Pbwired
        processPbwired(roundEnv);
        //process @Pbvalue
        processPbvalue(roundEnv);
    }

    private void processPbvalue(RoundEnvironment roundEnv) {
        Set<? extends Element> pbvalueElements = roundEnv.getElementsAnnotatedWith(Pbvalue.class);
        for (Element e : pbvalueElements) {
            JCTree tree = javacTrees.getTree(e);
            //if @Value exists, ignore @Pbvalue
            Value v = e.getAnnotation(Value.class);
            if (v != null) {
                continue;
            }
            Pbvalue pbvalue = e.getAnnotation(Pbvalue.class);
            tree.accept(new TreeTranslator() {
                @Override
                public void visitVarDef(JCTree.JCVariableDecl variableDecl) {
                    super.visitVarDef(variableDecl);
                    Symbol owner = variableDecl.sym.owner;
                    JCTree.JCClassDecl ownClass = (JCTree.JCClassDecl) javacTrees.getTree(owner);
                    JCTree.JCExpression jc0 = treeMaker.Assign(treeMaker.Ident(names.fromString(Constants.STRING_VALUE)), treeMaker.Literal(pbvalue.value()));
                    List<JCTree.JCExpression> params = List.of(jc0);
                    JCTree.JCAnnotation valued = treeMaker.Annotation(mainProcessor.access(Constants.VALUE_PATH), params);
                    List<JCTree.JCAnnotation> annos = List.of(valued);
                    JCTree.JCVariableDecl param = treeMaker.VarDef(
                            treeMaker.Modifiers(Flags.PARAMETER),
                            variableDecl.name,
                            variableDecl.vartype,
                            null
                    );
                    param.pos = ownClass.pos;
                    List<JCTree.JCVariableDecl> paraList = List.of(param);
                    ListBuffer<JCTree.JCStatement> mbody = new ListBuffer<>();
                    mbody.append(treeMaker.Exec(
                            treeMaker.Assign(
                                    treeMaker.Select(treeMaker.Ident(names.fromString(Constants.STRING_THIS)), variableDecl.name),
                                    treeMaker.Ident(variableDecl.name)
                            )
                    ));
                    JCTree.JCBlock methodBody = treeMaker.Block(0, mbody.toList());
                    JCTree.JCMethodDecl setter = treeMaker.MethodDef(
                            treeMaker.Modifiers(Flags.PUBLIC, annos),
                            names.fromString(generateMethodName(variableDecl.name.toString())),
                            treeMaker.Type(new Type.JCVoidType()),
                            List.nil(),
                            paraList,
                            List.nil(),
                            methodBody,
                            null
                    );
                    ownClass.defs = ownClass.defs.append(setter);
                }
            });
        }
    }

    private void processPbwired(RoundEnvironment roundEnv) {
        Set<? extends Element> pbwiredElements = roundEnv.getElementsAnnotatedWith(Pbwired.class);
        //Init:0, has @Autowired-annotated constructor: 1. When 1, it is never changed to 0.
        Map<String, Integer> alreadyInit = new HashMap<>();
        // Need to add new Ctor
        Map<String, Boolean> newCtorMap = new HashMap<>();
        for (Element e : pbwiredElements) {
            JCTree tree = javacTrees.getTree(e);
            //if @Resource/@Autowired exists，ignore @Pbwired
            Resource r = e.getAnnotation(Resource.class);
            if (r != null) {
                continue;
            }
            Autowired a = e.getAnnotation(Autowired.class);
            if (a != null) {
                continue;
            }

            Pbwired p = e.getAnnotation(Pbwired.class);
            tree.accept(new TreeTranslator() {
                @Override
                public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
                    Symbol owner = jcVariableDecl.sym.owner;
                    JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) javacTrees.getTree(owner);
                    alreadyInit.putIfAbsent(classDecl.sym.fullname.toString(), 0);
                    switch (p.wireType()) {
                        case SETTER:
                            JCTree.JCAnnotation jcAnnotation = treeMaker.Annotation(mainProcessor.access(Constants.AUTOWIRED_PATH), List.nil());
                            List<JCTree.JCAnnotation> annotations = List.of(jcAnnotation);
                            List<JCTree.JCTypeParameter> typeParameters = List.nil();
                            JCTree.JCVariableDecl p0 = treeMaker.VarDef(
                                    makeParamModifiers(p),
                                    jcVariableDecl.name,
                                    jcVariableDecl.vartype,
                                    null
                            );
                            p0.pos = classDecl.pos;
                            List<JCTree.JCVariableDecl> parameters = List.of(p0);
                            List<JCTree.JCExpression> throwList = List.nil();
                            ListBuffer<JCTree.JCStatement> body = new ListBuffer<>();
                            body.append(
                                    treeMaker.Exec(treeMaker.Assign(
                                            treeMaker.Select(treeMaker.Ident(names.fromString(Constants.STRING_THIS)), p0.getName()),
                                            treeMaker.Ident(p0.getName())))
                            );
                            JCTree.JCBlock bodyBlock = treeMaker.Block(0, body.toList());

                            JCTree.JCMethodDecl setter0 = treeMaker.MethodDef(
                                    treeMaker.Modifiers(Flags.PUBLIC, annotations),
                                    names.fromString(generateMethodName(jcVariableDecl.name.toString())),
                                    treeMaker.Type(new Type.JCVoidType()),
                                    typeParameters,
                                    parameters,
                                    throwList,
                                    bodyBlock,
                                    null
                            );
                            classDecl.defs = classDecl.defs.append(setter0);
                            break;
                        case CONSTRUCTOR:
                            for (JCTree t : classDecl.defs) {
                                if (t.getKind().equals(Tree.Kind.METHOD)) {
                                    JCTree.JCMethodDecl m = (JCTree.JCMethodDecl) t;
                                    if (Constants.STRING_CTOR.equals(m.getName().toString())) {
                                        // If the constructor with params has no @Autowired, add it as a flag.
                                        if (alreadyInit.get(classDecl.sym.fullname.toString()) == 0 && m.params.size() > 0) {
                                            JCTree.JCAnnotation jcAutowired = treeMaker.Annotation(mainProcessor.access(Constants.AUTOWIRED_PATH), List.nil());
                                            if (!checkContainsAnnotation(m.mods.annotations, jcAutowired)) {
                                                m.mods.annotations = m.mods.annotations.append(jcAutowired);
                                            }
                                            alreadyInit.put(classDecl.sym.fullname.toString(), Constants.AUTOWIRED_CTOR);
                                        }
                                        for (JCTree.JCAnnotation ann : m.getModifiers().annotations) {
                                            //The class has @Autowired-annotated constructor
                                            if (alreadyInit.get(classDecl.sym.fullname.toString()) == Constants.AUTOWIRED_CTOR || Constants.AUTOWIRED_PATH.equals(ann.annotationType.toString())) {
                                                JCTree.JCVariableDecl var = treeMaker.VarDef(
                                                    makeParamModifiers(p),
                                                    jcVariableDecl.name,
                                                    jcVariableDecl.vartype,
                                                    null
                                                );
                                                var.pos = classDecl.pos;
                                                //If jcVariableDecl is not static, make it final
                                                makeFinalIfPossible(jcVariableDecl);
                                                m.params = m.params.append(var);
                                                m.body.stats = m.body.stats.append(
                                                    treeMaker.Exec(treeMaker.Assign(
                                                        treeMaker.Select(treeMaker.Ident(names.fromString(Constants.STRING_THIS)), var.name),
                                                        treeMaker.Ident(var.name)))
                                                );
                                                alreadyInit.put(classDecl.sym.fullname.toString(), Constants.AUTOWIRED_CTOR);
                                                return;
                                            }
                                        }
                                        //No-args ctor
                                        if (alreadyInit.get(classDecl.sym.fullname.toString()) == 0 && m.params.size() == 0) {
                                            JCTree.JCAnnotation jcAutowired = treeMaker.Annotation(mainProcessor.access(Constants.AUTOWIRED_PATH), List.nil());
                                            JCTree.JCVariableDecl varDecl = treeMaker.VarDef(
                                                    makeParamModifiers(p),
                                                    jcVariableDecl.name,
                                                    jcVariableDecl.vartype,
                                                    null
                                            );
                                            varDecl.pos = classDecl.pos;

                                            // Remove compiler-given constructor
                                            if (!Boolean.TRUE.equals(newCtorMap.get(classDecl.sym.fullname.toString())) && checkIfDefaultCtorIsEmpty(m, classDecl)) {
                                                m = newCtor(varDecl);
                                                newCtorMap.put(classDecl.sym.fullname.toString(), true);
                                            }
                                            m.mods.annotations = m.mods.annotations.append(jcAutowired);
                                            m.params = List.of(varDecl);
                                            //If jcVariableDecl is not static, make it final
                                            makeFinalIfPossible(jcVariableDecl);
                                            JCTree.JCExpressionStatement assign =
                                                    treeMaker.Exec(treeMaker.Assign(
                                                            treeMaker.Select(treeMaker.Ident(names.fromString(Constants.STRING_THIS)), jcVariableDecl.name),
                                                            treeMaker.Ident(jcVariableDecl.name)));
                                            m.body.stats = m.body.stats.append(assign);
                                            classDecl.defs = classDecl.defs.append(m);
                                            alreadyInit.put(classDecl.sym.fullname.toString(), Constants.AUTOWIRED_CTOR);
                                            return;
                                        }
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    private boolean checkContainsAnnotation(List<JCTree.JCAnnotation> annotations, JCTree.JCAnnotation annotation) {
        for (JCTree.JCAnnotation anno : annotations) {
            if (("@" + anno.annotationType.type.toString()).equals(annotation.toString())) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkIfDefaultCtorIsEmpty(JCTree.JCMethodDecl ctor, JCTree.JCClassDecl classDecl) {
        List<JCTree.JCStatement> stats = ctor.body.stats;
        if (stats.isEmpty() || (stats.size() == 1 && "super();".equals(stats.get(0).toString()))) {
            // Remove this constructor
            ListBuffer<JCTree> newStats = new ListBuffer<>();
            for (JCTree s : classDecl.defs) {
                if (s != ctor) {
                    newStats.add(s);
                }
            }
            classDecl.defs = newStats.toList();
            return true;
        }
        return false;
    }

    private JCTree.JCMethodDecl newCtor(JCTree.JCVariableDecl varDecl) {
        return treeMaker.MethodDef(
            treeMaker.Modifiers(Flags.PUBLIC),
            names.fromString("<init>"),
            null,
            List.nil(),
            List.of(varDecl),
            List.nil(),
            treeMaker.Block(0, List.nil()),
            null
        );
    }

    private void makeFinalIfPossible(JCTree.JCVariableDecl jcVariableDecl) {
        Set<Modifier> modifiersSet = jcVariableDecl.getModifiers().getFlags();
        if (modifiersSet.contains(Modifier.STATIC) || modifiersSet.contains(Modifier.FINAL)) {
            return;
        }
        jcVariableDecl.getModifiers().flags += Flags.FINAL;
    }

    /**
     * creates parameter modifiers according to given Pbwired.
     *
     * @param p
     * @return
     */
    private JCTree.JCModifiers makeParamModifiers(Pbwired p) {
        JCTree.JCModifiers modifiers;
        if (p.name().trim().length() > 0) {
            JCTree.JCExpression jc0 = treeMaker.Assign(treeMaker.Ident(names.fromString(Constants.STRING_VALUE)), treeMaker.Literal(p.name()));
            List<JCTree.JCExpression> annParams = List.of(jc0);
            JCTree.JCAnnotation qualifier = treeMaker.Annotation(mainProcessor.access(Constants.QUALIFIER_PATH), annParams);
            List<JCTree.JCAnnotation> annos = List.of(qualifier);
            modifiers = treeMaker.Modifiers(Flags.PARAMETER, annos);
        } else {
            modifiers = treeMaker.Modifiers(Flags.PARAMETER);
        }
        return modifiers;
    }

    private static String generateMethodName(String fieldName) {
        if (fieldName.length() > 1
                && Character.isLowerCase(fieldName.charAt(0))
                && Character.isUpperCase(fieldName.charAt(1))) {
            return "set" + fieldName;
        }
        return "set" + firstUpperCase(fieldName);
    }

    /**
     * Returns a String which capitalizes the first letter of the string.
     * From spring
     */
    public static String firstUpperCase(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
    }
}
