package com.github.karazhanov.ast;

import com.squareup.javapoet.CodeBlock;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

import java.util.stream.Collectors;

/**
 * @author karazhanov on 19.10.17.
 */
public class AstBodyCode extends TreeTranslator {

    private CodeBlock.Builder astCodeBlock;

    public AstBodyCode(CodeBlock.Builder astCodeBlock) {
        this.astCodeBlock = astCodeBlock;
    }

    @Override
    public void visitMethodDef(JCTree.JCMethodDecl methodDecl) {
        super.visitMethodDef(methodDecl);
        astCodeBlock.add(methodDecl.body.stats
                .stream()
                .map(JCTree::toString)
                .collect(Collectors.joining(";\n")))
                .add("\n");
    }

    public CodeBlock.Builder getCodeBlock() {
        return astCodeBlock;
    }
}
