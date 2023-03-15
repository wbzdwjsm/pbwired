package com.purpblue.pbwired.processor;

import com.purpblue.pbwired.annotation.outer.EnableSimpleProcessorForConfigurableAnnotate;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.util.Set;

/**
 * In Spring, if a class is annotated with {@link Configurable @Configurable}, when a "new" operation is called, the fields
 * annotated with {@link Autowired @Autowired} or {@link Resource @Resource} in the class
 * can be injected with Spring beans. For the purpose Spring uses LoadTimeWeaving(LTW) mechanism, but LTW needs javaagent, which
 * makes something complex. For example, javaagent must be added into VM options, but which is prohibited in some groups or companies.
 * <p>This {@link ConfigurableAnnotationProcessor ConfigurableAnnotationProcessor} offers another way to implement the function of {@link Configurable @Configurable}.
 * It does not depend on javaagent, works in compiling-time, whose mechanism is modifying AST.
 * <p>What you need is to add pbwired-1.4.1 dependency into your maven/gradle config, and annotate {@link EnableSimpleProcessorForConfigurableAnnotate} onto a configuration class.
 *
 * @author Purpblue
 */
class ConfigurableAnnotationProcessor {
    private final JavacTrees javacTrees;
    private final TreeMaker treeMaker;
    private final Names names;
    private final Messager messager;
    private final PbMainProcessor mainProcessor;

    private JCTree.JCExpression utilGetBeanMethod;

    ConfigurableAnnotationProcessor(Messager messager, JavacTrees javacTrees, Names names, TreeMaker treeMaker, PbMainProcessor mainProcessor) {
        this.messager = messager;
        this.names = names;
        this.treeMaker = treeMaker;
        this.javacTrees = javacTrees;
        this.mainProcessor = mainProcessor;
        utilGetBeanMethod = mainProcessor.access("com.purpblue.pbwired.util.SpringBeanUtils.getBean");
    }

    void simpleProcessConfigurableAnnotation(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Configurable.class);
        for (Element e : elements) {
            JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) javacTrees.getTree(e);
            // Get all fields with @Autowired or @Resource
            for (JCTree tree : classDecl.defs) {
                if (Tree.Kind.VARIABLE.equals(tree.getKind())) {
                    // Check @Autowired
                    JCTree.JCVariableDecl variableDecl = (JCTree.JCVariableDecl) tree;
                    Autowired autowired = variableDecl.sym.getAnnotation(Autowired.class);
                    if (autowired == null) {
                        // Check @Resource
                        Resource resource = variableDecl.sym.getAnnotation(Resource.class);
                        if (resource == null) {
                            continue;
                        }
                    }

                    variableDecl.init = treeMaker.Apply(
                        null,
                        utilGetBeanMethod,
                        List.of(mainProcessor.access(variableDecl.sym.type.toString() + ".class"))
                    );
                }
            }
        }
    }
}
