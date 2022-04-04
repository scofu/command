import gradle.kotlin.dsl.accessors._30870a47e380c7c13eee96805f88c56d.bukkit
import org.gradle.kotlin.dsl.the

plugins {
    id("base-conventions")
    id("net.minecrell.plugin-yml.bungee")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.18-R0.1-SNAPSHOT")
}

val app = the<AppExtension>()
// Default plugin.yml
bungee {
    version = project.version as String
    description = project.description
    author = "jesper@scofu.com"
    main = app.mainClass.getOrElse(".")
}