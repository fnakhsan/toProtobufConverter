package com.fnakhsan.toprotobufconverter.converter.postprocessing

import com.robohorse.robopojogenerator.models.GenerationModel
import com.robohorse.robopojogenerator.properties.ClassItem
import com.robohorse.robopojogenerator.properties.templates.ClassTemplate
import com.robohorse.robopojogenerator.utils.ClassGenerateHelper
import com.robohorse.robopojogenerator.utils.ClassTemplateHelper

internal abstract class BasePostProcessor(
    protected val generateHelper: ClassGenerateHelper,
    protected val classTemplateHelper: ClassTemplateHelper
) {

    fun proceed(
        classItem: ClassItem,
        generationModel: GenerationModel
    ): String {
        applyAnnotations(generationModel, classItem)
        return proceedClass(classItem, generationModel)
    }

    abstract fun applyAnnotations(
        generationModel: GenerationModel,
        classItem: ClassItem
    )

    abstract fun proceedClassBody(
        classItem: ClassItem,
        generationModel: GenerationModel
    ): String?

    abstract fun createClassTemplate(
        classItem: ClassItem,
        classBody: String?,
        generationModel: GenerationModel
    ): String

    private fun proceedClass(
        classItem: ClassItem,
        generationModel: GenerationModel
    ): String {
        val classBody = generateHelper.updateClassBody(
            proceedClassBody(classItem, generationModel)
        )
        val classTemplate = createClassTemplate(classItem, classBody, generationModel)
        val importsBuilder = proceedClassImports(classItem.classImports, generationModel)
        return createClassItemText(
            classItem.packagePath,
            importsBuilder.toString(),
            classTemplate
        )
    }

    open fun proceedClassImports(
        imports: HashSet<String>,
        generationModel: GenerationModel
    ): StringBuilder {
        val importsBuilder = StringBuilder()
        for (importItem in imports) {
            importsBuilder.append(importItem)
            importsBuilder.append(ClassTemplate.NEW_LINE)
        }
        return importsBuilder
    }

    open fun createClassItemText(
        packagePath: String?,
        imports: String?,
        classTemplate: String?
    ) = classTemplateHelper.createClassItem(
        packagePath,
        imports,
        classTemplate
    )
}
