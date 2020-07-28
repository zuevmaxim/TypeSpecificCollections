rootProject.name = "TypeSpecificCollections"
include("Performance")
include("JVMPerformance")

pluginManagement {
    repositories {
        maven("https://dl.bintray.com/kotlin/kotlinx")
        gradlePluginPortal()
    }
}
