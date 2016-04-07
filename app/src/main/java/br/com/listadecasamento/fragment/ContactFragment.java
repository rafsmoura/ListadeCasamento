package br.com.listadecasamento.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.listadecasamento.MainActivity;
import br.com.listadecasamento.R;
import br.com.listadecasamento.model.Contact;
import br.com.listadecasamento.persistance.Cache;

/**
 * Created by root on 06/04/16.
 */
public class ContactFragment extends DialogFragment {


    private Context context;
    private Contact contact;
    private View view;

    private TextView contactName, contactNumber;
    private Button addButton, removeButton;


    public ContactFragment() {
    }

    public static ContactFragment newInstance() {

        Bundle args = new Bundle();

        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_contact, container);

        contactName = (TextView) view.findViewById(R.id.contactName);
        contactNumber = (TextView) view.findViewById(R.id.contactNumber);

        addButton = (Button) view.findViewById(R.id.addContact);

        removeButton = (Button) view.findViewById(R.id.removeContact);

        if (contact == null) {

            removeButton.setVisibility(View.GONE);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = contactName.getText().toString();
                    String number = contactNumber.getText().toString();
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(number)) {

                        contact = new Contact(name, number);
                        Cache.getInstance().saveContact(context, contact);
                        ((MainActivity) context).createScreen();
                        getDialog().dismiss();

                    } else {

                        Toast.makeText(context, R.string.error_text, Toast.LENGTH_LONG).show();
                    }

                }
            });


        } else {

            removeButton.setVisibility(View.VISIBLE);
            contactName.setText(contact.getContactName());
            contactNumber.setText(contact.getContactNumber());

            addButton.setText(R.string.edit_contact);
            addButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    String name = contactName.getText().toString();
                    String number = contactNumber.getText().toString();

                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(number)) {

                        contact.setContactName(name);
                        contact.setContactNumber(number);
                        Cache.getInstance().saveContact(context, contact);
                        ((MainActivity) context).createScreen();
                        getDialog().dismiss();

                    } else {

                        Toast.makeText(context, R.string.error_text, Toast.LENGTH_LONG).show();
                    }
                }
            });


            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (contact.isAlreadySent()) {
                        Cache.getInstance().removeContact(context, contact);
                        ((MainActivity) context).createScreen();
                        getDialog().dismiss();
                    } else {
                        Toast.makeText(context, R.string.error_delete_contact, Toast.LENGTH_LONG).show();
                    }
                }
            });




        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
