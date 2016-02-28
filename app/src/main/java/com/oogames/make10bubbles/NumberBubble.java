package com.oogames.make10bubbles;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

/**
 * Created by oguzkoroglu on 28/02/16.
 */
public class NumberBubble extends AnimatedSprite {
    //public static ArrayList<TextureRegion> nTextureRegion;
    //public static ArrayList<TextureRegion> nSTextureRegion;
    public static ArrayList<ITiledTextureRegion> nTTextureRegions;

    public NumberBubble(float pX, float pY, ITiledTextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
    }

    public NumberBubble(float pX, float pY, ITiledTextureRegion pTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager, int no, float radius) {
        super(pX,pY,pTextureRegion,pVertexBufferObjectManager);
        No = no;
        Radius = radius;
        setWidth(radius);
        setHeight(radius);
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        //Debug.d("Bubble Touched");
        if (pSceneTouchEvent.isActionDown()) {
            SetSelected(!IsSelected);
            return true;
        }
        else
            return false;
        //return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
    }

    public void SetSelected(boolean isSelected) {
        Debug.d(String.format("No : %d Bubble Selected : %s", No, isSelected));
        IsSelected = isSelected;
        setCurrentTileIndex(isSelected ? 1 : 0);
    }

    public int No;
    public float Radius;
    public boolean IsSelected;
    public boolean IsRemoved;
}
