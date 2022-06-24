package com.example.opengles_test.texture

import android.annotation.SuppressLint
import android.opengl.GLES20
import android.util.Log
import com.example.opengles_test.IBaseTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL

/**
 * CreateBy:Joker
 * CreateTime:2021/7/20 19:14
 * description：
 */
@SuppressLint("NewApi")
class TriangleTexture : IBaseTexture {

    companion object {
        private val TAG = TriangleTexture::class.java.simpleName

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

        private const val GROUP_COUNT = 3  //分组 n个一组 坐标系xyz
    }

    private var positionVertexBuffer: FloatBuffer? = null

    private var glProgram = 0
    private var mPositionHandle = 0
    private var mColorHandle = 0

    private var screenRate = 0.0f  //屏幕宽高比

    private var triangleCoors = floatArrayOf()

    private val color = floatArrayOf(
        1.0f, 0.0f, 0.0f, 1.0f, //RGBA
    )

    override fun onSurfaceCreated() {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)

        glProgram = loadProgram(vertexShader, fragmentShader)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        screenRate = width.toFloat() / height.toFloat()
        updatePosition(0f)
    }

    override fun onDrawFrame() {
        GLES20.glUseProgram(glProgram)

        mPositionHandle = GLES20.glGetAttribLocation(glProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, GROUP_COUNT, GLES20.GL_FLOAT, false, 0, positionVertexBuffer)

        mColorHandle = GLES20.glGetUniformLocation(glProgram, "vColor")
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, (triangleCoors.size / GROUP_COUNT))

        GLES20.glDisableVertexAttribArray(mPositionHandle)

        Log.i(TAG, "onDrawFrame glProgram=$glProgram mPositionHandle=$mPositionHandle  mColorHandle=$mColorHandle")
    }

    override fun updatePosition(displacement: Float) {
        triangleCoors = floatArrayOf(
            -0.5f, 0f * screenRate + displacement, 0.0f,
            0.5f, 0f * screenRate + displacement, 0.0f,
            0f, 0.5f * screenRate + displacement, 0.0f,
        )

        positionVertexBuffer = ByteBuffer.allocateDirect(triangleCoors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        positionVertexBuffer?.put(triangleCoors)?.position(0)
    }

    override fun updateSize(size: Float) {
        triangleCoors = floatArrayOf(
            0f, 0.5f * screenRate, 0.0f,
            -0.5f, 0f * screenRate, 0.0f,
            0.5f, 0f * screenRate, 0.0f,
        ).map { item -> item * size }.toFloatArray()

        positionVertexBuffer = ByteBuffer.allocateDirect(triangleCoors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        positionVertexBuffer?.put(triangleCoors)?.position(0)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun loadProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        Log.i(TAG, "vertexShader=$vertexShader  fragmentShader=$fragmentShader  program=$program")
        return program
    }
}