pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AA-car-repair-Android-AI"
include(
    ":app",
    ":core",
    ":contracts",
    ":domain",
    ":data",
    ":analytics",
    ":sync",
    ":feature:chat",
    ":feature:estimator",
    ":feature:dtc",
    ":feature:calculators",
    ":feature:fleet",
    ":feature:voice",
    ":feature:inspection"
)
