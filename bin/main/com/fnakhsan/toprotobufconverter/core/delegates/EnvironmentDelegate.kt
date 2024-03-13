package com.fnakhsan.toprotobufconverter.core.delegates

import com.fnakhsan.toprotobufconverter.core.PathException
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory

interface EnvironmentDelegate {
    fun obtainProjectModel(event: AnActionEvent): ProjectModel
    fun refreshProject(projectModel: ProjectModel)
}

internal class EnvironmentDelegateImpl : EnvironmentDelegate {

    override fun obtainProjectModel(event: AnActionEvent): ProjectModel {
        val directory = checkPath(event)
        val project = event.project as Project
        // Apa bedanya LangDataKeys.VIRTUAL_FILE sama CommonDataKeys.VIRTUAL_FILE ??
        val virtualFolder = event.getData(LangDataKeys.VIRTUAL_FILE) as VirtualFile
        val packageName = ProjectRootManager
            .getInstance(project)
            .fileIndex
            .getPackageNameByDirectory(virtualFolder)
        return ProjectModel(
            directory = directory,
            packageName = packageName,
            project = project,
            virtualFolder = virtualFolder
        )
    }

    override fun refreshProject(projectModel: ProjectModel) {
        ProjectView.getInstance(projectModel.project).refresh()
        projectModel.virtualFolder.refresh(false, true)
    }

    private fun checkPath(event: AnActionEvent): PsiDirectory {
        val pathItem = event.getData(CommonDataKeys.NAVIGATABLE)
        return when (pathItem) {
            is PsiDirectory -> pathItem
            else -> throw PathException()
        }
    }
}