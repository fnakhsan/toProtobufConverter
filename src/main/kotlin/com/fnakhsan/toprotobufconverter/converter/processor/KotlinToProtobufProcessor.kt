package com.fnakhsan.toprotobufconverter.converter.processor

import com.fnakhsan.toprotobufconverter.converter.parser.KotlinDataTypeParser
import com.fnakhsan.toprotobufconverter.converter.template.CommonTemplate.INDENT
import com.fnakhsan.toprotobufconverter.converter.template.CommonTemplate.NEW_LINE
import com.fnakhsan.toprotobufconverter.converter.template.FileOptionTemplate.JAVA_MULTIPLE_FILE
import com.fnakhsan.toprotobufconverter.converter.template.FileOptionTemplate.JAVA_OUTER_CLASSNAME
import com.fnakhsan.toprotobufconverter.converter.template.ImportTemplate.IMPORT_TEMPLATE
import com.fnakhsan.toprotobufconverter.converter.template.MessageTemplate.MESSAGE_TEMPLATE
import com.fnakhsan.toprotobufconverter.converter.template.PackageTemplate.JAVA_PACKAGE
import com.fnakhsan.toprotobufconverter.converter.template.PackageTemplate.PACKAGE_TEMPLATE
import com.fnakhsan.toprotobufconverter.converter.template.ProtobufTemplateHelper
import com.fnakhsan.toprotobufconverter.converter.template.VersionTemplate.PROTO2
import com.fnakhsan.toprotobufconverter.converter.template.VersionTemplate.PROTO3
import com.fnakhsan.toprotobufconverter.converter.template.VersionTemplate.VERSION_TEMPLATE
import com.fnakhsan.toprotobufconverter.core.models.ConversionModel
import com.fnakhsan.toprotobufconverter.core.models.NumericPreferencesVM
import com.fnakhsan.toprotobufconverter.core.models.ProjectModel
import com.fnakhsan.toprotobufconverter.core.models.VersionVW
import com.intellij.psi.PsiField
import org.jetbrains.kotlin.psi.KtFile

class KotlinToProtobufProcessor(private val projectModel: ProjectModel, private val conversionModel: ConversionModel) :
    ProtobufTemplateHelper {

    override fun getVersion(): String = String.format(
        VERSION_TEMPLATE, when (conversionModel.versionEnum) {
            is VersionVW.Proto2 -> PROTO2
            is VersionVW.Proto3 -> PROTO3
        }
    )

    override fun getPackage(): String = projectModel.packageName.let {
        String.format(
            PACKAGE_TEMPLATE,
            it
//            it?.split(".")?.subList(1, it.lastIndex)?.joinToString(".")
        )
    }

    override fun getImport(imports: List<String>): String = StringBuilder().apply {
        imports.forEach { import ->
            this.append(
                String.format(
                    IMPORT_TEMPLATE, import
                )
            )
        }
    }.toString()

    override fun getFileOption(): String = StringBuilder().apply {
        append(String.format(JAVA_PACKAGE, projectModel.packageName))
        append(String.format(JAVA_MULTIPLE_FILE, true))
        append(String.format(JAVA_OUTER_CLASSNAME, projectModel.virtualFile.nameWithoutExtension))
    }.toString()

    override fun getMessage(name: String, body: String): String = String().format(MESSAGE_TEMPLATE, name, body)

    override fun getField(id: Int, field: PsiField, numericPref: NumericPreferencesVM): String =
        KotlinDataTypeParser().parseDataType(id = id, field = field, numericPref = numericPref)

    override fun getAllContent(): String = StringBuilder().apply {
        append(getVersion())
        append(getPackage())
        append(getFileOption())
        (projectModel.psiFile as KtFile).classes.forEach {
            val body = StringBuilder()
            var id = 1
            it.allFields.forEach { field ->
                body.append(INDENT)
                body.append(getField(id = id, field = field, numericPref = conversionModel.preferenceEnum))
                id++
            }
            append(NEW_LINE)
            append(getMessage(it.name.toString(), body.toString()))
        }
    }.toString()
}