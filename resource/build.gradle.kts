plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    maven { url = uri("https://dl.bintray.com/korlibs/korlibs") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
    mavenCentral()
    google()
}

apply {
    plugin("kotlin")
}

dependencies {
    implementation("com.google.api-client:google-api-client:1.30.4")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.30.6")
    implementation("com.google.apis:google-api-services-sheets:v4-rev581-1.25.0")
}
