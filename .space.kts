job("Build and run tests") {
    container("openjdk:11") {
        resources {
            memory = 4096
        }

        kotlinScript { api ->
            api.gradlew("build")
        }
    }
}
