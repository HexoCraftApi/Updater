package com.github.hexocraftapi.updater.updater;

import com.github.hexocraftapi.updater.updater.utils.ReflectionUtils;

/**
 * Simple solution to stop the main thread being blocked
 *
 * @author Connor Spencer Harries
 */
public class UpdaterRunnable implements Runnable {

	/**
	 * Store the parent {@link Updater} instance.
	 */
	private final Updater updater;

	/**
	 * Create a new {@link UpdaterRunnable} with an {@link Updater} as the parent.
	 *
	 * @param parent instance of {@link Updater}
	 */
	public UpdaterRunnable(Updater parent) {
		this.updater = parent;
	}

	/**
	 * Use reflection to invoke the run method on {@link Updater}
	 */
	@Override
	public void run()
	{
		ReflectionUtils.run(updater);
	}
}
