package com.github.hexocraftapi.updater.updater.utils;

import com.github.hexocraftapi.updater.updater.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
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
