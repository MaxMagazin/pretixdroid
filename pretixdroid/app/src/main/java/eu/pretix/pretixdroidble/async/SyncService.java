package eu.pretix.pretixdroidble.async;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import eu.pretix.libpretixsync.api.PretixApi;
import eu.pretix.libpretixsync.sync.SyncManager;
import eu.pretix.pretixdroidble.AndroidHttpClientFactory;
import eu.pretix.pretixdroidble.AndroidSentryImplementation;
import eu.pretix.pretixdroidble.AppConfig;
import eu.pretix.pretixdroidble.PretixDroid;
import io.requery.BlockingEntityStore;
import io.requery.Persistable;

public class SyncService extends IntentService {

    private AppConfig config;
    private PretixApi api;
    private BlockingEntityStore<Persistable> dataStore;

    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        config = new AppConfig(this);
        dataStore = ((PretixDroid) getApplicationContext()).getData();
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.i("SyncService", "Sync triggered");

        // Rebuild in case config has changed
        api = PretixApi.fromConfig(config, new AndroidHttpClientFactory());

        long upload_interval = 1000;
        long download_interval = 60000;
        if (!config.getAsyncModeEnabled()) {
            download_interval = 120000;
        }

        SyncManager sm = new SyncManager(
                config,
                api,
                new AndroidSentryImplementation(),
                dataStore,
                null,
                upload_interval,
                download_interval
        );
        sm.sync(false);
    }

}
