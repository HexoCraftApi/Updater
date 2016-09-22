package com.github.hexocraftapi.updater.updater.utils;

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

import org.bukkit.Bukkit;

import java.util.logging.Level;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class LoggerUtils
{
	public static boolean verbose = false;

	/**
	 * This class should not normally be instantiated.
	 */
	private LoggerUtils() {}


	public static void log(Level level, String message)
	{
		if(verbose)
		{
			Bukkit.getServer().getLogger().log(level, "[Updater] " + message);
		}
	}

	public static void log(Level level, String message, Throwable error)
	{
		if(verbose)
			Bukkit.getServer().getLogger().log(level, "[Updater] " + message, error);
	}

}
