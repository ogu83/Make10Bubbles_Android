package com.oogames.make10bubbles;

import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.RotationByModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

/**
 * Created by oguzkoroglu on 28/02/16.
 */
public class NumberBubble extends AnimatedSprite {
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
        setZIndex(1);
        Debug.d(String.format("Bubble added to scene, no: %d, radius: %s", no, radius));
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (pSceneTouchEvent.isActionDown()) {
            if (!MainActivity.Instance.gamePaused && !MainActivity.Instance.gameOver) {
                SetSelected(!IsSelected);
                MainActivity.Instance.ExplodeBubbles();
                MainActivity.Instance.hintCountDown = MainActivity.Instance.nextLevelScore / 20;
                setHint(false);
                playClickSound();
            }
            return true;
        }
        else
            return false;
    }

    public void SetSelected(boolean isSelected) {
        Debug.d(String.format("Bubble No:%d Selected : %s", No, isSelected));
        IsSelected = isSelected;
        setCurrentTileIndex(isSelected ? 1 : 0);
    }

    public void ShrinkAction() {
        Debug.d(String.format("Bubble Exploding No: %d Radius: %s", No, Radius));
        final NumberBubble my = this;
        ScaleModifier modifier = new ScaleModifier(0.25f, 1f, 0.1f) {
            @Override
            protected void onModifierFinished(IEntity pItem) {
                super.onModifierFinished(pItem);
                MainActivity.Instance.CalculateScore(my);
                MainActivity.Instance.DrawScore();
                MainActivity.Instance.RemoveBubble(my);
                MainActivity.Instance.onExplode = false;
            }
        };
        registerEntityModifier(modifier);
    }

    public void setHint(boolean on) {
        if (on) {
            //RotationModifier r1 = new RotationModifier(0.1f,0,180);
            //registerEntityModifier(r1);
            ScaleModifier m1 = new ScaleModifier(0.1f, 1f, 110f/100f) {
                @Override
                protected void onModifierFinished(IEntity pItem) {
                    super.onModifierFinished(pItem);
                    ScaleModifier m1 = new ScaleModifier(0.1f, 1f, 100f/110f) {
                        @Override
                        protected void onModifierFinished(IEntity pItem) {
                            super.onModifierFinished(pItem);
                            ScaleModifier m1 = new ScaleModifier(0.1f, 1f, 110f/100f) {
                                @Override
                                protected void onModifierFinished(IEntity pItem) {
                                    super.onModifierFinished(pItem);
                                    ScaleModifier m1 = new ScaleModifier(0.1f, 1f, 100f/110f) {
                                        @Override
                                        protected void onModifierFinished(IEntity pItem) {
                                            super.onModifierFinished(pItem);
                                        }
                                    };
                                    registerEntityModifier(m1);
                                }
                            };
                            registerEntityModifier(m1);
                        }
                    };
                    registerEntityModifier(m1);
                }
            };
            registerEntityModifier(m1);
        }
    }

    public void playWarnSound() {
        Debug.d("Playing Warn Sound");
        //TODO:Play warn sound
    }

    public void playWhoopSound() {
        Debug.d("Playing Whoop Sound");
        //TODO:Play whoop sound
    }

    public void playClickSound() {
        Debug.d("Playing Click Sound");
        //TODO:Play Click Sound
    }

    public void playHitSound() {
        Debug.d("Playing Hit Sound");
        //TODO:Play Hit Sound
    }

    public int No;
    public float Radius;
    public boolean IsSelected;
    public boolean IsRemoved;
    public Body Body;
}
