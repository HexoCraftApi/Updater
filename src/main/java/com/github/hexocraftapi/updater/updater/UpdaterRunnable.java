package com.github.hexocraftapi.updater.updater;

/*
 * Copyright 2016 hexosse
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
