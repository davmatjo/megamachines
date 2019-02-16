package com.battlezone.megamachines.renderer;

import com.battlezone.megamachines.math.Matrix4f;

public class SubTexture implements Texture {

    private Matrix4f position;

    public SubTexture(Matrix4f position) {
        this.position = position;
    }

    @Override
    public void bind() {
        Shader.STATIC.setMatrix4f("texturePosition", position);
    }

    public void setPosition(Matrix4f position) {
        this.position = position;
    }
}
