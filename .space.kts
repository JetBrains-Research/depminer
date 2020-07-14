job("Build and run tests") {
    container("openjdk:11") {
        resources {
            memory = 768
        }

        kotlinScript { api ->
            api.gradlew("build")
        }
    }
}
