job("Build and run tests") {
    container("openjdk:11") {
        resources {
            memory = 1536
        }

        kotlinScript { api ->
            api.gradlew("build")
        }
    }
}
