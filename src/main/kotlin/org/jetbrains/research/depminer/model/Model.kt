package org.jetbrains.research.depminer.model

import java.io.File


/**
 * Describes a connection type between two elements [CodeElement] in the [Dependency] class
 */
enum class ConnectionType {
    USAGE, // "from" is declared in "to"
    UNKNOWN  // going to use this for now
}

/**
 * Describes a [CodeElement] type
 */
enum class ElementType {
    FILE,
    CLASS,
    FUNCTION,
    LINE,
    UNKNOWN
}

/**
 * Structure representing a location (coordinate) in certain file
 *
 * @property line
 * @property offset IntelliJ SDK platform defined offset
 */
data class FileLocation(val line: Int, val offset: Int)

/**
 * Data class representing a region of the file
 *
 * @property start
 * @property end
 */
data class FileRange(val start: FileLocation?, val end: FileLocation?)

/**
 * Full description of code element location in file system
 *
 * @property path absolute path to file containing the code element
 * @property range [FileRange] of code element in the file
 */
data class LocationInfo(val path: String, val range: FileRange): AnalysisScope {
    override fun getLocations(): List<LocationInfo> {
        return listOf(this)
    }
}

/**
 * Data class representing a code element that can form a dependency (field, function/method, class etc)
 *
 * @property location A [LocationInfo] instance containing information about element location on disk
 * @property type [ElementType] of the given code element
 */
data class CodeElement(val location: LocationInfo, val type: ElementType)

/**
 * Data class describing a dependency betwwen two Code Elements
 *
 * @property ConnectionType connection type between two elements
 * @property from 1/2 [CodeElement]
 * @property to 2/2 [CodeElement], a pair of Code Elements forming a dependency
 */
data class Dependency(val type: ConnectionType, val from: CodeElement, val to: CodeElement)


interface AnalysisScope {
    fun getLocations(): List<LocationInfo>
}

class FileScope(val path: String): AnalysisScope {
    override fun getLocations(): List<LocationInfo> {
        return listOf(LocationInfo(path, FileRange(null, null)))
    }
}

class ProjectScope(val path: String): AnalysisScope {
    override fun getLocations(): List<LocationInfo> {
        val analysisScope = mutableListOf<LocationInfo>()
        File(path).walk().forEach {
            if (it.isFile) {
                 if (!it.absolutePath.contains(".idea")) {
                     // Excluding .idea folder files for now
                     analysisScope.add(LocationInfo(it.absolutePath, FileRange(null, null)))
                 }
            }
        }
        return analysisScope
    }
}