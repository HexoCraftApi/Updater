package com.github.hexocraftapi.updater.updater;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
 */
public interface UpdateCallback {
	/**
	 * Called when the updater has finished working.
	 * @param updater The updater instance
	 */
	void onFinish(Updater updater);
}

