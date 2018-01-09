package com.vicpin.kpa.annotation.processor

import com.vicpin.kpa.annotation.processor.model.Model
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import javax.annotation.processing.ProcessingEnvironment

/**
 * Created by victor on 10/12/17.
 */
class AdapterWritter(private val entity: Model.EntityModel) {

    private var text: String = ""
    private val className: String

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

        try { // write the file

            val outputPath = File(env.options["kapt.kotlin.generated"],"${entity.pkg}.$className").toPath().resolve("$className.kt")
            EnvironmentUtil.logWarning("generando $outputPath")

            OutputStreamWriter(Files.newOutputStream(outputPath), StandardCharsets.UTF_8).use { writer -> writer.append(text) }


                    //FileSpec.builder(entity.pkg, "$className.kt").build().writeTo(File("${entity.pkg}.$className.kt"))
           // EnvironmentUtil.logWarning("generando ${entity.pkg}.$className.kt")

            /*EnvironmentUtil.logWarning("generando ${entity.pkg}.$className.kt")
            val source = env.filer.createSourceFile("${entity.pkg}.$className.kt")

            val writer = source.openWriter()
            writer.write(generateClass())
            writer.flush()
            writer.close()*/
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