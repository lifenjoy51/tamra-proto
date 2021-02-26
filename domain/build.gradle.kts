import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation

plugins {
    kotlin("multiplatform")
}

val kormaVersion = "2.0.6"

group = "me.lifenjoy51.tamra"
version "0.0.1"

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
    maven {
        url = uri("https://dl.bintray.com/korlibs/korlibs")

    }
}

dependencies {
    commonMainImplementation("com.soywiz.korlibs.korma:korma:$kormaVersion")
}

kotlin {
    jvm {
        val main by compilations.getting {
            kotlinOptions {
            }
        }
    }
    // gradle project :domain:unspecified
    js(IR) {// or: IR, LEGACY, BOTH
        browser()
        nodejs()
        // https://youtrack.jetbrains.com/issue/KT-41382
        val main: KotlinJsCompilation by compilations {
            NamedDomainObjectCollectionDelegateProvider.of(this) {
                kotlinOptions {
                    //metaInfo = true
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //implementation(kotlin("stdlib-common"))
            }
        }
        val jvmMain by getting
        val jsMain by getting
    }

}
