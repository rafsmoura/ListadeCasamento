package br.com.listadecasamento.persistance;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.listadecasamento.model.Contact;

/**
 * Created by root on 06/04/16.
 */
public class Cache {
    private static Cache ourInstance = new Cache();

    private final String SHARED_KEY = "ListaCasamento";
    private final String KEY = "Contatos";

    public static Cache getInstance() {
        return ourInstance;
    }



    private Cache() {
    }

    private Type getContactType() {
        Type type = new TypeToken<ArrayList<Contact>>() {
        }.getType();

        return type;
    }

    public void saveContact(Context context, Contact contact) {

        SharedPreferences sp = context.getSharedPreferences(SHARED_KEY, 1);

        Gson gson = new Gson();

        String serialized = sp.getString(KEY, null);

        List<Contact> contacts;

        if (TextUtils.isEmpty(serialized)) {
            contacts = new ArrayList<>();
            contacts.add(0, contact);

            String serialList = gson.toJson(contacts, getContactType());

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY, serialList);
            editor.apply();

        } else {

            contacts = gson.fromJson(serialized, getContactType());
            boolean found = false;

            for (int i = 0; i < contacts.size(); i++) {
                Contact c = contacts.get(i);

                if (c.getId() == contact.getId()) {
                    c.setContactName(contact.getContactName());
                    c.setContactNumber(contact.getContactNumber());
                    c.setAlreadySent(contact.isAlreadySent());
                    found = true;
                    break;
                }
            }

            if (!found) {
                contacts.add(0, contact);
            }

            String serialList = gson.toJson(contacts, getContactType());

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY, serialList);
            editor.apply();

        }

    }


    public List<Contact> getContacts(Context context) {

        SharedPreferences sp = context.getSharedPreferences(SHARED_KEY, 1);

        Gson gson = new Gson();

        String serialized = sp.getString(KEY, null);

        if (TextUtils.isEmpty(serialized)) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(serialized, getContactType());
        }

    }

    public void removeContact(Context context, Contact contact) {

        SharedPreferences sp = context.getSharedPreferences(SHARED_KEY, 1);

        Gson gson = new Gson();

        String serialized = sp.getString(KEY, null);

        if (TextUtils.isEmpty(serialized)) {
            return;
        } else {

            List<Contact> contacts = gson.fromJson(serialized, getContactType());

            for (int i = 0; i < contacts.size(); i++) {
                Contact c = contacts.get(i);
                if (c.getId() == contact.getId()) {
                    contacts.remove(c);
                }
            }
            String serialList = gson.toJson(contacts, getContactType());

            SharedPreferences.Editor editor = sp.edit();
            editor.putString(KEY, serialList);
            editor.apply();

        }


    }


}
