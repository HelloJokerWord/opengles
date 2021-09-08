package com.example.opengles_test

import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.example.opengles_test.texture.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val triangleTexture = TriangleTexture()
        val squareTexture = SquareTexture()
        val circleTexture = CircleTexture()
        val cubeTexture = CubeTexture()
        val imageTexture = ImageTexture()
        imageTexture.bitmap = BitmapFactory.decodeResource(resources, R.drawable.nn_bg_gift_receive_fail)

        val render = BaseRender(cubeTexture)
        gl_surface_view.apply {
            setEGLContextClientVersion(2)
            setRenderer(render)
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }


        btn_change_position.setOnClickListener {
            gl_surface_view.apply {
                queueEvent {
                    render.updatePosition(0.1f)
                }

                requestRender()
            }
        }

        btn_change_size.setOnClickListener {
            gl_surface_view.apply {
                queueEvent {
                    render.updateSize(0.5f)
                }

                requestRender()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gl_surface_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        gl_surface_view.onPause()
    }
}