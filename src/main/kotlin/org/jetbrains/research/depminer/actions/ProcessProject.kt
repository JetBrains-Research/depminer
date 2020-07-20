package org.jetbrains.research.depminer.actions

import com.intellij.openapi.project.ProjectManager
import org.jetbrains.research.depminer.model.AnalysisScope
import org.jetbrains.research.depminer.model.Dependency
import org.jetbrains.research.depminer.model.FileScope
import org.jetbrains.research.depminer.model.ProjectScope

fun getProjectDependencies(projectPath: String): Collection<Dependency> {
    return getDependencies(ProjectScope(projectPath))
}

fun getFileDependencies(filePath: String): Collection<Dependency> {
    TODO("Implement FileScope approach")
    return getDependencies(FileScope(filePath))
}

private fun getDependencies(scope: AnalysisScope): Collection<Dependency> {
    TODO("Reimplement the approach from /dev branch")
    return emptyList()
}