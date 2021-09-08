package com.example.opengles_test

import android.annotation.SuppressLint
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.example.opengles_test.texture.CircleTexture
import com.example.opengles_test.texture.SquareTexture
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * CreateBy:Joker
 * CreateTime:2021/7/20 18:38
 * description：
 */
@SuppressLint("NewApi")
class BaseRender(private val baseTexture: IBaseTexture?) : GLSurfaceView.Renderer {

    companion object{
        private val TAG = BaseRender::class.java.simpleName
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(1f, 1f, 1f, 1f)
        baseTexture?.onSurfaceCreated()
        Log.i(TAG, "onSurfaceCreated")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        baseTexture?.onSurfaceChanged(width, height)
        Log.i(TAG, "onSurfaceChanged  width=$width  height=$height")
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        baseTexture?.onDrawFrame()
    }

    fun updateSize(size: Float) {
        baseTexture?.updateSize(size)
    }

    fun updatePosition(displacement: Float) {
        baseTexture?.updatePosition(displacement)
    }
}