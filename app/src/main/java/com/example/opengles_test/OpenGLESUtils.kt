package com.example.opengles_test

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log

/**
 * CreateBy:Joker
 * CreateTime:2021/7/22 17:34
 * description：
 */
object OpenGLESUtils {

    private val TAG = OpenGLESUtils::class.java.simpleName
    const val NO_TEXTURE = -1

    fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertex = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertex == 0) return 0
        val fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragment == 0) return 0
        var program = GLES20.glCreateProgram()
        if (program != 0) {
            GLES20.glAttachShader(program, vertex)
            GLES20.glAttachShader(program, fragment)
            GLES20.glLinkProgram(program)
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program:" + GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    private fun loadShader(shaderType: Int, source: String?): Int {
        var shader = GLES20.glCreateShader(shaderType)
        if (0 != shader) {
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader:$shaderType")
                Log.e(TAG, "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    fun loadImgTexture(image: Bitmap?, isRecycle: Boolean): Int {
        var textureId = NO_TEXTURE
        if (image == null || image.isRecycled) {
            return NO_TEXTURE
        } else {
            try {
                var hasAddPaddingX = false
                var hasAddPaddingY = false
                var resizedBitmap: Bitmap? = null

                //奇数宽度的时候需要做这个处理， 原因是因为某些机型如果存在奇数宽度生成纹理后绘制出来完全不正确
                if (image.width % 2 == 1) hasAddPaddingX = true
                if (image.height % 2 == 1) hasAddPaddingY = true

                if (hasAddPaddingX || hasAddPaddingY) {
                    resizedBitmap = Bitmap.createBitmap(image.width + if (hasAddPaddingX) 1 else 0, image.height + if (hasAddPaddingY) 1 else 0, Bitmap.Config.ARGB_8888)
                    val can = Canvas(resizedBitmap)
                    val paint = Paint()
                    paint.isAntiAlias = true
                    can.drawBitmap(image, 0f, 0f, paint)
                }

                val resized = resizedBitmap != null
                textureId = loadTexture(if (resized) resizedBitmap else image, NO_TEXTURE, isRecycle)
                resizedBitmap?.recycle()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return textureId
    }

    private fun loadTexture(bitmap: Bitmap?, usedTexId: Int, isRecycle: Boolean): Int {
        return if (bitmap == null || bitmap.isRecycled) {
            NO_TEXTURE
        } else {
            val textures = IntArray(1)
            if (usedTexId == NO_TEXTURE) {
                //生成纹理
                GLES20.glGenTextures(1, textures, 0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
                //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat())
                //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat())
                //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat())
                //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat())
                //根据以上指定的参数，生成一个2D纹理
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            } else {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId)
                GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap)
                textures[0] = usedTexId
            }

            if (isRecycle) bitmap.recycle()

            textures[0]
        }
    }

}