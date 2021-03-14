package ui

import com.soywiz.korag.DefaultShaders
import com.soywiz.korag.shader.Uniform
import com.soywiz.korag.shader.VarType
import com.soywiz.korag.shader.VertexShader
import com.soywiz.korag.shader.storageForMatrix3D
import com.soywiz.korge.render.BatchBuilder2D
import com.soywiz.korge.view.filter.ShaderFilter
import com.soywiz.korma.geom.*

class Pseudo3DFilter(
    width: Double,
    height: Double
) : ShaderFilter() {
    companion object {
        private val u_MVP = Uniform("mvpMatrix", VarType.Mat4)
        private val VERTEX_SHADER = VertexShader {
            DefaultShaders {
                SET(v_Tex, a_Tex)
                SET(BatchBuilder2D.v_ColMul, BatchBuilder2D.a_ColMul)
                SET(BatchBuilder2D.v_ColAdd, BatchBuilder2D.a_ColAdd)
                SET(out, u_MVP * vec4(a_Pos, 0f.lit, 1f.lit))
                //SET(out, u_ProjMat * vec4(a_Pos, 0f.lit, 1f.lit))
            }
        }
    }

    val eye = Vector3D(0f, 1.2f, 30f)
    val target = Vector3D(0f, 0f, 0f)
    val up = Vector3D(0f, 1f, 0f)

    val model = Matrix3D()
        .rotate(Angle.fromDegrees(180), Angle.fromDegrees(0), Angle.fromDegrees(0))
        .translate(-width / 2, -height / 2, 0.0)

    val view = Matrix3D().setToLookAt(eye, target, up)
    val perspective = Matrix3D().setToPerspective(
        120.degrees,
        1.5,
        0.006,
        1000.0
    ).invert()
    val mvp = perspective * view * model

    /** 3x3 matrix representing a convolution kernel */
    var mvpMatrix: Matrix3D by uniforms.storageForMatrix3D(u_MVP, mvp)

    override val vertex = VERTEX_SHADER
}
