package com.example.shagufta.medicalert_v3;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Shagufta on 11/02/2018.
 */

public class New_ordonnance extends Activity {

    private static TaskModel sTasks;

    private String name;
    private String dureestr = "0";
    private String nbComprimestr = "0";
    private String nbBoitestr = "0";
    private String nbCptMatinstr = "0";
    private String nbCptMidistr = "0";
    private String nbCptSoirstr = "0";


    static final String LOG_TAG = "New_Ordonnance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_ordonnance);

        Log.d(LOG_TAG, "dialog new ordo");
        this.sTasks = MainActivity.getTaskModel();

        final CheckBox matin = (CheckBox) findViewById(R.id.matin);
        final CheckBox midi = (CheckBox) findViewById(R.id.midi);
        final CheckBox soir = (CheckBox) findViewById(R.id.soir);


        final Button button = findViewById(R.id.valider);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText description = (EditText) findViewById(R.id.nomMedoc);

                final EditText dureeEdit = (EditText) findViewById(R.id.duree);
                final EditText nbComprimeEdit = (EditText) findViewById(R.id.nbTotcomprime);
                final EditText nbBoiteEdit = (EditText) findViewById(R.id.nbBoite);
                final EditText nbCptMatinEdit = (EditText) findViewById(R.id.nbcptmatin);
                final EditText nbCptMidiEdit = (EditText) findViewById(R.id.nbcptmidi);
                final EditText nbCptSoirEdit = (EditText) findViewById(R.id.nbcptsoir);

                name = description.getText().toString();

                dureestr = dureeEdit.getText().toString();
                nbComprimestr = nbComprimeEdit.getText().toString();
                nbBoitestr = nbBoiteEdit.getText().toString();
                nbCptMatinstr = nbCptMatinEdit.getText().toString();
                nbCptMidistr = nbCptMidiEdit.getText().toString();
                nbCptSoirstr = nbCptSoirEdit.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("duree", dureestr);
                intent.putExtra("nbCpt", nbComprimestr);
                intent.putExtra("nbBoite", nbBoitestr);
                intent.putExtra("cptMatin", nbCptMatinstr);
                intent.putExtra("cptMidi", nbCptMidistr);
                intent.putExtra("cptSoir", nbCptSoirstr);
                intent.putExtra("matin", matin.isChecked());
                intent.putExtra("midi", midi.isChecked());
                intent.putExtra("soir", soir.isChecked());

                Log.d(LOG_TAG, "name : " + name + " matin : " + matin + " midi : " + midi
                        + " soir : " + soir + " duree : " + dureestr + " NB comprime : " + nbComprimestr +
                        " nb boite : " + nbBoitestr + " cpt matin : " + nbCptMatinstr + " cpt midi : " + nbCptMidistr
                        + " cpt soir : " + nbCptSoirstr);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    public static Intent makeIntent(Context context) {

        return new Intent(context, New_ordonnance.class);
    }

    public String getName() { return this.name; }


}
