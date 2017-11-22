package com.github.karazhanov.generator;

import com.github.karazhanov.annotations.VertXRestController;
import com.github.karazhanov.annotations.methods.DELETE;
import com.github.karazhanov.annotations.methods.GET;
import com.github.karazhanov.annotations.methods.POST;
import com.github.karazhanov.annotations.methods.PUT;
import com.github.karazhanov.annotations.params.Path;
import com.github.karazhanov.ast.AstBodyCode;
import com.github.karazhanov.builders.ComponentBuilder;
import com.github.karazhanov.configuration.vertx.controllers.METHOD_TYPE;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.github.karazhanov.annotations.params.Query;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

/**
 * @author karazhanov on 18.10.17.
 */
public class ComponentGenerator {

    public static ComponentBuilder generateComponent(ExecutableElement method, TypeElement mOriginElement, Trees trees) {
        ComponentBuilder.RequestMethod requestMethod = getRequestMethodType(method);
        if (requestMethod == null) {
            return null;
        }
        VertXRestController vertXRestController = mOriginElement.getAnnotation(VertXRestController.class);
        ComponentBuilder component = new ComponentBuilder();
        JCTree.JCCompilationUnit jcCompilationUnit = toUnit(mOriginElement, trees);
        if(jcCompilationUnit != null) {
            component.setImport(jcCompilationUnit.getImports());
            component.setPackageName(jcCompilationUnit.getPackageName().toString());
        }
        component.setBasePath(vertXRestController.value());
        component.setBaseClassName(mOriginElement.getSimpleName().toString());
        component.setMethodName(method.getSimpleName().toString());
        component.setMethodCodeBlock(getMethodCodeBody(method, trees));
        component.setRequestMethod(requestMethod);
        return component;
    }


    private static JCTree.JCCompilationUnit toUnit(Element element, Trees trees) {
        TreePath path = trees == null ? null : trees.getPath(element);
        if (path == null) return null;
        return (JCTree.JCCompilationUnit) path.getCompilationUnit();
    }

    private static ComponentBuilder.RequestMethod getRequestMethodType(ExecutableElement method) {
        GET _get = method.getAnnotation(GET.class);
        if (_get != null) {
            return new ComponentBuilder.RequestMethod(METHOD_TYPE.GET, _get.value());
        }
        POST _post = method.getAnnotation(POST.class);
        if (_post != null) {
            return new ComponentBuilder.RequestMethod(METHOD_TYPE.POST, _post.value());
        }
        PUT _put = method.getAnnotation(PUT.class);
        if (_put != null) {
            return new ComponentBuilder.RequestMethod(METHOD_TYPE.PUT, _put.value());
        }
        DELETE _delete = method.getAnnotation(DELETE.class);
        if (_delete != null) {
            return new ComponentBuilder.RequestMethod(METHOD_TYPE.DELETE, _delete.value());
        }
        return null;
    }

    private static CodeBlock.Builder getMethodCodeBody(ExecutableElement method, Trees mTrees) {
        CodeBlock.Builder astCodeBlock = CodeBlock.builder();
        generateParameters(astCodeBlock, method);
        AstBodyCode astBodyCode = new AstBodyCode(astCodeBlock);
        ((JCTree) mTrees.getTree(method)).accept(astBodyCode);
        return astBodyCode.getCodeBlock();
    }

    // TODO validate params type
    private static void generateParameters(CodeBlock.Builder astBodyCode, ExecutableElement method) {
        List<? extends VariableElement> parameters = method.getParameters();
        for (VariableElement param : parameters) {
            try {
                generatePathParam(param, astBodyCode);
                generateQueryParam(param, astBodyCode);
                generateBodyParam(param, astBodyCode);
            } catch (RuntimeException ignore) {
            }
        }
    }

    private static void generatePathParam(VariableElement param, CodeBlock.Builder astBodyCode) {
        Path path = param.getAnnotation(Path.class);
        if (path != null) {
            generateParam(param, astBodyCode);
        }
    }

    private static void generateQueryParam(VariableElement param, CodeBlock.Builder astBodyCode) {
        Query query = param.getAnnotation(Query.class);
        if (query != null) {
            generateParam(param, astBodyCode);
        }
    }

    private static void generateParam(VariableElement param, CodeBlock.Builder astBodyCode) {
        ComponentValidator.validateType(param);
        ComponentValidator.validatePathContainParam(param);
        astBodyCode.addStatement(
                "$T $L = ($T) routingContext.request().getParam(\"$L\")",
                ClassName.get(param.asType()),
                param.getSimpleName(),
                ClassName.get(param.asType()),
                param.getSimpleName());
    }

    private static void generateBodyParam(VariableElement param, CodeBlock.Builder astBodyCode) {
//        Body body = o.getAnnotation(Body.class);
    }


}
