package com.example.opengles_test.texture

import android.opengl.GLES20
import android.util.Log
import com.example.opengles_test.IBaseTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * CreateBy:Joker
 * CreateTime:2021/7/22 10:17
 * description：
 */
class CircleTexture : IBaseTexture {

    companion object {
        private val TAG = CircleTexture::class.java.simpleName

        private const val VERTEX_SHADER_CODE =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}"

        private const val FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"

        private const val GROUP_COUNT = 3  //分组 n个一组
    }

    private var circleVertexBuffer: FloatBuffer? = null
    private var circlePosition = floatArrayOf()

    private val mColor = floatArrayOf(
        1f, 0f, 0f, 1f
    )

    private var screenRate = 0.0f

    private var glProgram = 0
    private var mPositionHandle = 0
    private var mColorHandle = 0

    override fun onSurfaceCreated() {
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, VERTEX_SHADER_CODE)
        GLES20.glCompileShader(vertexShader)

        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, FRAGMENT_SHADER_CODE)
        GLES20.glCompileShader(fragmentShader)

        glProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(glProgram, vertexShader)
        GLES20.glAttachShader(glProgram, fragmentShader)
        GLES20.glLinkProgram(glProgram)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        screenRate = width.toFloat() / height.toFloat()
        updatePosition(0f)
    }

    override fun onDrawFrame() {
        GLES20.glUseProgram(glProgram)

        mPositionHandle = GLES20.glGetAttribLocation(glProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, GROUP_COUNT, GLES20.GL_FLOAT, false, 0, circleVertexBuffer)

        mColorHandle = GLES20.glGetUniformLocation(glProgram, "vColor")
        GLES20.glUniform4fv(mColorHandle, 1, mColor, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, circlePosition.size / GROUP_COUNT)

        GLES20.glDisableVertexAttribArray(mPositionHandle)

        Log.i(TAG, "onDrawFrame glProgram=$glProgram mPositionHandle=$mPositionHandle  mColorHandle=$mColorHandle")
    }

    override fun updatePosition(displacement: Float) {
        circlePosition = createPositions(0.5f)
        position2buff(circlePosition)
    }

    override fun updateSize(size: Float) {
        circlePosition = createPositions(size)
        position2buff(circlePosition)
    }

    private fun position2buff(circlePosition: FloatArray) {
        circleVertexBuffer = ByteBuffer.allocateDirect(circlePosition.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        circleVertexBuffer?.put(circlePosition)?.position(0)
    }

    /**
     * @param radius 半径
     */
    private fun createPositions(radius: Float): FloatArray {
        val data = ArrayList<Float>()
        for (i in 0..359) {
            data.add(sin(i.toFloat()) * radius)
            data.add(cos(i.toFloat()) * radius * screenRate)
            data.add(0.0f)
        }
        return data.toFloatArray()
    }
}