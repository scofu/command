pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven {
            name = "scofu"
            url = uri("https://repo.scofu.com/repository/maven-snapshots")
            credentials(PasswordCredentials::class)
        }
    }
}

dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven {
            name = "scofu"
            url = uri("https://repo.scofu.com/repository/maven-snapshots")
            credentials(PasswordCredentials::class)
        }
    }
}

rootProject.name = "command-parent"

sequenceOf(
    "command-api",
    "command-standard",
    "command-bukkit"
).forEach {
    include(it)
    project(":$it").projectDir = file(it)
}
