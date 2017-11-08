package com.github.karazhanov;

import com.github.karazhanov.annotations.VertXRestController;
import com.github.karazhanov.ast.AstWalker;
import com.github.karazhanov.builders.MainRouteBuilder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;

/**
 * @author karazhanov on 17.10.17.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class RestControllerProcessor extends AbstractProcessor {

    private final Map<TypeElement, AstWalker> mVisitors = new HashMap<>();
    private Messager messager;

    public RestControllerProcessor() {
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.unmodifiableSet(
                Collections.singleton(
                        VertXRestController.class.getCanonicalName()
                ));
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations,
                           final RoundEnvironment roundEnv) {
        for (final Element element : roundEnv.getElementsAnnotatedWith(VertXRestController.class)) {
            if (element instanceof TypeElement) {
                element.accept(mVisitors.computeIfAbsent((TypeElement) element, o -> new AstWalker(processingEnv, o)), null);
            } else {
                messager.printMessage(Diagnostic.Kind.NOTE, "STRANGE TYPE " + element.toString());
            }
            generateComponents(mVisitors.values());
        }
        return true;
    }

    private void generateComponents(Collection<AstWalker> visitors) {
        MainRouteBuilder routeBuilder = new MainRouteBuilder(processingEnv);
        messager.printMessage(Diagnostic.Kind.NOTE, "Generating components for VertXRestController");
        visitors.stream()
                .flatMap(astWalker -> astWalker.getComponents().stream())
                .forEach(routeBuilder::addRoute);
        routeBuilder.build();
    }


}
