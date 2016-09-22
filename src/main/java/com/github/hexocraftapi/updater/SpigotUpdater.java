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
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import static com.github.hexocraftapi.updater.updater.Version.isSemver;
import static com.github.hexocraftapi.updater.updater.utils.LoggerUtils.log;
import static com.github.hexocraftapi.updater.updater.utils.WebUtils.initConnection;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
@SuppressWarnings("unused")
public class SpigotUpdater extends Updater
{
	/**
	 * Host to contact
	 */
	protected static String spiget = "http://api.spiget.org";
	protected static String spigot = "http://www.spigotmc.org";

	/**
	 * Repository query
	 */
	protected static String queryVersion = "/v2/resources/{{ RESOURCE_ID }}/versions";
	protected static String queryUpdate = "/v2/resources/{{ RESOURCE_ID }}/updates";

	/**
	 * Ressource ID.
	 */
	protected String resourceId = null;

	/**
	 * URL to query.
	 */
	protected URL queryVersionUrl;
	protected URL queryUpdateUrl;



	public SpigotUpdater(JavaPlugin plugin, String resourceId)
	{
		this(plugin, resourceId, false);
	}

	public SpigotUpdater(JavaPlugin plugin, String resourceId, boolean verbose)
	{
		super(plugin, verbose);

		try
		{
			this.resourceId = resourceId;
			this.queryVersionUrl = new URL((spiget + queryVersion).replace("{{ RESOURCE_ID }}", this.resourceId));
			this.queryUpdateUrl = new URL((spiget + queryUpdate).replace("{{ RESOURCE_ID }}", this.resourceId));
			log(Level.INFO, "Set the URL to " + this.queryVersionUrl);
			log(Level.INFO, "Set the resource ID to " + this.resourceId);
		}
		catch(NumberFormatException ex)
		{
			log(Level.SEVERE, "Unable to parse semver string!");
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

			final URLConnection vConnection = initConnection(queryVersionUrl);
			log(Level.INFO, "Opening connection to API");

			final BufferedReader vReader = new BufferedReader(new InputStreamReader(vConnection.getInputStream()));
			final StringBuilder vBuffer = new StringBuilder();

			String line; while((line = vReader.readLine()) != null) vBuffer.append(line);

			JSONArray versions = (JSONArray) JSONValue.parse(vBuffer.toString());
			log(Level.INFO, "Parsing the returned JSON");

			if(versions.isEmpty())
			{
				log(Level.WARNING, "Appears there were no releases, setting result");
				this.result = Response.REPO_NO_RELEASES;
				return false;
			}
			JSONObject version = (JSONObject) versions.get(versions.size() - 1);
			String tag = version.get("name").toString();
			String url = version.get("url").toString();
			ReleaseType type = ReleaseType.RELEASE;


			final URLConnection uConnection = initConnection(queryUpdateUrl);
			log(Level.INFO, "Opening connection to API");

			final BufferedReader uReader = new BufferedReader(new InputStreamReader(uConnection.getInputStream()));
			final StringBuilder uBuffer = new StringBuilder();

			while((line = uReader.readLine()) != null) uBuffer.append(line);

			JSONArray updates = (JSONArray) JSONValue.parse(uBuffer.toString());
			log(Level.INFO, "Parsing the returned JSON");

			if(updates.isEmpty())
			{
				log(Level.WARNING, "Appears there were no releases, setting result");
				this.result = Response.REPO_NO_RELEASES;
				return false;
			}
			JSONObject update = (JSONObject) updates.get(updates.size() - 1);
			String name = update.get("title").toString();
			String body = Base64Coder.decodeString(update.get("description").toString());

			if(isSemver(tag))
			{
				this.update = new Update(this.plugin, name, Version.parse(tag), type);
				this.update.setChanges(body);
				this.update.setDownloadUrl(new URL(spigot + "/" + url));

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
			if(e.getMessage().contains("connect timed out")
				|| e.getMessage().contains("HTTP response code: 522")
				|| e.getMessage().contains("HTTP response code: 524"))
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
