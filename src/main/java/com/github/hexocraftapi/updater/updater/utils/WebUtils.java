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

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;

/**
 * @author <b>Hexosse</b> (<a href="https://github.com/hexosse">on GitHub</a>))
 */
public class WebUtils
{
	private static Proxy proxy;

	/**
	 * This class should not normally be instantiated.
	 */
	private WebUtils() {}


	/*
	 * Get the proxy used by the JVM
	 */
	public static Proxy getProxy()
	{
		if(proxy!=null) return proxy;

		List<Proxy> proxyList = null;
		try {
			ProxySelector def = ProxySelector.getDefault();
			proxyList = def.select(new URI("http://foo/bar"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		if(proxyList != null) {
			for(Iterator<Proxy> iter = proxyList.iterator(); iter.hasNext(); ) {
				proxy = iter.next();
				return proxy;
			}
		}
		return null;
	}

	public static URLConnection initConnection(URL url) throws IOException
	{
		Proxy proxy = getProxy();
		final URLConnection connection = (proxy != null ? url.openConnection(proxy) : url.openConnection());
		connection.setConnectTimeout(2000);
		connection.setReadTimeout(2500);
		connection.addRequestProperty("User-Agent", Updater.agent);
		connection.setDoOutput(true);

		return connection;
	}

}
