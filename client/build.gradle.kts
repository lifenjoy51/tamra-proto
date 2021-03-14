import com.soywiz.korge.gradle.KorgeGradlePlugin
import com.soywiz.korge.gradle.korge

apply<KorgeGradlePlugin>()

korge {
    id = "me.lifenjoy51.tamra"

    targetJvm()
    targetJs()
    // targetDesktop()
    // targetIos()
    // targetAndroidIndirect()
    // targetAndroidDirect()

    // https://discuss.kotlinlang.org/t/is-it-possible-to-make-a-library-module-that-is-pure-kotlin-without-a-target-platform-like-jvm-or-js-so-that-it-can-be-consumed-by-any-other-kotlin-module/18657
    dependencyProject(":domain")
}