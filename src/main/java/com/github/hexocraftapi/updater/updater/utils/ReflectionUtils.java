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

import com.github.hexocraftapi.updater.updater.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class ReflectionUtils
{
	/**
	 * This class should not normally be instantiated.
	 */
	private ReflectionUtils() {}


	public static void run(Updater updater)
	{
		try
		{
			Class<?> clazz = updater.getClass();
			while(clazz != null)
			{
				Method[] methods = clazz.getDeclaredMethods();
				for(Method method : methods)
				{
					if(method.getName().equals("run"))
					{
						method.setAccessible(true);
						method.invoke(updater);
						method.setAccessible(false);
						return;
					}
				} clazz = clazz.getSuperclass();
			}
		}
		catch(IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	public static File getPluginFile(JavaPlugin plugin)
	{
		File file = null;

		try {
			Method method =JavaPlugin.class.getDeclaredMethod("getFile");
			method.setAccessible(true);
			file = (File)method.invoke(plugin);
			method.setAccessible(false);
		}
		catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return file;
	}
}
