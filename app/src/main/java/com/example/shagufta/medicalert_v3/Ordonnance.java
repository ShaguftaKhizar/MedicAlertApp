package com.example.shagufta.medicalert_v3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cloudant.sync.documentstore.ConflictException;
import com.cloudant.sync.documentstore.DocumentNotFoundException;
import com.cloudant.sync.documentstore.DocumentStoreException;

import java.util.List;

public class Ordonnance extends ListActivity {

    private static TaskModel sTasks;
    private TaskAdapter mTaskAdapter;
    ActionMode mActionMode = null;
    private static int id = 1;
    private int stock, jourFin;

    static final String LOG_TAG = "Ordonnance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordonnance);

        if (sTasks == null) {
            // Model needs to stay in existence for lifetime of app.
            this.sTasks = MainActivity.getTaskModel();
        }

        // Register this activity as the listener to replication updates
        // while its active.
        this.sTasks.setReplicationListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //sTasks.startPullReplication();
                intent_start();
                //sTasks.startPushReplication();
            }
        });

        /****************PERMIER PULL !!!!! ******************/
        //sTasks.startPullReplication();
        // Load the tasks from the model
        //this.reloadTasksFromModel();

    }

    private void reloadTasksFromModel() {
        try {
            List<Medicament> tasks = this.sTasks.allTasks();
            this.mTaskAdapter = new TaskAdapter(this, tasks);
            this.setListAdapter(this.mTaskAdapter);
        } catch (DocumentStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void intent_start() {

        Intent i = New_ordonnance.makeIntent(Ordonnance.this);
        this.startActivityForResult(i, 1);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String name = "";
        String dureestr = "", nbCptstr = "", nbBoitestr = "", cptMatinstr = "", cptMidistr = "", cptSoirstr = "";
        boolean matin = false, midi = false, soir = false;
        int duree = 0, nbCpt = 0, nbBoite = 0, cptMatin = 0, cptMidi = 0, cptSoir = 0;

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                name = data.getStringExtra("name").toString();

                matin = data.getBooleanExtra("matin", matin);
                midi = data.getBooleanExtra("midi", midi);
                soir = data.getBooleanExtra("soir", soir);

                dureestr = data.getStringExtra("duree").toString();
                nbCptstr = data.getStringExtra("nbCpt").toString();
                nbBoitestr = data.getStringExtra("nbBoite").toString();
                cptMatinstr = data.getStringExtra("cptMatin").toString();
                cptMidistr = data.getStringExtra("cptMidi").toString();
                cptSoirstr = data.getStringExtra("cptSoir").toString();

                duree = Integer.parseInt(dureestr);
                nbCpt = Integer.parseInt(nbCptstr);
                nbBoite = Integer.parseInt(nbBoitestr);
                cptMatin = Integer.parseInt(cptMatinstr);
                cptMidi = Integer.parseInt(cptMidistr);
                cptSoir = Integer.parseInt(cptSoirstr);

            }
        }

        createNewTask(name, matin, midi, soir, duree, nbCpt, nbBoite, cptMatin, cptMidi, cptSoir, this.id);
        this.id++;

        Log.d(LOG_TAG, "medicament cree name : " + name + " matin : " + matin + " midi : " + midi
                + " soir : " + soir + " duree : " + duree + " NB comprime : " + nbCpt +
                " nb boite : " + nbBoite + " cpt matin : " + cptMatin + " cpt midi : " + cptMidi
                + " cpt soir : " + cptSoir + " id : " + id);
    }

    /*public static Intent makeIntent(Context context) {

        return new Intent(context, MainActivity.class);
    }*/

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();

        // Clear our reference as listener.
        //this.sTasks.setReplicationListener(null);
    }

    void replicationComplete() {
        reloadTasksFromModel();
        Toast.makeText(getApplicationContext(), R.string.replication_completed, Toast.LENGTH_LONG).show();
        //dismissDialog(DIALOG_PROGRESS);
    }

    void replicationError() {
        Log.i(LOG_TAG, "error()");
        reloadTasksFromModel();
        Toast.makeText(getApplicationContext(), R.string.replication_error, Toast.LENGTH_LONG).show();
    }

    void stopReplication() {
        sTasks.stopAllReplications();
        mTaskAdapter.notifyDataSetChanged();
    }

    private void createNewTask(String desc, boolean morning, boolean afternoon, boolean noon, int duration, int pillNumberPerBox,
                               int numberOfBox, int numberPillMorning, int numberPillAfternoon, int numberPillNoon, int id) {

        Medicament o = new Medicament(desc, morning, afternoon, noon, duration, pillNumberPerBox, numberOfBox, numberPillMorning,
                numberPillAfternoon, numberPillNoon, id);

        /*stock = calculStock(duration, pillNumberPerBox, numberOfBox, numberPillMorning, numberPillAfternoon, numberPillNoon);
        jourFin = duration + MainActivity.getDay();*/

        /*
        Intent intent = new Intent();
        intent.putExtra("stock", stock);
        intent.putExtra("jourFin", jourFin);

        setResult(RESULT_OK, intent);
        finish();
        */
        sTasks.createDocument(o);
        reloadTasksFromModel();
        sTasks.startPushReplication();
    }

    public int getStock() { return this.stock; }
    public int getJourFin() { return jourFin; }

    public int calculStock(int duration, int pillNumberPerBox, int numberOfBox, int numberPillMorning,
                           int numberPillAfternoon, int numberPillNoon) {

        int nbUseParJour = numberPillMorning + numberPillAfternoon + numberPillNoon;
        int nbRestant = pillNumberPerBox*numberOfBox-nbUseParJour*duration;

        return nbRestant;

    }

    private void toggleTaskCompleteAt(int position) {
        try {
            Medicament o = (Medicament) mTaskAdapter.getItem(position);
            o.setCompleted(!o.isCompleted());
            o = sTasks.updateDocument(o);
            mTaskAdapter.set(position, o);
        } catch (ConflictException e) {
            throw new RuntimeException(e);
        } catch (DocumentStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public void onCompleteCheckboxClicked(View view) {
        this.toggleTaskCompleteAt(view.getId());
    }

    private void deleteTaskAt(int position) {
        try {
            Medicament o = (Medicament) mTaskAdapter.getItem(position);
            sTasks.deleteDocument(o);
            mTaskAdapter.remove(position);
            Toast.makeText(Ordonnance.this, "Medicament supprime : " + o.getName(), Toast.LENGTH_SHORT).show();
        } catch (ConflictException e) {
            throw new RuntimeException(e);
        } catch (DocumentNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DocumentStoreException e) {
            throw new RuntimeException(e);
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(mActionMode != null) {
            mActionMode.finish();
        }

        // Make the newly clicked item the currently selected one.
        this.getListView().setItemChecked(position, true);
        mActionMode = this.startActionMode(mActionModeCallback);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteTaskAt(getListView().getCheckedItemPosition());
                    sTasks.startPushReplication();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            getListView().setItemChecked(getListView().getCheckedItemPosition(), false);
            mActionMode = null;
        }
    };

}
