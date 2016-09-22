package com.github.hexocraftapi.updater;

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

import com.github.hexocraftapi.updater.updater.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import static com.github.hexocraftapi.updater.updater.utils.LoggerUtils.log;
import static com.github.hexocraftapi.updater.updater.utils.WebUtils.initConnection;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
@SuppressWarnings("unused")
public class GitHubUpdater extends Updater
{
	/**
	 * Host to contact
	 */
	protected static String host = "https://api.github.com";

	/**
	 * Repository query
	 */
	protected static String query = "/repos/{{ REPOSITORY }}/releases";

	/**
	 * Store the repository to lookup.
	 */
	protected String repository = null;

	/**
	 * URL to query.
	 */
	protected URL queryUrl;


	public GitHubUpdater(JavaPlugin plugin, String repository)
	{
		this(plugin, repository, false);
	}

	public GitHubUpdater(JavaPlugin plugin, String repository, boolean verbose)
	{
		super(plugin, verbose);

		try
		{
			this.repository = repository;
			this.queryUrl = new URL((host + query).replace("{{ REPOSITORY }}", this.repository));
			log(Level.INFO, "Set the URL to " + this.queryUrl);
		}
		catch(NumberFormatException ex)
		{
			log(Level.SEVERE, "Unable to parse semver string!");
			result = Response.FAILED;
		}
		catch(MalformedURLException ex)
		{
			log(Level.SEVERE, "Invalid URL, return failed response.");
			result = Response.FAILED;
		}

		if (this.result != Response.FAILED) {
			this.thread = new Thread(new UpdaterRunnable(this));
			this.thread.start();
		}
	}


	/**
	 * Check the repository for neweest file.
	 *
	 * @return true if successful.
	 */
	protected boolean read()
	{
		try {
			final URLConnection connection = initConnection(queryUrl);
			connection.addRequestProperty("Accept", "application/vnd.github.v3+json");
			log(Level.INFO, "Opening connection to API");

			final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			final StringBuilder buffer = new StringBuilder();

			String line;
			while((line = reader.readLine()) != null)
				buffer.append(line);

			JSONArray releases = (JSONArray) JSONValue.parse(buffer.toString());
			log(Level.INFO, "Parsing the returned JSON");

			if(releases.isEmpty())
			{
				log(Level.WARNING, "Appears there were no releases, setting result");
				this.result = Response.REPO_NO_RELEASES;
				return false;
			}

			JSONObject release = (JSONObject) releases.get(0);
			String name = release.get("name").toString();
			String tag = release.get("tag_name").toString();
			String body = release.get("body").toString();
			ReleaseType type = ((Boolean)release.get("draft") || (Boolean)release.get("prerelease")) ? ReleaseType.PRE_RELEASE : ReleaseType.RELEASE;

			final JSONArray assets = (JSONArray) JSONValue.parse(release.get("assets").toString());

			if(assets.isEmpty())
			{
				log(Level.WARNING, "Appears there were no download link for this releases, setting result");
				this.result = Response.REPO_NO_RELEASES;
				return false;
			}

			JSONObject link = (JSONObject) assets.get(assets.size() - 1);
			String url = (String)link.get("browser_download_url");

			if(Version.isSemver(tag))
			{
				this.update = new Update(this.plugin, name, Version.parse(tag), type);
				this.update.setChanges(body);
				this.update.setDownloadUrl(new URL(url));

				if(current.isLower(this.update.getVersion()))
				{
					log(Level.INFO, "Hooray, we found a semver compliant update!");
					this.result = Response.SUCCESS;
				}
				else
				{
					log(Level.INFO, "The update you specified is the latest update available!");
					this.result = Response.NO_UPDATE;
					this.update = null;
				}
			}
			else
			{
				log(Level.WARNING, "Version string is not semver compliant!");
				this.result = Response.REPO_NOT_SEMVER;
				this.update = null;
			}
		}
		catch(Exception e)
		{
			if(e.getMessage().contains("connect timed out"))
			{
				log(Level.WARNING, "HTTP request time out!");
				this.result = Response.ERROR_TIME_OUT;
				this.update = null;
			}
			else if(e.getMessage().contains("HTTP response code: 403"))
			{
				log(Level.WARNING, "GitHub denied our HTTP request!");
				this.result = Response.ERROR_403;
				this.update = null;
			}
			else if(e.getMessage().contains("HTTP response code: 404"))
			{
				log(Level.WARNING, "The specified repository could not be found!");
				this.result = Response.ERROR_404;
				this.update = null;
			}
			else if(e.getMessage().contains("HTTP response code: 500"))
			{
				log(Level.WARNING, "Internal server error");
				this.result = Response.ERROR_500;
				this.update = null;
			}
			else
			{
				log(Level.SEVERE, "Failed to check for updates!");
				this.result = Response.FAILED;
				this.update = null;
			}
			return false;
		}
		return true;
	}
}
