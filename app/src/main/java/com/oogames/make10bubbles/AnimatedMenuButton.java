package com.oogames.make10bubbles;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by ADMIN on 02.03.2016.
 */
public class AnimatedMenuButton extends AnimatedSprite {
    public AnimatedMenuButton(float pX, float pY, TiledTextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
    }

    public static float buttonScaleRatio()
    {
        return 1.0f / 1.0f;
    }
}