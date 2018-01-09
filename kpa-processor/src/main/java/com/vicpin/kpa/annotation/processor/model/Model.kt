package com.vicpin.kpa.annotation.processor.model

import com.vicpin.kpa.annotation.processor.EnvironmentUtil
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement


/**
 * Created by Oesia on 07/12/2017.
 */
class Model private constructor() {

    var entities = mutableListOf<EntityModel>()

    private fun parseAnnotation(env: RoundEnvironment, annotationClass: Class<Annotation>) {
        val annotatedClasses = env.getElementsAnnotatedWith(annotationClass)


        for (annotatedClass in annotatedClasses) {
            if (annotatedClass.kind != ElementKind.CLASS) {
                EnvironmentUtil.logError(annotationClass.simpleName + " can only be used for classes")
                break
            }

            val entity = createEntityModel(annotatedClass)

            entities.add(entity)

        }

    }

    private fun createEntityModel(annotatedClass: Element): EntityModel {

            val entityModel = EntityModel(annotatedClass, getPackpageFor(annotatedClass))
            entities.add(entityModel)


        return entityModel


    }

    private fun getPackpageFor(parentClass: Element): String {
        var parent = parentClass.enclosingElement
        while (parent !is PackageElement) {
            parent = parent.enclosingElement
        }
        return parent.qualifiedName.toString()
    }


    inner class EntityModel(internal var modelClass: Element, internal var pkg: String) {
        var name: String

        init {
            this.name = modelClass.simpleName.toString()
        }

    }

    companion object {

        fun buildFrom(env: RoundEnvironment, annotations: Set<String>): Model {
            val model = Model()

            for (annotation in annotations) {
                try {
                    model.parseAnnotation(env, Class.forName(annotation) as Class<Annotation>)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            return model
        }
    }
}


