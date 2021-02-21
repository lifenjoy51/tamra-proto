import com.soywiz.korge.gradle.KorgeGradlePlugin
import com.soywiz.korge.gradle.korge

buildscript {
    val korgePluginVersion: String by project

    repositories {
        mavenLocal()
        maven { url = uri("https://dl.bintray.com/korlibs/korlibs") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.soywiz.korlibs.korge.plugins:korge-gradle-plugin:$korgePluginVersion")
    }
}

apply<KorgeGradlePlugin>()

korge {
    id = "me.lifenjoy51.tamra"

// To enable all targets at once

//  targetAll()

// To enable targets based on properties/environment variables
    //targetDefault()

// To selectively enable targets

    targetJvm()
    targetJs()
    // targetDesktop()
    // targetIos()
    // targetAndroidIndirect()
    // targetAndroidDirect()

    // https://discuss.kotlinlang.org/t/is-it-possible-to-make-a-library-module-that-is-pure-kotlin-without-a-target-platform-like-jvm-or-js-so-that-it-can-be-consumed-by-any-other-kotlin-module/18657
    dependencyProject(":domain")
}