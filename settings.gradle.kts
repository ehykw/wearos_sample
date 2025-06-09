pluginManagement {
    repositories {
  //      google {
   //         content {
   //             includeGroupByRegex("com\\.android.*")
   //             includeGroupByRegex("com\\.google.*")
   //             includeGroupByRegex("androidx.*")
    //        }
    //    }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    /*
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.kotlin.plugin.compose") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
            }
        }
    }
    */
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

}

rootProject.name = "Sotuken"
//include(":app")
include(":mobile")
include(":wear")
