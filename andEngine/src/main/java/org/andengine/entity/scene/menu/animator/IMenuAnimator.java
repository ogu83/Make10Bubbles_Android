package org.andengine.entity.scene.menu.animator;

import java.util.ArrayList;

import org.andengine.entity.scene.menu.item.IMenuItem;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 10:50:36 - 02.04.2010
 */
public interface IMenuAnimator {
	// ===========================================================
	// Constants
	// ===========================================================

	IMenuAnimator DEFAULT = new AlphaMenuAnimator();

	// ===========================================================
	// Methods
	// ===========================================================

	void prepareAnimations(final ArrayList<IMenuItem> pMenuItems, final float pCameraWidth, final float pCameraHeight);
	void buildAnimations(final ArrayList<IMenuItem> pMenuItems, final float pCameraWidth, final float pCameraHeight);
}
