package org.jetbrains.research.depminer.model

import com.google.gson.Gson

fun convertToJsonString(dependenciesMap: Collection<Dependency>): String {
    val gson = Gson()
    return gson.toJson(dependenciesMap)
}

fun convertSingleDependencyToJSON(dependency: Dependency): String {
    val gson = Gson()
    return gson.toJson(dependency)
}



