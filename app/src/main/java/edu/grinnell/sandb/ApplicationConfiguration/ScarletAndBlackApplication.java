package edu.grinnell.sandb.ApplicationConfiguration;

import android.os.AsyncTask;
import android.os.Build;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orm.SugarApp;

public class ScarletAndBlackApplication extends SugarApp {

    public void onCreate() {
        super.onCreate();

        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration.Builder configb = new ImageLoaderConfiguration.Builder(this)
	        .denyCacheImageMultipleSizesInMemory()
	        .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
	        .memoryCacheSize(6 * 1024 * 1024)
	        .discCacheSize(100 * 1024 * 1024)
	        .discCacheFileCount(100);
	       // .enableLogging();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        	configb = configb.taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        					 .taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        
        ImageLoader.getInstance().init(configb.build());
    }
}