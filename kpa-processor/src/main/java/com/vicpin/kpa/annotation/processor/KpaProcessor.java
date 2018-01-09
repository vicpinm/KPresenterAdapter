package com.vicpin.kpa.annotation.processor;

import com.vicpin.kpa.annotation.processor.model.Model;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@SupportedAnnotationTypes({
        "com.vicpin.kpa.annotation.AutoViewHolder"
})
public class KpaProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        EnvironmentUtil.init(processingEnv);
        Model model = buildModel(roundEnv);
        for (Model.EntityModel entity : model.getEntities()) {

            createViewHolderClassFor(entity);
            createPresenterClassFor(entity);
            createAdapterClassFor(entity);
        }

        return true;
    }

    private void createViewHolderClassFor(Model.EntityModel entity) {
        ViewHolderWritter writter = new ViewHolderWritter(entity);
        writter.generateFile(processingEnv);
    }

    private void createPresenterClassFor(Model.EntityModel entity) {
        PresenterWritter writter = new PresenterWritter(entity);
        writter.generateFile(processingEnv);
    }

    private void createAdapterClassFor(Model.EntityModel entity) {
        AdapterWritter writter = new AdapterWritter(entity);
        writter.generateFile(processingEnv);
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Model buildModel(RoundEnvironment env) {
        return Model.Companion.buildFrom(env, getSupportedAnnotationTypes());
    }
}
