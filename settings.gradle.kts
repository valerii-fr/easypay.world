dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven("https://androidx.dev/snapshots/builds/7633184/artifacts/repository")
        maven("https://jitpack.io")
    }
}

rootProject.name = "AEON Test Task"
include(":app")
