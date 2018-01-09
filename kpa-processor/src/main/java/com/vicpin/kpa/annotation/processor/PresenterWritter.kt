package com.vicpin.kpa.annotation.processor

import com.vicpin.kpa.annotation.processor.model.Model
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment

/**
 * Created by victor on 10/12/17.
 */
class PresenterWritter(private val entity: Model.EntityModel) {

    private var text: String = ""
    private val className: String

    init {
        this.className = entity.name + PRESENTER_SUFIX
    }

    private fun generateClass(): String {

        appendPackpage()
        appendImports()
        appendClassName()
        appendOnCreateMethod()
        appendInterface()
        appendClassEnding()

        return text
    }

    private fun appendPackpage() {
        newLine("package ${entity.pkg}", newLine = true)
    }

    private fun appendImports() {
        newLine("import ${entity.modelClass}")
        newLine("import com.vicpin.kpresenteradapter.ViewHolderPresenter", newLine = true)
    }

    private fun appendClassName() {
        newLine("public class $className extends ViewHolderPresenter<${entity.name}, $className.View> {", newLine = true)
    }

    private fun appendOnCreateMethod() {
        newLine("@Override public void onCreate() {", level = 1)
        newLine("if(getView() != null) getView().bind(data)", level = 2)
        newLine("}", level = 1, newLine = true)
    }

    private fun appendInterface() {
        newLine("interface View {", level = 1)
            newLine("void bind(${entity.name} data)", level = 2)
        newLine("}", level = 1, newLine = true)
    }


    private fun appendClassEnding() {
        newLine("}")
    }

    fun generateFile(env: ProcessingEnvironment) {

        try { // write the file
            val source = env.filer.createSourceFile("${entity.pkg}.$className")

            val writer = source.openWriter()
            writer.write(generateClass())
            writer.flush()
            writer.close()
        } catch (e: IOException) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }

    }

    fun newLine(line: String = "", level: Int = 0, newLine: Boolean = false) {
        var indentation = ""
        var semicolon = if (!line.isEmpty() && !line.endsWith("}") && !line.endsWith("{")) ";" else ""

        (1..level).forEach { indentation += "\t" }

        text += if (newLine) {
            "$indentation$line$semicolon\n\n"
        } else {
            "$indentation$line$semicolon\n"
        }
    }

    companion object {

        public val PRESENTER_SUFIX = "PresenterParent"

    }
}