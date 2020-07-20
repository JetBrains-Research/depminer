package org.jetbrains.research.depminer.runner

import com.intellij.openapi.application.ApplicationStarter
import java.io.File
import kotlin.system.exitProcess

const val testOutput = "test-output"
const val testContent = "test"

class IdeRunner : ApplicationStarter {
    override fun getCommandName(): String = "mine-dependencies"

    override fun main(args: MutableList<String>) {
        File(testOutput).writeText(testContent)
        exitProcess(0)
    }
}