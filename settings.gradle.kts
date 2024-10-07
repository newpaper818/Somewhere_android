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

include(":core:model")

include(":core:ui:ui")
include(":core:ui:designsystem")

include(":core:data:data")
include(":core:data:datastore")
include(":core:data:firebase-firestore")
include(":core:data:google-map-places")
include(":core:data:local-image-file")
include(":core:data:firebase-functions")
include(":core:data:firebase-common")
include(":core:data:firebase-storage")
include(":core:data:firebase-authentication")


include(":feature:signin")
include(":feature:trip")
include(":feature:profile")
include(":feature:more")
include(":feature:dialog")
include(":core:utils")
include(":core:data:gemini-ai")
include(":core:data:moshi")
