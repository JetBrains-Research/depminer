plugins {
    id("java")
    id("idea")
    id("org.jetbrains.intellij") version "0.4.21"
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("junit:junit:4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1.2"
    setPlugins("java", "Kotlin")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    runIde {
        val dataset: String? by project
        val output: String? by project
        args = listOfNotNull("mine-dependencies", dataset, output)
        jvmArgs = listOf("-Djava.awt.headless=true") // Provides an intellij instance access under the hood
    }

    register("extractDependencies") {
        dependsOn(runIde)
    }
}
