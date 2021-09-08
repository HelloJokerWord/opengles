package com.example.opengles_test.texture

import android.opengl.GLES20
import android.util.Log
import com.example.opengles_test.IBaseTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * CreateBy:Joker
 * CreateTime:2021/7/22 10:17
 * description：
 */
class SquareTexture : IBaseTexture {

    companion object {
        private val TAG = SquareTexture::class.java.simpleName

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

    private var squareVertexBuffer: FloatBuffer? = null

    private var squarePosition = floatArrayOf()

    private val mColor = floatArrayOf(
        1f, 0f, 0f, 1f
    )

    private var screenRate = 0.0f

    private var glProgram = 0
    private var mPositionHandle = 0
    private var mColorHandle = 0

    override fun onSurfaceCreated() {
        //创建顶点着色器
        val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertexShader, VERTEX_SHADER_CODE)
        GLES20.glCompileShader(vertexShader)

        //创建片元着色器
        val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragmentShader, FRAGMENT_SHADER_CODE)
        GLES20.glCompileShader(fragmentShader)

        //创建GL程序 链接顶点和片元着色器
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
        //使用创建GL程序
        GLES20.glUseProgram(glProgram)

        //创建顶点着色器句柄
        mPositionHandle = GLES20.glGetAttribLocation(glProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, GROUP_COUNT, GLES20.GL_FLOAT, false, 0, squareVertexBuffer)

        //创建片元着色器句柄
        mColorHandle = GLES20.glGetUniformLocation(glProgram, "vColor")
        GLES20.glUniform4fv(mColorHandle, 1, mColor, 0)

        //创建绘制模式
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, squarePosition.size / GROUP_COUNT)

        //关闭顶点着色器
        GLES20.glDisableVertexAttribArray(mPositionHandle)

        Log.i(TAG, "onDrawFrame glProgram=$glProgram mPositionHandle=$mPositionHandle  mColorHandle=$mColorHandle")
    }

    override fun updatePosition(displacement: Float) {
        squarePosition = floatArrayOf(
            -0.5f, 0.5f * screenRate + displacement, 0f,
            -0.5f, -0.5f * screenRate + displacement, 0f,
            0.5f, -0.5f * screenRate + displacement, 0f,
            0.5f, 0.5f * screenRate + displacement, 0f,
        )

        squareVertexBuffer = ByteBuffer.allocateDirect(squarePosition.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        squareVertexBuffer?.put(squarePosition)?.position(0)
    }

    override fun updateSize(size: Float) {
        squarePosition = floatArrayOf(
            -0.5f, 0.5f * screenRate, 0f,
            -0.5f, -0.5f * screenRate, 0f,
            0.5f, -0.5f * screenRate, 0f,
            0.5f, 0.5f * screenRate, 0f,
        ).map { item -> item * size }.toFloatArray()

        squareVertexBuffer = ByteBuffer.allocateDirect(squarePosition.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        squareVertexBuffer?.put(squarePosition)?.position(0)
    }
}