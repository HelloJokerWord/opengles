package com.example.opengles_test;

/**
 * GLES里用到的用于渲染的一个接口。
 * 如果是GLSurfaceView要用到，则其对应的GLSurfaceView.Renderer可以来调用IGLESRenderer的实现类来实现逻辑
 * 如果是TextureView要用到，则使用自定义的一个线程里调用IGLESRenderer的实现类来做一个类似于GLSurfaceView.Renderer的操作
 * 所以IGLESRenderer中的方法都要在GL线程里运行（TextureView创建一个线程，把它当做一个GL线程）
 *
 */
public interface IGLRenderer {
    /**
     * Surface创建好之后
     */
    void onSurfaceCreated();

    /**
     * Surface创建好之后
     */
    void onSurfaceCreateError();

    /**
     * 界面大小有更改
     */
    void onSurfaceChanged(int width, int height);

    /**
     * 绘制每一帧
     */
    boolean onDrawFrame();

    /**
     * Activity的onDestroy时的操作
     */
    void onDestroy();
}
