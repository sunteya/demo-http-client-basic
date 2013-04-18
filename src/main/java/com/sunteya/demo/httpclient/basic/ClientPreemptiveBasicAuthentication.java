package com.sunteya.demo.httpclient.basic;

import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

public class ClientPreemptiveBasicAuthentication {

	public static void main(String[] args) throws Exception {

		HttpHost targetHost = new HttpHost("10.27.69.111", 80, "http");
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		try {
			httpclient.getCredentialsProvider().setCredentials(
					new AuthScope(targetHost.getHostName(), targetHost.getPort()),
					new UsernamePasswordCredentials("username", "password"));

			AuthCache authCache = new BasicAuthCache();
			BasicScheme basicAuth = new BasicScheme();
			authCache.put(targetHost, basicAuth);

			BasicHttpContext localcontext = new BasicHttpContext();
			localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

			HttpGet httpget = new HttpGet("/api/v2/live/channels/3/resources.json");
			HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);

			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String encoding = "utf-8";
				Header contentType = entity.getContentType();
				if (contentType != null) {
					String charset = StringUtils.substringAfter(contentType.getValue(), "charset=");
					if (StringUtils.isNotBlank(charset)) {
						encoding = charset;
					}
				}
				System.out.println("Response content: " + IOUtils.toString(entity.getContent(), Charset.forName(encoding)));
			}
			
			EntityUtils.consume(entity);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

}
