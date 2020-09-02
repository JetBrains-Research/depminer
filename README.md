# dependencies-plugin-idea

## Requirements

- JDK version 8u251 (Available [here](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html))
- Gradle [distribution](https://gradle.org/install/)

## Build

Build the project using 

    /gradlew build 
    
or a corresponding Gradle task in your IDE. For more info on how to build Kotlin projects with Gradle see:

- [Kotlin gradle plugin](https://kotlinlang.org/docs/reference/using-gradle.html)
- [Gradle guide](https://guides.gradle.org/building-java-applications/)

## Current Usage

Navigate to Depminer root directory and call ./extract-dependencies.sh [arg1] [arg2] [arg3] with the following arguments:

- arg1 - Input project directory. 
- arg1 - Input project source code root directory. 
- arg2 - A path to directory where you'd like your output file placed.

For instance: 

    ./extract-dependencies.sh testData/testProjects/javaTestProject testData/testProjects/javaTestProject/src . 
    
will run the analysis of javaTestProject located at testData/testProjects in the DepMiner root directory. The analysis output will be places in the Depminer's root directory under the name _"test-output"_ 





