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
        // All of React Native (JS, Obj-C sources, Android binaries) is installed from npm
        maven { url = uri("$rootDir/../node_modules/react-native/android") }
        // Android JSC is installed from npm
        maven { url = uri("$rootDir/../node_modules/jsc-android/dist") }
        maven { url = uri("https://www.jitpack.io") }
        maven { url = uri("https://artifactory.appodeal.com/appodeal") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Add Appodeal repository
        maven { url = uri("https://artifactory.appodeal.com/appodeal") }
    }
}

rootProject.name = "StickerPocket"
include(":app")
 