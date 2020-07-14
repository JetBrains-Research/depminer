# dependencies-plugin-idea

## Requirements

- JDK version 8u251 (Available [here](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html))
- Gradle [distribution](https://gradle.org/install/)

As well as the following Intellij IDEA plugins/extensions:

- Gradle 
- Kotlin extension for .kt files

## Brief

Open the project folder with Intellij IDEA, make sure you have Gradle plugin active. If it is so, the Gradle plugin tab will appear on the either side from the editor along with other tools. 

Open the Gradle tab, and allow some time for Gradle dependencies and project configuration. After it has configure correctly, go on and expand the following menu points in the gradle tab:

**dependencies-plugin-idea**/project name -> Tasks -> intellij and in the last level menu run 'runIde' process. This should open a test instance of intellij Community Version in a new window from which the developed plugin can be used and manually tested. 

## Current Usage

When in a running test instance of IDEA, right click anywhere for a context menu to pop up and the plugin action should appear on the bottom of the context menu. The information pop-up window will then appear reflecting all of the infromation retrieved about the element at the caret (An element at the typing cursor or an element highlighted in code by the editor).

## Known Bugs and Vulnerabilities

- Currently unsure about the possibility of cross platform/language independent usage - shall dive into PSI in a little more depth. Runs into problems with certain elements in kotlin code.



