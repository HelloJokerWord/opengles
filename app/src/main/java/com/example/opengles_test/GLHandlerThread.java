package com.example.opengles_test;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

public class GLHandlerThread extends HandlerThread implements Handler.Callback {
    public static final int MSG_INIT = 1;
    public static final int MSG_DRAWFRAM = 2;
    public static final int MSG_ONSIZECHANGE = 3;
    public static final int MSG_FRAMSHOT = 4;
    public static final int MSG_ONDESTROY = 5;

    private SurfaceTexture mSurfaceTexture;
    private final IGLRenderer mRenderer;
    private DrawGiftEglCore mDrawGiftEglCore;
    private int mWidth;
    private int mHeight;

    public GLHandlerThread(IGLRenderer renderer) {
        super("GLESHandlerThread");
        mRenderer = renderer;
    }

    public void updateSurfaceTexture(SurfaceTexture surface) {
        mSurfaceTexture = surface;
    }

    private void drawFram() {
        boolean isLastFrame = false;
        while (!isLastFrame) {
            if (mRenderer != null) {
                isLastFrame = mRenderer.onDrawFrame();// 绘制
            }
            // 一帧完成之后，调用eglSwapBuffers(EGLDisplay dpy, EGLContext ctx)来显示
            if (mDrawGiftEglCore != null) {
                mDrawGiftEglCore.swapBuffer();
            }
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case MSG_INIT:
                boolean isSuccess = createEglContext();
                if (isSuccess) {
                    if (mRenderer != null) {
                        mRenderer.onSurfaceCreated();
                    }
                } else {
                    if (mRenderer != null) {
                        mRenderer.onSurfaceCreateError();
                    }
                }

                break;
            case MSG_ONSIZECHANGE:
                int[] obj = (int[]) msg.obj;
                mWidth = obj[0];
                mHeight = obj[1];
                if (mRenderer != null) {
                    mRenderer.onSurfaceChanged(mWidth, mHeight);
                }
                break;
            case MSG_DRAWFRAM:
                drawFram();
                break;
            case MSG_ONDESTROY:
                onDestroy();
                break;
        }
        return true;
    }

    private void onDestroy() {
        if (mRenderer != null) {
            mRenderer.onDestroy();
        }
        if (mDrawGiftEglCore != null) {
            mDrawGiftEglCore.destoryGLESContext();
            mDrawGiftEglCore = null;
            mSurfaceTexture = null;
            quitSafely();
        }
    }

    private boolean createEglContext() {
        try {
            if (mSurfaceTexture != null && mDrawGiftEglCore == null) {
                mDrawGiftEglCore = new DrawGiftEglCore(mSurfaceTexture);
            } else {
                mDrawGiftEglCore.destoryGLESContext();
                if (mSurfaceTexture != null) {
                    mDrawGiftEglCore.createEglContext(mSurfaceTexture);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
