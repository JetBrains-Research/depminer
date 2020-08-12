package org.jetbrains.research.depminer.model

import com.intellij.psi.*
import java.io.File


/**
 * Describes a connection type between two elements [CodeElement] in the [Dependency] class
 */
enum class ConnectionType {
    USAGE, // "from" - usage, "to" - declaration of the element
    FILE_DEPENDENCY // "from" file uses "to" file data
}

/**
 * Describes a [CodeElement] type
 */
@Deprecated("Language specific logic - unused")
enum class ElementType {
    UNDEFINED, // For null safety for now
    FILE,
    CLASS,
    FUNCTION,
    FIELD,
    OBJECT
}

/**
 * Determines and returns a [psiElement] type
 *
 * @param psiElement an element to inspect
 *
 * @return [ElementType]
 */
@Deprecated("Language specific logic - unused")
fun determineElementType(psiElement: PsiElement): ElementType {
    return when (psiElement) {
        is PsiClass -> ElementType.CLASS
        is PsiMethod -> ElementType.FUNCTION
        is PsiField -> ElementType.FIELD
        is PsiFile -> ElementType.FILE
        is PsiReferenceExpression -> {
            if (psiElement.hasParentMethodCall()) ElementType.FUNCTION else ElementType.UNDEFINED
        }
        else -> ElementType.UNDEFINED
    }
}

/**
 * Checks if a PsiElement has a parent of type PsiMethodCallExpression
 */
@Deprecated("Language specific logic - unused")
private fun PsiElement.hasParentMethodCall(): Boolean {
    return if (this.parent != null) {
        if (this.parent is PsiMethodCallExpression) {
            true
        } else {
            this.parent.hasParentMethodCall()
        }
    } else false
}

/* ================= Remove? ==================*/

///**
// * Structure representing a location (coordinate) in certain file
// *
// * @property line
// * @property offset IntelliJ SDK platform defined offset
// */
//data class FileLocation(val line: Int, val offset: Int)

/* ================= Remove? ==================*/

/**
 * Data class representing a region of the file
 *
 * @property start offset
 * @property end offset
 */
data class FileRange(val start: Int?, val end: Int?)

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
 * @property typeSignature [ElementType] of the given code element
 */
data class CodeElement(val location: LocationInfo, val typeSignature: String)

/**
 * Data class describing a dependency between two Code Elements
 *
 * @property ConnectionType connection type between two elements
 * @property from 1/2 [CodeElement]
 * @property to 2/2 [CodeElement], a pair of Code Elements forming a dependency
 */
data class Dependency(val type: ConnectionType, val from: CodeElement, val to: CodeElement)


interface AnalysisScope {
    fun getLocations(): List<LocationInfo>
}

/* ================= Remove? ==================*/

//class FileScope(private val path: String): AnalysisScope {
//    override fun getLocations(): List<LocationInfo> {
//        return listOf(LocationInfo(path, FileRange(null, null)))
//    }
//}

/* ================= Remove? ==================*/

class ProjectScope(private val path: String): AnalysisScope {
    override fun getLocations(): List<LocationInfo> {
        val analysisScope = mutableListOf<LocationInfo>()
        File(path).walk().forEach {
            if (it.isFile) {
                if (!it.absolutePath.contains(".idea") and !it.absolutePath.contains("out") and !it.isHidden) {
                     // Excluding .idea folder files for now
                     analysisScope.add(LocationInfo(it.absolutePath, FileRange(null, null)))
                 }
            }
        }
        return analysisScope
    }
}