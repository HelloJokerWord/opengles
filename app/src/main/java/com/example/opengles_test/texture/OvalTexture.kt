package com.example.opengles_test.texture

import android.opengl.Matrix
import com.example.opengles_test.IBaseTexture
import com.example.opengles_test.OpenGLESUtils

/**
 * CreateBy:Joker
 * CreateTime:2021/7/22 18:29
 * description：
 */
class OvalTexture : IBaseTexture {

    companion object {
        private val TAG = OvalTexture::class.java.simpleName

        private const val VERTEX_SHADER_CODE =
            "attribute vec4 vPosition;" +
                    "uniform mat4 vMatrix;" +
                    "void main() {" +
                    "  gl_Position = vMatrix*vPosition;" +
                    "}"

        private const val FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}"
    }

    private var glProgram = 0
    private val mMatrixHandler = 0
    private val mPositionHandle = 0
    private val mColorHandle = 0

    private val mViewMatrix = FloatArray(16)
    private val mProjectMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)


    override fun onSurfaceCreated() {

        glProgram = OpenGLESUtils.createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE)

    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        //计算宽高比
        val ratio = width.toFloat() / height
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
    }

    override fun onDrawFrame() {
    }

    override fun updatePosition(displacement: Float) {}

    override fun updateSize(size: Float) {}
}