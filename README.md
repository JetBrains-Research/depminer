# dependencies-plugin-idea

## Requirements

- JDK version 8u251 (Available [here](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html))
- Gradle [distribution](https://gradle.org/install/)

As well as the following Intellij IDEA plugins/extensions:

- Gradle 
- Kotlin extension for .kt files

## Brief

Open the project folder with Intellij IDEA, make sure you have Gradle plugin active. If it is so, the Gradle plugin tab will appear on the either side from the editor along with other tools. 

Build the project using the corresponding gradle task in the pop up menu. Now in order to run the analysis use a bash script **extract-dependencies.sh** 

## Current Usage

Navigate to project directory and call ./extract-dependencies.sh [arg1] [arg2] with the following arguments:

- arg1 - Input project directory. (Currently only supports directories contained in the main project, for instance src/test/resources/testProjects)
- arg2 - A path to directory where you'd like your output file placed. 





