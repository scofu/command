plugins {
    id("base-conventions")
}

dependencies {
    api(project(":command-api"))
    testImplementation("com.scofu:app-bootstrap-api:1.0-SNAPSHOT")
}