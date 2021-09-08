package com.example.opengles_test.texture

import android.graphics.Bitmap
import com.example.opengles_test.IBaseTexture

/**
 * CreateBy:Joker
 * CreateTime:2021/8/18 16:14
 * description：
 */
class ImageTexture : IBaseTexture {

    companion object {
        private val TAG = ImageTexture::class.java.simpleName

        private const val VERTEX_SHADER_CODE =
            "attribute vec4 vPosition;" +
                    "attribute vec2 vCoordinate;" +
                    "uniform mat4 vMatrix;" +
                    "varying vec2 aCoordinate;" +
                    "varying vec4 aPos;" +
                    "varying vec4 gPosition;" +
                    "void main(){" +
                    "  gl_Position=vMatrix*vPosition;" +
                    "  aPos=vPosition;" +
                    "  aCoordinate=vCoordinate;" +
                    "  gPosition=vMatrix*vPosition;" +
                    "}"

        private const val FRAGMENT_SHADER_CODE =
            "precision mediump float;\n" +
                    "uniform sampler2D vTexture;\n" +
                    "uniform int vChangeType;\n" +
                    "uniform vec3 vChangeColor;\n" +
                    "uniform int vIsHalf;\n" +
                    "uniform float uXY;\n" +
                    "varying vec4 gPosition;\n" +
                    "varying vec2 aCoordinate;\n" +
                    "varying vec4 aPos;\n" +
                    "void modifyColor(vec4 color){\n" +
                    "    color.r=max(min(color.r,1.0),0.0);\n" +
                    "    color.g=max(min(color.g,1.0),0.0);\n" +
                    "    color.b=max(min(color.b,1.0),0.0);\n" +
                    "    color.a=max(min(color.a,1.0),0.0);\n" +
                    "}\n" +
                    "void main(){\n" +
                    "    vec4 nColor=texture2D(vTexture,aCoordinate);\n" +
                    "    if(aPos.x>0.0||vIsHalf==0){\n" +
                    "        if(vChangeType==1){\n" +
                    "            float c=nColor.r*vChangeColor.r+nColor.g*vChangeColor.g+nColor.b*vChangeColor.b;\n" +
                    "            gl_FragColor=vec4(c,c,c,nColor.a);\n" +
                    "        }else if(vChangeType==2){\n" +
                    "            vec4 deltaColor=nColor+vec4(vChangeColor,0.0);\n" +
                    "            modifyColor(deltaColor);\n" +
                    "            gl_FragColor=deltaColor;\n" +
                    "        }else if(vChangeType==3){\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.r,aCoordinate.y-vChangeColor.r));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.r,aCoordinate.y+vChangeColor.r));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.r,aCoordinate.y-vChangeColor.r));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.r,aCoordinate.y+vChangeColor.r));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.g,aCoordinate.y-vChangeColor.g));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.g,aCoordinate.y+vChangeColor.g));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.g,aCoordinate.y-vChangeColor.g));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.g,aCoordinate.y+vChangeColor.g));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.b,aCoordinate.y-vChangeColor.b));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x-vChangeColor.b,aCoordinate.y+vChangeColor.b));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.b,aCoordinate.y-vChangeColor.b));\n" +
                    "            nColor+=texture2D(vTexture,vec2(aCoordinate.x+vChangeColor.b,aCoordinate.y+vChangeColor.b));\n" +
                    "            nColor/=13.0;\n" +
                    "            gl_FragColor=nColor;\n" +
                    "        }else if(vChangeType==4){\n" +
                    "            float dis=distance(vec2(gPosition.x,gPosition.y/uXY),vec2(vChangeColor.r,vChangeColor.g));\n" +
                    "            if(dis<vChangeColor.b){\n" +
                    "                nColor=texture2D(vTexture,vec2(aCoordinate.x/2.0+0.25,aCoordinate.y/2.0+0.25));\n" +
                    "            }\n" +
                    "            gl_FragColor=nColor;\n" +
                    "        }else{\n" +
                    "            gl_FragColor=nColor;\n" +
                    "        }\n" +
                    "    }else{\n" +
                    "        gl_FragColor=nColor;\n" +
                    "    }\n" +
                    "}"

    }

    var bitmap: Bitmap? = null

    override fun onSurfaceCreated() {


    }

    override fun onSurfaceChanged(width: Int, height: Int) {
    }

    override fun onDrawFrame() {
    }

    override fun updatePosition(displacement: Float) {
    }

    override fun updateSize(size: Float) {
    }
}