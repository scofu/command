plugins {
    id("com.scofu.common-build.base") version "1.0-SNAPSHOT"
}

dependencies {
    api(project(":command-api"))
    testImplementation("com.scofu:app-bootstrap-api:1.0-SNAPSHOT")
}