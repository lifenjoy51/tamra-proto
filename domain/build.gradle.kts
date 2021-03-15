import com.soywiz.korge.gradle.korge
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation

plugins {
    kotlin("multiplatform")
    // @Serializable이 붙은 클래스를 자동 변환한다.
    kotlin("plugin.serialization")
}

val kormaVersion = "2.0.6"

group = "me.lifenjoy51.tamra"
version "0.0.1"


dependencies {
    commonMainApi("com.soywiz.korlibs.korma:korma:$kormaVersion")
    commonMainApi("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
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
