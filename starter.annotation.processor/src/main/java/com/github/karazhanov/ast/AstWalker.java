package com.github.karazhanov.ast;

import com.github.karazhanov.builders.ComponentBuilder;
import com.sun.source.util.Trees;
import com.github.karazhanov.generator.ComponentGenerator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner8;
import java.util.ArrayList;
import java.util.Collection;

import static javax.lang.model.element.ElementKind.METHOD;

/**
 * @author karazhanov on 19.10.17.
 */
public class AstWalker extends ElementScanner8<Void, Void> {

    private final Trees mTrees;
    private final TypeElement mOriginElement;
    private final Collection<ComponentBuilder> components = new ArrayList<>();

    public AstWalker(ProcessingEnvironment env, TypeElement element) {
        mTrees = Trees.instance(env);
        mOriginElement = element;
    }

    @Override
    public Void visitExecutable(ExecutableElement e, Void aVoid) {
        if (e.getKind() == METHOD) {
            ComponentBuilder componentBuilder = ComponentGenerator.generateComponent(e, mOriginElement, mTrees);
            if (componentBuilder != null) {
                components.add(componentBuilder);
            }
        }
        return super.visitExecutable(e, aVoid);
    }

    public Collection<ComponentBuilder> getComponents() {
        return components;
    }
}
