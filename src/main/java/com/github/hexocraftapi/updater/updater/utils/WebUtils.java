package com.github.hexocraftapi.updater.updater.utils;

import com.github.hexocraftapi.updater.updater.Updater;

import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.List;

/**
 * @author <b>hexosse</b> (<a href="https://github.comp/hexosse">hexosse on GitHub</a>))
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
