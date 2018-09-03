package eu.pretix.pretixdroid;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import eu.pretix.libpretixsync.api.HttpClientFactory;
import okhttp3.OkHttpClient;

public class AndroidHttpClientFactory implements HttpClientFactory {

    private static final int TIMEOUT = 30;

    @Override
    public OkHttpClient buildClient() {
        if (BuildConfig.DEBUG) {
            return new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS) // connect timeout
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)    // socket timeout
                    .writeTimeout(TIMEOUT / 2, TimeUnit.SECONDS)
                    .build();
        } else {
            return new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS) // connect timeout
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)    // socket timeout
                    .writeTimeout(TIMEOUT / 2, TimeUnit.SECONDS)
                    .build();
        }
    }
}
