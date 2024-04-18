pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "Somewhere"
include(":app")
include(":core:ui")
include(":core:designsystem")
include(":core:data")
include(":core:model")
include(":feature:signin")
include(":feature:trip")
include(":feature:profile")
include(":feature:more")
include(":feature:dialog")
include(":core:datastore")
