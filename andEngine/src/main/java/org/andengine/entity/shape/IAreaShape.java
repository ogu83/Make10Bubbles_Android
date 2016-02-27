package org.andengine.entity.shape;


/**
 * (c) Zynga 2011
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 19:01:16 - 07.08.2011
 */
public interface IAreaShape extends IShape {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	float getWidth();

	void setWidth(final float pWidth);

	float getHeight();

	void setHeight(final float pHeight);

	float getWidthScaled();

	float getHeightScaled();

	void setSize(final float pWidth, final float pHeight);
}
