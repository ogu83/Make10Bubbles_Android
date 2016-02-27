package org.andengine.util.adt.queue;

import org.andengine.util.adt.list.IList;

/**
 * (c) Zynga 2012
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 15:00:30 - 24.01.2012
 */
public interface IQueue<T> extends IList<T> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	T peek();
	T poll();
	void enter(final T pItem);
	void enter(final int pIndex, final T pItem) throws IndexOutOfBoundsException;
}
