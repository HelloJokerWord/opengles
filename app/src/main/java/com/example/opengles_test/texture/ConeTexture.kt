package com.example.opengles_test.texture

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import com.example.opengles_test.IBaseTexture
import com.example.opengles_test.OpenGLESUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * CreateBy:Joker
 * CreateTime:2021/7/22 17:01
 * description：
 */
class ConeTexture : IBaseTexture {

    companion object {
        private val TAG = ConeTexture::class.java.simpleName

        private const val VERTEX_SHADER_CODE =
            "uniform mat4 vMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 vPosition;" +
                    "void main(){" +
                    "  gl_Position=vMatrix*vPosition;" +
                    "  if(vPosition.z!=0.0){" +
                    "      vColor=vec4(0.0,0.0,0.0,1.0);" +
                    "  }else{" +
                    "      vColor=vec4(0.9,0.9,0.9,1.0);" +
                    "  }" +
                    "}"

        private const val FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "void main(){" +
                    "  gl_FragColor=vColor;" +
                    "}"

    }

    private var glProgram = 0
    private var matrixHandle = 0
    private var mPositionHandle = 0
    private var mColorHandle = 0

    private val mProjectMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)

    private val colors = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

    private val n = 360         //切割份数
    private val height = 2.0f   //圆锥高度
    private val radius = 1.0f   //圆锥底面半径

    private var vSize = 0

    private var bufferPosition: FloatBuffer? = null

    override fun onSurfaceCreated() {
        initPosition()
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        glProgram = OpenGLESUtils.createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE)

        matrixHandle = GLES20.glGetUniformLocation(glProgram, "vMatrix")
        mPositionHandle = GLES20.glGetAttribLocation(glProgram, "vPosition")
        mColorHandle = GLES20.glGetUniformLocation(glProgram, "vColor")
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        //计算宽高比
        val ratio = width.toFloat() / height.toFloat()
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
    }

    override fun onDrawFrame() {
        GLES20.glUseProgram(glProgram)

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mMVPMatrix, 0)

        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, bufferPosition)

        //GLES20.glEnableVertexAttribArray(colorHandle);
        //GLES20.glUniform4fv(colorHandle, 1, colors, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vSize)

        GLES20.glDisableVertexAttribArray(mPositionHandle)

        Log.i(TAG, "onDrawFrame glProgram=$glProgram mPositionHandle=$mPositionHandle  mColorHandle=$mColorHandle")
    }

    override fun updatePosition(displacement: Float) {
    }

    override fun updateSize(size: Float) {
    }

    private fun initPosition() {
        val pos = ArrayList<Float>()
        pos.add(0.0f)
        pos.add(0.0f)
        pos.add(height)

        val angDegSpan = 360f / n
        var i = 0f
        while (i < 360 + angDegSpan) {
            pos.add((radius * sin(i * Math.PI / 180f)).toFloat())
            pos.add((radius * cos(i * Math.PI / 180f)).toFloat())
            pos.add(0.0f)
            i += angDegSpan
        }
        val d = pos.toFloatArray()
        vSize = d.size / 3

        bufferPosition = ByteBuffer.allocateDirect(d.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        bufferPosition?.put(d)?.position(0)
    }
}
