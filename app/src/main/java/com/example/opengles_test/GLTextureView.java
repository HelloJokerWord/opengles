package com.example.opengles_test;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;


public class GLTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    public static final String TAG = "BaseGLESTextureView";

    protected GLHandlerThread mGLThread;
    protected WeakHandler mGlHandler;
    private boolean isInit = false;
    protected CopyGlRender copyGlRender;

    public GLTextureView(Context context) {
        this(context, null);
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSurfaceTextureListener(this);
        setOpaque(false);
    }

    public void requestRender() {
        if (isInit() && mGlHandler != null && mGLThread != null) {
            mGlHandler.sendEmptyMessage(GLHandlerThread.MSG_DRAWFRAM);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable :  " + surface + "  width  : " + width + "   height : " + height);
        isInit = true;
        mGLThread = new GLHandlerThread(copyGlRender);// 创建一个线程，作为GL线程
        mGLThread.start();

        mGlHandler = new WeakHandler(mGLThread.getLooper(), mGLThread);
        mGLThread.updateSurfaceTexture(surface);

        mGlHandler.sendEmptyMessage(GLHandlerThread.MSG_INIT);

        int[] ints = {width, height};
        Message obtain = Message.obtain();
        obtain.what = GLHandlerThread.MSG_ONSIZECHANGE;
        obtain.obj = ints;
        mGlHandler.sendMessage(obtain);
        requestRender();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        releaseGl();
        return true;//true 系统自己回收SurfaceTexture， false 需要自己主动回收
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged :  " + surface + "  width  : " + width + "   height : " + height);
        if (isInit() && mGlHandler != null && mGLThread != null) {
            Message obtain = Message.obtain();
            int[] ints = {width, height};
            obtain.what = GLHandlerThread.MSG_ONSIZECHANGE;
            obtain.obj = ints;
            mGlHandler.sendMessage(obtain);
        }
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureUpdated :  " + surface);
    }

    public void onDestroy() {
        releaseGl();
        setSurfaceTextureListener(null);
    }

    protected void releaseGl() {
        if (isInit() && mGlHandler != null && mGLThread != null && mGLThread.isAlive()) {
            mGlHandler.sendEmptyMessage(GLHandlerThread.MSG_ONDESTROY);
        }
        isInit = false;
    }

    public boolean isInit() {
        return isInit;
    }

}
