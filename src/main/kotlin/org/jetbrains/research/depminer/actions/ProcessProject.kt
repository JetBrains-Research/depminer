package org.jetbrains.research.depminer.actions

import org.jetbrains.research.depminer.model.AnalysisScope
import org.jetbrains.research.depminer.model.Dependency
import org.jetbrains.research.depminer.model.ProjectScope

fun getProjectDependencies(projectPath: String): Collection<Dependency> {
    return getDependencies(ProjectScope(projectPath))
}

private fun getDependencies(scope: AnalysisScope): Collection<Dependency> {
    return emptyList()
}