package com.fnakhsan.toprotobufconverter.converter.processor

import com.fnakhsan.toprotobufconverter.converter.parser.KotlinDataTypeParser
import com.fnakhsan.toprotobufconverter.converter.template.CommonTemplate.CLOSE_CURLY_BRACKET
import com.fnakhsan.toprotobufconverter.converter.template.CommonTemplate.INDENT
import com.fnakhsan.toprotobufconverter.converter.template.CommonTemplate.NEW_LINE
import com.fnakhsan.toprotobufconverter.converter.template.CommonTemplate.OPEN_CURLY_BRACKET
import com.fnakhsan.toprotobufconverter.converter.template.FileOptionTemplate.JAVA_MULTIPLE_FILE
import com.fnakhsan.toprotobufconverter.converter.template.FileOptionTemplate.JAVA_OUTER_CLASSNAME
import com.fnakhsan.toprotobufconverter.converter.template.ImportTemplate.IMPORT_TEMPLATE
import com.fnakhsan.toprotobufconverter.converter.template.Keyword.MESSAGE
import com.fnakhsan.toprotobufconverter.converter.template.PackageTemplate.JAVA_PACKAGE
import com.fnakhsan.toprotobufconverter.converter.template.PackageTemplate.PACKAGE_TEMPLATE
import com.fnakhsan.toprotobufconverter.converter.template.ProtobufTemplateHelper
import com.fnakhsan.toprotobufconverter.converter.template.VersionTemplate.PROTO2
import com.fnakhsan.toprotobufconverter.converter.template.VersionTemplate.PROTO3
import com.fnakhsan.toprotobufconverter.converter.template.VersionTemplate.VERSION_TEMPLATE
import com.fnakhsan.toprotobufconverter.core.KotlinStructureException
import com.fnakhsan.toprotobufconverter.core.PluginException
import com.fnakhsan.toprotobufconverter.core.models.ConversionModel
import com.fnakhsan.toprotobufconverter.core.models.NumericPreferencesVM
import com.fnakhsan.toprotobufconverter.core.models.ProjectModel
import com.fnakhsan.toprotobufconverter.core.models.VersionVW
import com.intellij.psi.PsiField
import org.jetbrains.kotlin.idea.testIntegration.framework.KotlinPsiBasedTestFramework.Companion.asKtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly

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
            it?.replace("/", ".")
        ) + NEW_LINE
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
        append(String.format(JAVA_PACKAGE, projectModel.packageName?.replace("/", ".")))
        append(String.format(JAVA_MULTIPLE_FILE, true))
        append(
            String.format(JAVA_OUTER_CLASSNAME,
                conversionModel.rootFileName.split("_").joinToString("") { it.capitalizeAsciiOnly() })
        )
    }.toString()

    override fun getMessage(name: String, body: String): String =
        "$MESSAGE $name $OPEN_CURLY_BRACKET$NEW_LINE$body$CLOSE_CURLY_BRACKET"

    override fun getField(id: Int, field: PsiField, numericPref: NumericPreferencesVM): String =
        KotlinDataTypeParser().parseField(id = id, field = field, numericPref = numericPref)

    override fun getAllContent(): String =
        try {
            StringBuilder().apply {
                append(getVersion())
                append(getPackage())
                append(getFileOption())

                (projectModel.psiFile as KtFile).classes.filter {
                    it.asKtClassOrObject()?.isData() == true
                }.ifEmpty {
                    throw KotlinStructureException()
                }.forEach {
                    val body = StringBuilder()
                    var id = 1
                    it.allFields.forEach { field ->
                        body.append(INDENT)
                        body.append(getField(id = id, field = field, numericPref = conversionModel.preferenceEnum))
                        body.append(NEW_LINE)
                        id++
                    }
                    append(NEW_LINE)
                    append(getMessage(it.name.toString(), body.toString()))
                }
            }.toString()
        } catch (e: PluginException) {
            throw e
        }

}