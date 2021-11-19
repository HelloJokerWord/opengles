package com.example.opengles_test.texture

import android.content.res.Resources
import android.graphics.Bitmap
import android.opengl.GLES20
import com.example.opengles_test.IBaseTexture
import com.example.opengles_test.OpenGLESUtils
import com.example.opengles_test.bean.RedPackageFallingBean
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*

/**
 * CreateBy:Joker
 * CreateTime:2021/8/18 16:14
 * description：
 */
class RedPacketRainTexture : IBaseTexture {

    companion object {
        private val TAG = RedPacketRainTexture::class.java.simpleName

        private const val VERTEX_SHADER_CODE =
            "attribute vec4 a_position;\n" +
                    "attribute vec4 a_color;\n" +
                    "attribute float a_pointSize;\n" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_texCoord;\n" +

                    "void main()\n" +
                    "{\n" +
                    "    gl_Position = vec4(a_position.xyz , 1.0);\n" +
                    "    v_color = a_color;\n" +
                    "    gl_PointSize = a_pointSize;\n" +
                    "}\n"

        private const val FRAGMENT_SHADER_CODE =
            "precision mediump float;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture0;\n" +
                    "void main()\n" +
                    "{\n" +
                    "    gl_FragColor = v_color * texture2D(u_texture0, gl_PointCoord);\n" +
                    "}\n"

    }

    var bitmap: Bitmap? = null
    private var imgSize = 0             //图片大小
    private var speedX = 0f             //每刷新一帧左右滑动像素
    private var speedY = 0f             //每刷新一帧下滑多少个像素
    private val mCurrentAllah = 1.0f
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var needCreateImg = true
    private val mRandom = Random()
    private var starIndexX = 0
    private var beforeImgCount = 0
    private var mRedPackageFallingBeans = mutableListOf<RedPackageFallingBean>()

    private var mSnowfallProgramId = 0

    private var gAPositionHandle = 0
    private var gAColorHandle = 0
    private var gAPointSizeHandle = 0
    private var gUTexture0Handle = 0

    private var gPos: FloatArray? = null
    private var gCol: FloatArray? = null
    private var gSize: FloatArray? = null

    private var mGLPosBuffer: FloatBuffer? = null
    private var mGLColBuffer: FloatBuffer? = null
    private var mGLSize: FloatBuffer? = null

    private var mTextureId: Int = OpenGLESUtils.NO_TEXTURE

    init {
        starIndexX = mRandom.nextInt(3)
        speedX = 0f
        speedY = dp2px(6f).toFloat()
    }

    override fun onSurfaceCreated() {
        imgSize = bitmap?.width ?: 0

        mSnowfallProgramId = OpenGLESUtils.createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE)
        gAPositionHandle = GLES20.glGetAttribLocation(mSnowfallProgramId, "a_position")
        gAColorHandle = GLES20.glGetAttribLocation(mSnowfallProgramId, "a_color")
        gAPointSizeHandle = GLES20.glGetAttribLocation(mSnowfallProgramId, "a_pointSize")
        gUTexture0Handle = GLES20.glGetUniformLocation(mSnowfallProgramId, "u_texture0")

        mTextureId = OpenGLESUtils.loadImgTexture(bitmap, true)
        if (mTextureId == OpenGLESUtils.NO_TEXTURE) {
            onDestroy()
        }
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        mScreenWidth = width
        mScreenHeight = height
        needCreateImg = true
        mRedPackageFallingBeans.clear()
        mRedPackageFallingBeans.add(createNewImgFallingBean())
    }

    override fun onDrawFrame() {
        if (mScreenWidth == 0 || mScreenHeight == 0 || mTextureId == OpenGLESUtils.NO_TEXTURE) return
        if (mCurrentAllah <= 0f) {
            onDestroy()
            return
        }

        updatePosition()
        bindVerticalDatas()

        GLES20.glUseProgram(mSnowfallProgramId)

        GLES20.glVertexAttribPointer(gAPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mGLPosBuffer)
        GLES20.glEnableVertexAttribArray(gAPositionHandle)

        GLES20.glVertexAttribPointer(gAColorHandle, 4, GLES20.GL_FLOAT, false, 0, mGLColBuffer)
        GLES20.glEnableVertexAttribArray(gAColorHandle)

        GLES20.glVertexAttribPointer(gAPointSizeHandle, 1, GLES20.GL_FLOAT, false, 0, mGLSize)
        GLES20.glEnableVertexAttribArray(gAPointSizeHandle)

        GLES20.glEnable(GLES20.GL_BLEND)

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        if (mTextureId != OpenGLESUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId)
            GLES20.glUniform1i(gUTexture0Handle, 0)
        }
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, mRedPackageFallingBeans.size)

        GLES20.glDisableVertexAttribArray(gAPositionHandle)
        GLES20.glDisableVertexAttribArray(gAColorHandle)
        GLES20.glDisableVertexAttribArray(gAPointSizeHandle)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        GLES20.glDisable(GLES20.GL_BLEND)
    }

    override fun updatePosition(displacement: Float) {}

    override fun updateSize(size: Float) {}

    private fun updatePosition() {
        val halfWidth = mScreenWidth / 2
        val halfHeight = mScreenHeight / 2
        val starDY = imgSize * 1.0f / 2 / halfHeight
        val newRedPackageFallingBeans = ArrayList<RedPackageFallingBean>()

        for (i in mRedPackageFallingBeans.indices) {
            var bean = mRedPackageFallingBeans[i]
            newRedPackageFallingBeans.add(bean)

            bean.pointCenterX -= speedX * 1.0f / halfWidth
            bean.pointCenterY -= speedY * 1.0f / halfHeight
            bean.size = imgSize
            bean.alpha = mCurrentAllah

            //第一个滑出屏幕，则不再生成新图片
            if (i == 0 && bean.pointCenterY < -1 - starDY && needCreateImg) {
                needCreateImg = false
            }

            when {
                //复用滑出屏幕的那一个图片
                !needCreateImg && bean.pointCenterY < -1 - starDY -> {
                    bean = createNewImgFallingBean()
                    newRedPackageFallingBeans[i] = bean
                }
                needCreateImg && i == mRedPackageFallingBeans.size - 1 && bean.pointCenterY <= 1 - 2 * starDY -> {
                    newRedPackageFallingBeans.add(createNewImgFallingBean())
                }
            }
        }

        mRedPackageFallingBeans = newRedPackageFallingBeans
    }

    private fun bindVerticalDatas() {
        //避免每次都去重新申请内存
        if (beforeImgCount != mRedPackageFallingBeans.size) {
            gPos = FloatArray(mRedPackageFallingBeans.size * 2)
            gCol = FloatArray(mRedPackageFallingBeans.size * 4)
            gSize = FloatArray(mRedPackageFallingBeans.size)

            mGLPosBuffer = ByteBuffer.allocateDirect(gPos!!.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            mGLColBuffer = ByteBuffer.allocateDirect(gCol!!.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            mGLSize = ByteBuffer.allocateDirect(gSize!!.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        }

        for (i in mRedPackageFallingBeans.indices) {
            val redPackageFallingBean = mRedPackageFallingBeans[i]

            gPos!![i * 2] = redPackageFallingBean.pointCenterX
            gPos!![i * 2 + 1] = redPackageFallingBean.pointCenterY

            gCol!![i * 4] = 1f
            gCol!![i * 4 + 1] = 1f
            gCol!![i * 4 + 2] = 1f
            gCol!![i * 4 + 3] = redPackageFallingBean.alpha

            gSize!![i] = redPackageFallingBean.size.toFloat()
        }

        mGLPosBuffer!!.clear()
        mGLColBuffer!!.clear()
        mGLSize!!.clear()

        mGLPosBuffer!!.put(gPos).position(0)
        mGLColBuffer!!.put(gCol).position(0)
        mGLSize!!.put(gSize).position(0)

        beforeImgCount = mRedPackageFallingBeans.size
    }

    private fun createNewImgFallingBean(): RedPackageFallingBean {
        val halfWidth = mScreenWidth * 1.0f / 2
        val halfHeight = mScreenHeight * 1.0f / 2
        val starDY = imgSize * 1.0f / 2 / halfHeight

        //x轨道分成3个轨道
        val totalLength = mScreenWidth - imgSize
        val perLength = totalLength * 1.0f / 3
        var firstPoint = imgSize * 1.0f / 2
        var secondPoint = firstPoint + perLength
        var thirdPoint = secondPoint + perLength
        var fourPoint = thirdPoint + perLength

        //归一化
        firstPoint = (firstPoint - halfWidth) / halfWidth
        secondPoint = (secondPoint - halfWidth) / halfWidth
        thirdPoint = (thirdPoint - halfWidth) / halfWidth
        fourPoint = (fourPoint - halfWidth) / halfWidth

        //随机X
        val pointCenterX = when (starIndexX++ % 3) {
            0 -> nextFloat(firstPoint, secondPoint)
            1 -> nextFloat(secondPoint, thirdPoint)
            else -> nextFloat(thirdPoint, fourPoint)
        }
        val pointCenterY = 1 + starDY

        return RedPackageFallingBean(pointCenterX, pointCenterY, mCurrentAllah, imgSize)
    }

    private fun onDestroy() {
        if (mTextureId != OpenGLESUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, intArrayOf(mTextureId), 0)
            mTextureId = OpenGLESUtils.NO_TEXTURE
        }

        if (mSnowfallProgramId > 0) {
            GLES20.glDeleteProgram(mSnowfallProgramId)
            mSnowfallProgramId = 0
        }
    }

    private fun nextFloat(min: Float, max: Float): Float {
        val distance = max - min
        val r = mRandom.nextFloat()
        return min + distance * r
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}