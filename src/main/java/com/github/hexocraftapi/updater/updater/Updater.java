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

import com.github.hexocraftapi.updater.updater.utils.LoggerUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

import static com.github.hexocraftapi.updater.updater.utils.LoggerUtils.log;

/**
 * Class that tries to find updates.
 *
 */
//@SuppressWarnings("unused")
public abstract class Updater {

	/**
	 * User agent to use when making requests, according to the API it's preferred if this is your username.
	 */
	public static String agent = "HexoCraft multi-updater";

	/**
	 * Do not update if the update contains one of this tags.
	 */
	protected static final String[] devTag = { "-DEV", "-PRE", "-SNAPSHOT" };

	/**
	 * Store the query result.
	 */
	protected Response result = Response.NO_UPDATE;

	/**
	 * Plugin running Updater.
	 */
	protected final JavaPlugin plugin;

	/**
	 * The provided callback (if any)
	 */
	protected final UpdateCallback callback;

	/**
	 * Store the <strong>latest</strong> update.
	 */
	protected Update update;

	/**
	 * Store the update passed in the constructor.
	 */
	protected Version current;

	/**
	 * Thread that does the heavy lifting.
	 */
	protected Thread thread;

	/**
	 * Create a new {@link Updater} using a {@link JavaPlugin} object.
	 *
	 * @param plugin Plugin running Updater
	 * @throws Exception error whilst parsing semver string
	 */
	public Updater(JavaPlugin plugin) throws Exception {
		this(plugin, false);
	}

	/**
	 * Create a new {@link Updater} using a {@link JavaPlugin}
	 *
	 * @param plugin Plugin running Updater
	 * @param verbose verbose
	 */
	public Updater(JavaPlugin plugin, boolean verbose)
	{
		Validate.notNull(plugin, "Plugin cannot be null!");

		Version version = Version.parse(plugin);

		Validate.notNull(version, "Provided update is not semver compliant!");

		this.plugin = plugin;
		this.current = version;
		this.callback = null;

		LoggerUtils.verbose = verbose;
	}

	/**
	 * Check for new update.
	 */
	private void run()
	{
		if(this.read() && this.checkVersion())
		{
			if((this.update == null))
				this.result = Response.NO_UPDATE;
		}

		if (this.callback != null)
		{
			new BukkitRunnable() {
				@Override
				public void run() {
					runCallback();
				}
			}.runTask(this.plugin);
		}
	}

	/**
	 * Check the repository for neweest file.
	 *
	 * @return true if successful.
	 */
	protected abstract boolean read();


	/**
	 * Check to see if the program should continue by evaluating whether the plugin is already updated, or shouldn't be updated.
	 *
	 * @return true if the update was located and is not the same as the remote's newest.
	 */
	protected boolean checkVersion()
	{
		if(this.update == null || isDev(this.update.getName()) || !this.shouldUpdate())
		{
			// We already have the latest update, or this build is tagged as development version
			this.result = Response.NO_UPDATE;
			return false;
		}

		return true;
	}

	private void exit(Response response) {
		if (response != Response.SUCCESS) {
			this.result = response;
			this.update = null;
		}
	}

	public Version getVersion()
	{
		log(Level.INFO, "Somebody queried the current version");
		waitForThread();
		if(current == null)
		{
			log(Level.INFO, "Current version is undefined !");
			return null;
		}

		return current;
	}

	public Update getUpdate()
	{
		log(Level.INFO, "Somebody queried the latest update");
		waitForThread();
		if(update == null)
		{
			log(Level.INFO, "Latest update is undefined!");
			return null;
		}

		return update;
	}

	/**
	 * @return {@link java.lang.String} the update that GitHub tells us about.
	 */
	public String getLatestVersion()
	{
		log(Level.INFO, "Somebody queried the latest update");
		waitForThread();
		if(update == null)
		{
			log(Level.INFO, "Latest update is undefined!");
			return "Please check #getResult()";
		}

		return update.getVersion().toString();
	}

	/**
	 * @return {@link Response}
	 */
	public Response getResult() {
		log(Level.INFO, "Somebody queried the update result");
		waitForThread();
		return this.result;
	}

	/**
	 * Try and wait for the thread to finish executing.
	 */
	protected void waitForThread()
	{
		if((this.thread != null) && this.thread.isAlive())
		{
			try
			{
				this.thread.join();
				log(Level.INFO, "Trying to join thread");
			}
			catch(final InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Evaluate whether the version number is marked as a development version.
	 *
	 * @param version a version number to check for tags in.
	 * @return true if updating should be disabled.
	 */
	protected boolean isDev(String version)
	{
		for(final String string : Updater.devTag)
		{
			if(version.contains(string))
				return true;
		}
		return false;
	}

	/**
	 * @return true if Updater should consider the remote version as an update, false if not.
	 */
	public boolean shouldUpdate() {
		return this.update==null ? false : this.current.isLower(this.update.getVersion());
	}

	private void runCallback() {
		this.callback.onFinish(this);
	}
}
