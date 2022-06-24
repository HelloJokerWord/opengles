/*
 *
 * FGLRender.java
 *
 * Created by Wuwang on 2016/9/29
 */
package com.example.opengles_test;


import android.opengl.GLES20;

import com.example.opengles_test.texture.TriangleTexture;

import java.util.LinkedList;
import java.util.Queue;

public class CopyGlRender implements IGLRenderer {

    private final Queue<Runnable> mPreDrawTask = new LinkedList<>();//绘制之前的任务

    private TriangleTexture texture;

    @Override
    public void onSurfaceCreated() {
        texture.onSurfaceCreated();
    }

    @Override
    public void onSurfaceCreateError() {

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        //glViewport- 设置视口
        GLES20.glViewport(0, 0, width, height);
        texture.onSurfaceChanged(width, height);
    }

    @Override
    public boolean onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        runPreDrawTask();
        texture.onDrawFrame();
        return false;
    }

    private void addPreDrawTask(Runnable runnable) {
        synchronized (mPreDrawTask) {
            mPreDrawTask.add(runnable);
        }
    }

    private void runPreDrawTask() {
        synchronized (mPreDrawTask) {
            while (!mPreDrawTask.isEmpty()) {
                Runnable runnable = mPreDrawTask.poll();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }
    }

    public void onDestroy() {
        //Texture.onDestroy()
    }
}
