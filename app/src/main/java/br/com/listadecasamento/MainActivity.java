package br.com.listadecasamento;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.listadecasamento.adapter.ListAdapter;
import br.com.listadecasamento.fragment.ContactFragment;
import br.com.listadecasamento.model.Contact;
import br.com.listadecasamento.persistance.Cache;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RelativeLayout container;
    private TextView noContactsLabel;
    private ListAdapter listAdapter;
    private CheckBox selectAll;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context = this;

        noContactsLabel = (TextView) findViewById(R.id.noContacts);
        container = (RelativeLayout) findViewById(R.id.contactContainer);
        selectAll = (CheckBox) findViewById(R.id.selectAll);
        recyclerView = (RecyclerView) findViewById(R.id.contactList);

        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (listAdapter != null && listAdapter.getCheckBoxes().size() > 0) {

                    if (isChecked) {

                        for (CheckBox c : listAdapter.getCheckBoxes()) {
                            c.setChecked(true);
                        }
                    } else {
                        for (CheckBox c : listAdapter.getCheckBoxes()) {
                            c.setChecked(false);
                        }
                    }
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getSupportFragmentManager();
                ContactFragment cf = new ContactFragment();
                cf.setContext(context);
                cf.show(fm, "New contact");

            }
        });

            createScreen();

    }

    public void createScreen() {

        List<Contact> contacts = Cache.getInstance().getContacts(this.context);

        if (contacts.size() == 0 || contacts == null) {
            noContactsLabel.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else {
            noContactsLabel.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);

            listAdapter = new ListAdapter(contacts, this.context);

            LinearLayoutManager lm = new LinearLayoutManager(this.context);
            recyclerView.setLayoutManager(lm);
            recyclerView.setAdapter(listAdapter);
        }

        selectAll.setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void sendSms() {

        SmsManager manager = SmsManager.getDefault();

        List<Contact> contactsForSend = new ArrayList<>();

        for (CheckBox box : listAdapter.getCheckBoxes()) {

            if (box.isChecked()) {

                contactsForSend.add((Contact) box.getTag());

            }

        }

            if (contactsForSend.size() > 0) {

                Collections.sort(contactsForSend);

                Log.d("==CONTACT_ORDED==", contactsForSend.toString());

                for (int i =0; i < contactsForSend.size(); i++) {
                    Contact c = contactsForSend.get(i);
                    if (!c.isAlreadySent()) {
                        Log.d("==CONTACT==", c.toString());
                        manager.sendTextMessage(c.getContactNumber(),
                                null,
                                getString(R.string.sent_msg_text, c.getContactName()),
                                null,
                                null);

                        c.setAlreadySent(true);


                        Cache.getInstance().saveContact(context, c);
                    }
                }

                createScreen();


            } else {
                Toast.makeText(context, R.string.no_contacts_selected, Toast.LENGTH_LONG).show();
            }
        }

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, 1);

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, 1);


            }
        } else {

            sendSms();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    sendSms();

                } else {

                    Toast.makeText(context, R.string.permission, Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.alert_title);
            builder.setMessage(R.string.alert_message);
            builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (listAdapter != null && listAdapter.getItemCount() > 0) {
                        checkPermission();
                    } else {
                        Toast.makeText(context, R.string.no_contacts_selected, Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton(R.string.no_send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
