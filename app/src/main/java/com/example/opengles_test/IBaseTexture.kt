package com.example.opengles_test

/**
 * CreateBy:Joker
 * CreateTime:2021/7/20 18:37
 * description：
 */
interface IBaseTexture {

    fun onSurfaceCreated()

    fun onSurfaceChanged(width: Int, height: Int)

    fun onDrawFrame()

    fun updatePosition(displacement: Float)  //移动三角形位置

    fun updateSize(size: Float) //改变三角形大小
}