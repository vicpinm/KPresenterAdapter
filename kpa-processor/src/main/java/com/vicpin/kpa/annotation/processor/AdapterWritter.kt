package com.vicpin.kpa.annotation.processor

import com.vicpin.kpa.annotation.processor.model.Model
import java.io.File
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment

/**
 * Created by victor on 10/12/17.
 */
class AdapterWritter(private val entity: Model.EntityModel) {

    private var text: String = ""
    private val className: String
    val KAPT_KOTLIN_GENERATED_OPTION = "kapt.kotlin.generated"
    init {
        this.className = entity.name + ADAPTER_SUFIX
    }

    private fun generateClass(): String {

        appendPackpage()
        appendImports()
        appendClassName()
        appendGetViewInfomethod()
        appendClassEnding()

        return text
    }

    private fun appendPackpage() {
        newLine("package ${entity.pkg}", newLine = true)
    }

    private fun appendImports() {
        newLine("import ${entity.modelClass}")
        newLine("import com.vicpin.kpresenteradapter.PresenterAdapter")
        newLine("import com.vicpin.kpresenteradapter.model.ViewInfo", newLine = true)
    }

    private fun appendClassName() {
        newLine("class TownPresenterAdapter(val layoutRes: Int) : PresenterAdapter<${entity.name}>() {")
    }

    private fun appendGetViewInfomethod() {
        newLine("override fun getViewInfo(position: Int) = ViewInfo(${entity.name}ViewHolderParent::class, layoutRes)", level = 1)
    }


    private fun appendClassEnding() {
        newLine("}")
    }

    fun generateFile(env: ProcessingEnvironment) {

        try { // write the env
            val options = env.options
            val kotlinGenerated = options[KAPT_KOTLIN_GENERATED_OPTION] ?: ""

            File(kotlinGenerated.replace("kaptKotlin","kapt"), "$className.kt").writer().buffered().use {
                it.appendln(generateClass())
            }

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

        public val ADAPTER_SUFIX = "PresenterAdapter"

    }
}