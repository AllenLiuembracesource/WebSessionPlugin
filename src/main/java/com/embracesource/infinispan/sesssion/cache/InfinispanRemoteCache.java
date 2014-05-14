package com.embracesource.infinispan.sesssion.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.util.FileLookupFactory;

public class InfinispanRemoteCache {
	/**
	 * 集中session 的infinispan缓存名称
	 */
	private String cacheName = "infinispan-session";
	/**
	 * infinispan hotrod客户端配置
	 */
	private String infinispanConfig = "infinispan-session-config.properties";

	private RemoteCacheManager remoteCacheManager = null;
	private RemoteCache<String, Object> remoteCache; 

	private Lock lock = new ReentrantLock();

	public InfinispanRemoteCache(String cacheName, String infinispanConfig) {
		this.cacheName = cacheName != null ? cacheName : "infinispan-session";
		this.infinispanConfig = infinispanConfig != null ? infinispanConfig
				: "infinispan-session-config.properties";
	}

	/**
	 * 初始化infinispan client
	 */
	public void init() {
		if (remoteCacheManager == null) {
			try {
				lock.lock();
				if (remoteCacheManager == null) {
					InputStream stream = null;
					Properties properties = new Properties();
					try {
						stream = FileLookupFactory.newInstance().lookupFile(
								infinispanConfig,
								Thread.currentThread().getContextClassLoader());
						properties.load(stream);
					} catch (IOException e) {
						throw new RuntimeException(
								"Issues configuring from client "
										+ infinispanConfig, e);
					} finally {
						if (stream != null) {
							try {
								stream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					remoteCacheManager = new RemoteCacheManager(
							new ConfigurationBuilder().withProperties(
									properties).build());
					remoteCache=remoteCacheManager.getCache(cacheName);
				}
			} finally {
				lock.unlock();
			}
		}
	}
	public RemoteCache<String, Object> getRemoteCache() {
		return remoteCache;
	}

	/**
	 * 关闭infinispan客户端
	 */
	private synchronized void _destroy() {
		if (remoteCacheManager != null) {
			remoteCacheManager.stop();
			remoteCacheManager = null;
		}
	}

	public void destroy() {
		this._destroy();
	}
}
