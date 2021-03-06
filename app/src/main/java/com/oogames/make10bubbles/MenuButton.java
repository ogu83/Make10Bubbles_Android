package com.oogames.make10bubbles;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by oguzkoroglu on 29/02/16.
 */
public class MenuButton extends Sprite  {
    public static TextureRegion PlayButtonTextureRegion;
    public static TextureRegion InfoButtonTextureRegion;
    public static TextureRegion HighScoreButtonTextureRegion;
    public static TextureRegion ReviewButtonTextureRegion;
    public static TextureRegion ExitButtonTextureRegion;

    public static TextureRegion TwitterButtonTextureRegion;
    public static TextureRegion FacebookButtonTextureRegion;

    public static TiledTextureRegion SoundButtonTextureRegion;
    public static TiledTextureRegion PlayPauseButtonTextureRegion;
    public static TextureRegion GotoMenuButtonTextureRegion;

    public MenuButton(float pX, float pY, TextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
    }

    public static float buttonScaleRatio()
    {
        return 1.0f / 1.0f;
    }
}

