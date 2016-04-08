package br.com.listadecasamento;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    private Button deleteAll;
    private Context context;
    private FloatingActionButton fab;
    private boolean deleteList;

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
        deleteAll = (Button) findViewById(R.id.deleteAll);

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

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!deleteList) {
                    deleteList = true;
                    deleteAll.setText(R.string.delete_cancel);
                    fab.setImageResource(R.drawable.ic_delete_white_24dp);

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteSelectedContacts();
                        }
                    });

                    createScreen(deleteList);

                } else {
                    deleteList = false;
                    deleteAll.setText(R.string.delete_all_contacts);
                    fab.setImageResource(R.drawable.ic_add_white_24dp);
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

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
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

    public void createScreen(boolean flag) {

        List<Contact> contacts = Cache.getInstance().getContacts(this.context);

        if (contacts.size() == 0 || contacts == null) {
            noContactsLabel.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        } else {
            noContactsLabel.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);

            listAdapter = new ListAdapter(contacts, this.context, flag);

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



        List<Contact> contactsForSend = new ArrayList<>();

        for (CheckBox box : listAdapter.getCheckBoxes()) {

            if (box.isChecked()) {

                contactsForSend.add((Contact) box.getTag());

            }

        }

            if (contactsForSend.size() > 0) {

                Collections.sort(contactsForSend);

                new Kevin(contactsForSend).execute();

                // createScreen();


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

    private void deleteSelectedContacts() {
        List<Contact> contactsForSend = new ArrayList<>();

        for (CheckBox box : listAdapter.getCheckBoxes()) {

            if (box.isChecked()) {

                contactsForSend.add((Contact) box.getTag());

            }

        }

        if (contactsForSend.size() > 0) {

            for (int i = 0; i < contactsForSend.size(); i++) {
                Cache.getInstance().removeContact(context, contactsForSend.get(i));
            }

            this.deleteList = false;
            deleteAll.setText(R.string.delete_all_contacts);
            fab.setImageResource(R.drawable.ic_add_white_24dp);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    ContactFragment cf = new ContactFragment();
                    cf.setContext(context);
                    cf.show(fm, "New contact");
                }
            });

            createScreen();

        } else {
            Toast.makeText(context, R.string.error_delete_n_contacts, Toast.LENGTH_LONG).show();
        }
    }

    public class Kevin extends AsyncTask<Void, String, Void> {

        private List<Contact> list;
        private ProgressDialog dialog;

        public Kevin(List<Contact> list) {
            this.list = list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(context);
            dialog.setMessage(context.
                    getResources().
                    getString(R.string.loading_msg));
            dialog.setCancelable(false);

            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SmsManager manager = SmsManager.getDefault();
            for (int i = 0; i < list.size(); i++) {
                Contact c = list.get(i);
                Log.d("==CONTACT==", c.getContactName());
                if (!c.isAlreadySent()) {

                    publishProgress(c.getContactName());

                    manager.sendTextMessage(c.getContactNumber(),
                            null,
                            getString(R.string.sent_msg_text, c.getContactName()),
                            null,
                            null);

                    c.setAlreadySent(true);


                    Cache.getInstance().saveContact(context, c);

                    try {
                        Thread.sleep(2 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            dialog.setMessage(context.getResources().getString(R.string.sent_to, values[0]));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            createScreen();
        }
    }
}
