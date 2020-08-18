package org.jetbrains.research.depminer.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun convertToJsonString(dependenciesMap: Collection<Dependency>): String {
    val gson = Gson()
    return gson.toJson(dependenciesMap)
}

fun convertSingleDependencyToJSON(dependency: Dependency): String {
    val gson = Gson()
    return gson.toJson(dependency)
}

fun readFromJsonString(jsonString: String): Collection<Dependency> {
    val gson = Gson()
    val collectionDependeciesType = object : TypeToken<Collection<Dependency>>() {}.type
    val dependenciesMap: Collection<Dependency> = gson.fromJson(jsonString, collectionDependeciesType)
    return dependenciesMap
}