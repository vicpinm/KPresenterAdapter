package com.vicpin.kpa.annotation.processor

import com.vicpin.kpa.annotation.processor.model.Model
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment

/**
 * Created by victor on 10/12/17.
 */
class ViewHolderWritter(private val entity: Model.EntityModel) {

    private var text: String = ""
    private val className: String

    init {
        this.className = entity.name + VIEWHOLDER_SUFIX
    }

    private fun generateClass(): String {
        appendPackpage()
        appendImports()
        appendClassName()
        appendClassProperties()
        appendPrimaryConstructor()
        appendGetPresenterMethod()
        appendBindMethod()
        appendClassEnding()

        return text
    }

    private fun appendPackpage() {
        newLine("package ${entity.pkg}", newLine = true)
    }

    private fun appendImports() {
        newLine("import ${entity.modelClass}")
        newLine("import android.view.View")
        newLine("import com.vicpin.butcherknife.Binding")
        newLine("import com.vicpin.kpresenteradapter.ViewHolder")
        newLine("import com.vicpin.kpresenteradapter.ViewHolderPresenter")
        newLine("import ${entity.pkg}.${entity.name}Binding", newLine = true)
    }

    private fun appendClassName() {
        newLine("public class $className extends ViewHolder<${entity.name}> implements ${entity.name}PresenterParent.View {", newLine = true)
    }

    private fun appendClassProperties() {
        newLine("private ${entity.name}${PresenterWritter.PRESENTER_SUFIX} presenter = new ${entity.name}${PresenterWritter.PRESENTER_SUFIX}()", level = 1)
        newLine("private Binding binding", newLine = true, level = 1)
    }

    private fun appendPrimaryConstructor() {
        newLine("public ${entity.name}ViewHolderParent(View itemView) {", level = 1)
        newLine("super(itemView)", level = 2)
        newLine("binding = ${entity.name}Binding.with(itemView)", level = 2)
        newLine("}", newLine = true, level = 1)
    }

    private fun appendGetPresenterMethod() {
        newLine("@Override public ViewHolderPresenter<${entity.name}, ${entity.name}${PresenterWritter.PRESENTER_SUFIX}.View> getPresenter() {", level = 1)
        newLine("return presenter", level = 2)
        newLine("}", newLine = true, level = 1)
    }

    private fun appendBindMethod() {
        newLine("@Override public void bind(${entity.name} data) {", level = 1)
        newLine("binding.bind(data)", level = 2);
        newLine("}", newLine = true, level = 1)
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

        private val VIEWHOLDER_SUFIX = "ViewHolderParent"

    }
}
