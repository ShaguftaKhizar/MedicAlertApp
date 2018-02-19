package com.example.shagufta.medicalert_v3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudant.sync.documentstore.ConflictException;
import com.cloudant.sync.documentstore.DocumentBodyFactory;
import com.cloudant.sync.documentstore.DocumentException;
import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentRevision;
import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreException;
import com.cloudant.sync.documentstore.DocumentStoreNotOpenedException;
import com.cloudant.sync.event.Subscribe;
import com.cloudant.sync.event.notifications.ReplicationCompleted;
import com.cloudant.sync.event.notifications.ReplicationErrored;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shagufta on 11/02/2018.
 */

public class TaskModel {

    private static final String LOG_TAG = "TasksModel";

    private static final String DOCUMENT_STORE_DIR = "data";
    private static final String DOCUMENT_STORE_NAME = "tasks";

    private DocumentStore mDocumentStore;

    private Replicator mPushReplicator;
    private Replicator mPullReplicator;

    private final Context mContext;
    private final Handler mHandler;

    private Ordonnance mListener;
    private MainActivity mListener2;

    public TaskModel(Context context) {

        this.mContext = context;

        // Set up our tasks DocumentStore within its own folder in the applications
        // data directory.
        File path = this.mContext.getApplicationContext().getDir(DOCUMENT_STORE_DIR, Context.MODE_PRIVATE);

        try {
            this.mDocumentStore = DocumentStore.getInstance(new File(path, DOCUMENT_STORE_NAME));
        } catch (DocumentStoreNotOpenedException e) {
            Log.e(LOG_TAG, "Unable to open DocumentStore", e);
        }

        Log.d(LOG_TAG, "Set up database at " + path.getAbsolutePath());

        // Set up the replicator objects from the app's settings.
        try {
            this.reloadReplicationSettings();
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "Unable to construct remote URI from configuration", e);
        }

        // Allow us to switch code called by the ReplicationListener into
        // the main thread so the UI can update safely.
        this.mHandler = new Handler(Looper.getMainLooper());

        Log.d(LOG_TAG, "TasksModel set up " + path.getAbsolutePath());
    }

    public void reloadReplicationSettings() throws URISyntaxException {

        // Stop running replications before reloading the replication
        // settings.
        // The stop() method instructs the replicator to stop ongoing
        // processes, and to stop making changes to the DocumentStore. Therefore,
        // we don't clear the listeners because their complete() methods
        // still need to be called once the replications have stopped
        // for the UI to be updated correctly with any changes made before
        // the replication was stopped.
        this.stopAllReplications();

        // Set up the new replicator objects
        URI uri = this.createServerURI();

        mPullReplicator = ReplicatorBuilder.pull().to(mDocumentStore).from(uri).build();
        mPushReplicator = ReplicatorBuilder.push().from(mDocumentStore).to(uri).build();

        mPushReplicator.getEventBus().register(this);
        mPullReplicator.getEventBus().register(this);

        Log.d(LOG_TAG, "Set up replicators for URI:" + uri.toString());
    }

    private URI createServerURI()
            throws URISyntaxException {
        // We store this in plain text for the purposes of simple demonstration,
        // you might want to use something more secure.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        String username = sharedPref.getString(MainActivity.SETTINGS_CLOUDANT_USER, "");
        String dbName = sharedPref.getString(MainActivity.SETTINGS_CLOUDANT_DB, "");
        String apiKey = sharedPref.getString(MainActivity.SETTINGS_CLOUDANT_API_KEY, "");
        String apiSecret = sharedPref.getString(MainActivity.SETTINGS_CLOUDANT_API_SECRET, "");
        String host = username + ".cloudant.com";

        // We recommend always using HTTPS to talk to Cloudant.
        return new URI("https", apiKey + ":" + apiSecret, host, 443, "/" + dbName, null, null);
    }

    public void setReplicationListener(MainActivity listener) {
        this.mListener2 = listener;
    }
    public void setReplicationListener(Ordonnance listener) {
        this.mListener = listener;
    }

    public List<Medicament> allTasks() throws DocumentStoreException {
        int nDocs = this.mDocumentStore.database().getDocumentCount();
        List<DocumentRevision> all = this.mDocumentStore.database().read(0, nDocs, true);
        List<Medicament> tasks = new ArrayList<Medicament>();

        // Filter all documents down to those of type Task.
        for(DocumentRevision rev : all) {
            Medicament o = Medicament.fromRevision(rev);
            if (o != null) {
                tasks.add(o);
            }
        }

        return tasks;
    }

    public Medicament createDocument(Medicament task) {
        DocumentRevision rev = new DocumentRevision();
        rev.setBody(DocumentBodyFactory.create(task.asMap()));
        try {
            DocumentRevision created = this.mDocumentStore.database().create(rev);
            return Medicament.fromRevision(created);
        } catch (DocumentException de) {
            return null;
        } catch (DocumentStoreException de) {
            return null;
        }
    }

    public Medicament updateDocument(Medicament task) throws ConflictException, DocumentStoreException {
        DocumentRevision rev = task.getDocumentRevision();
        rev.setBody(DocumentBodyFactory.create(task.asMap()));
        try {
            DocumentRevision updated = this.mDocumentStore.database().update(rev);
            return Medicament.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }

    public void deleteDocument(Medicament task) throws ConflictException, DocumentNotFoundException, DocumentStoreException {
        this.mDocumentStore.database().delete(task.getDocumentRevision());
    }

    public void stopAllReplications() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.stop();
        }
        if (this.mPushReplicator != null) {
            this.mPushReplicator.stop();
        }
    }

    public void startPullReplication() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.start();
        } else {
            throw new RuntimeException("Push replication not set up correctly");
        }
    }

    public void startPushReplication() {
        if (this.mPushReplicator != null) {
            this.mPushReplicator.start();
        } else {
            throw new RuntimeException("Push replication not set up correctly");
        }
    }

    @Subscribe
    public void complete(ReplicationCompleted rc) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.replicationComplete();
                }
            }
        });
    }

    @Subscribe
    public void error(ReplicationErrored re) {
        Log.e(LOG_TAG, "Replication error:", re.errorInfo);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.replicationError();
                }
            }
        });
    }

}
