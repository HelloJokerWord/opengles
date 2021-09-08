package com.example.opengles_test.texture

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.example.opengles_test.IBaseTexture
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * CreateBy:Joker
 * CreateTime:2021/7/22 15:46
 * description：
 */
class CubeTexture : IBaseTexture {

    companion object {
        private val TAG = CubeTexture::class.java.simpleName

        private const val VERTEX_SHADER_CODE =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 aColor;" +
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "  vColor=aColor;" +
                    "}"

        private const val FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"

        private const val GROUP_COUNT = 3  //3个坐标一组 xyz
    }

    private var glProgram = 0

    private var mPositionHandle = 0
    private var mColorHandle = 0
    private var mMatrixHandler = 0

    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    private var bufferPosition: FloatBuffer? = null
    private var cubePositions = floatArrayOf(
        -1.0f, 1.0f, 1.0f,     //正面左上0
        -1.0f, -1.0f, 1.0f,    //正面左下1
        1.0f, -1.0f, 1.0f,     //正面右下2
        1.0f, 1.0f, 1.0f,      //正面右上3
        -1.0f, 1.0f, -1.0f,    //反面左上4
        -1.0f, -1.0f, -1.0f,   //反面左下5
        1.0f, -1.0f, -1.0f,    //反面右下6
        1.0f, 1.0f, -1.0f,     //反面右上7
    )

    private var bufferColor: FloatBuffer? = null
    private val cubeColor = floatArrayOf(
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
        1f, 0f, 0f, 1f,
    )

    private var bufferIndex: ShortBuffer? = null
    private val cubeIndex = shortArrayOf(
        6, 7, 4, 6, 4, 5,    //后面
        6, 3, 7, 6, 2, 3,    //右面
        6, 5, 1, 6, 1, 2,    //下面
        0, 3, 2, 0, 2, 1,    //正面
        0, 1, 5, 0, 5, 4,    //左面
        0, 7, 3, 0, 4, 7,    //上面
    )

    override fun onSurfaceCreated() {
        bufferPosition = ByteBuffer.allocateDirect(cubePositions.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        bufferPosition?.put(cubePositions)?.position(0)

        bufferColor = ByteBuffer.allocateDirect(cubeColor.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        bufferColor?.put(cubeColor)?.position(0)

        bufferIndex = ByteBuffer.allocateDirect(cubeIndex.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        bufferIndex?.put(cubeIndex)?.position(0)

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

        mPositionHandle = GLES20.glGetAttribLocation(glProgram, "vPosition")
        mColorHandle = GLES20.glGetAttribLocation(glProgram, "aColor")
        mMatrixHandler = GLES20.glGetUniformLocation(glProgram, "vMatrix")

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        val ratio = width.toFloat() / height.toFloat()
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 5.0f, 5.0f, 10.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
    }

    override fun onDrawFrame() {
        GLES20.glUseProgram(glProgram)

        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)

        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, GROUP_COUNT, GLES20.GL_FLOAT, false, 0, bufferPosition)

        GLES20.glEnableVertexAttribArray(mColorHandle)
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, bufferColor)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, cubeIndex.size, GLES20.GL_UNSIGNED_SHORT, bufferIndex)

        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mColorHandle)

        Log.i(TAG, "onDrawFrame glProgram=$glProgram mPositionHandle=$mPositionHandle  mColorHandle=$mColorHandle  mMatrixHandler=$mMatrixHandler")
    }

    override fun updatePosition(displacement: Float) {
    }

    override fun updateSize(size: Float) {
    }
}