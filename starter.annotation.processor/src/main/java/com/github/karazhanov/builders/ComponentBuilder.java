package com.github.karazhanov.builders;

import com.squareup.javapoet.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import io.vertx.ext.web.RoutingContext;
import org.springframework.stereotype.Component;
import com.github.karazhanov.configuration.vertx.controllers.METHOD_TYPE;
import com.github.karazhanov.configuration.vertx.controllers.VertxController;

import javax.lang.model.element.Modifier;

/**
 * @author karazhanov on 19.10.17.
 */
public class ComponentBuilder {

    private String baseClassName;
    private String packageName;
    private String basePath;
    private RequestMethod requestMethod;
    private String methodName;
    private CodeBlock.Builder methodCodeBlock;
    private List<JCTree.JCImport> imports;

    public void setBaseClassName(String baseClassName) {
        this.baseClassName = baseClassName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setRequestMethod(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setMethodCodeBlock(CodeBlock.Builder methodCodeBlock) {
        this.methodCodeBlock = methodCodeBlock;
    }

    public void setImport(List<JCTree.JCImport> imports) {
        this.imports = imports;
    }

    public String getPackageName() {
        return packageName;
    }

    List<JCTree.JCImport> getImports() {
        return imports;
    }

    TypeSpec build() {
        TypeSpec.Builder clazz = TypeSpec.classBuilder(baseClassName
                + "$" + requestMethod.type + "_"
                + methodName.substring(0, 1).toUpperCase() + methodName.substring(1));
        clazz.addModifiers(Modifier.PUBLIC)
                .superclass(VertxController.class)
                .addAnnotation(Component.class);

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("super($T.$L, $S)", requestMethod.type.getClass(), requestMethod.type, buildPath())
                .build();

        MethodSpec executeMethod = MethodSpec.methodBuilder("execute")
                .addAnnotation(Override.class)
                .returns(Object.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(RoutingContext.class, "routingContext")
                .addCode(methodCodeBlock.build())
                .build();

        return clazz
                .addMethod(constructor)
                .addMethod(executeMethod)
                .build();
    }

    private String buildPath() {
        return basePath + requestMethod.path;
    }

    public static class RequestMethod {
        METHOD_TYPE type;
        String path;

        public RequestMethod(METHOD_TYPE type, String value) {
            this.type = type;
            this.path = value;
        }
    }

}
