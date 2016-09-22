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

import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
@SuppressWarnings("unused")
public class Update
{
	/**
	 * The plugin being updated.
	 */
	private final JavaPlugin plugin;

	/**
	 * Name.
	 */
	private final String name;

	/**
	 * Version available for download.
	 */
	private final Version version;

	/**
	 * Type of release.
	 */
	private final ReleaseType releaseType;

	/**
	 * Download link.
	 */
	private URL downloadUrl;

	/*
	 * update's game
	 */
	private String versionGame;

	/*
	 * Changes
	 */
	private String changes;



	public Update(JavaPlugin plugin, String name, Version version, ReleaseType releaseType)
	{
		this.plugin = plugin;
		this.name = name;
		this.version = version;
		this.releaseType = releaseType;
	}



	public JavaPlugin getPlugin()
	{
		return plugin;
	}

	public String getName()
	{
		return name;
	}

	public Version getVersion()
	{
		return version;
	}

	public ReleaseType getReleaseType()
	{
		return releaseType;
	}

	public void setDownloadUrl(URL downloadUrl)
	{
		this.downloadUrl = downloadUrl;
	}

	public URL getDownloadUrl()
	{
		return downloadUrl;
	}

	public void setVersionGame(String versionGame)
	{
		this.versionGame = versionGame;
	}

	public String getVersionGame()
	{
		return versionGame;
	}

	public void setChanges(String changes)
	{
		this.changes = changes;
	}

	public String getChanges()
	{
		return changes;
	}

	/*public String getNewFileName()
	{
		File file = ReflectionUtils.getPluginFile(this.plugin);
		String name = FilenameUtils.getBaseName(file.getPath());
		String ext = FilenameUtils.getExtension(file.getPath());
		return name + " " + this.version.toString() + "." + ext;
	}*/
}
