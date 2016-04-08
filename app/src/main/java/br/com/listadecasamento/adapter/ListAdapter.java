package br.com.listadecasamento.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.listadecasamento.MainActivity;
import br.com.listadecasamento.R;
import br.com.listadecasamento.fragment.ContactFragment;
import br.com.listadecasamento.model.Contact;

/**
 * Created by root on 06/04/16.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private View view;

    private List<Contact> contacts;
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private Context context;
    private boolean deleteList;

    public ListAdapter(List<Contact> contacts, Context context) {
        this.context = context;
        this.contacts = contacts;
        this.deleteList = false;
        Collections.sort(this.contacts);
        setHasStableIds(true);
    }

    public ListAdapter(List<Contact> contacts, Context context, boolean deleteList) {
        this.context = context;
        this.contacts = contacts;
        Collections.sort(this.contacts);
        this.deleteList = deleteList;
        setHasStableIds(true);
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_content, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout checkboxLayout;
        private TextView contactName, contactNumber, dateAndTime, status;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);

            contactName = (TextView) itemView.findViewById(R.id.contactName);
            contactNumber = (TextView) itemView.findViewById(R.id.contactNumber);
            dateAndTime = (TextView) itemView.findViewById(R.id.dateAndTime);
            status = (TextView) itemView.findViewById(R.id.status);
            checkboxLayout = (RelativeLayout) itemView.findViewById(R.id.checkboxLayout);

            view = itemView;

        }
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {


        final Contact contact = contacts.get(position);

        holder.contactName.setText(contact.getContactName());
        holder.contactNumber.setText(contact.getContactNumber());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        String formattedDate = simpleDateFormat.format(contact.getRegisterDate());
        holder.dateAndTime.setText(formattedDate);

        if (contact.isAlreadySent()) {
            holder.status.setText(context.getString(R.string.sent));

            if (this.deleteList) {
                holder.status.setText(context.getString(R.string.sent));
                CheckBox checkBox = (CheckBox) View.inflate(context, R.layout.checkbox, null);
                checkBox.setTag(contact);
                checkBoxes.add(checkBox);

                holder.checkboxLayout.addView(checkBox);
            }

        } else if (!this.deleteList) {
            holder.status.setText(context.getString(R.string.not_sent));
            CheckBox checkBox = (CheckBox) View.inflate(context, R.layout.checkbox, null);
            checkBox.setTag(contact);
            checkBoxes.add(checkBox);

            holder.checkboxLayout.addView(checkBox);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                ContactFragment cf = ContactFragment.newInstance();
                cf.setContact(contact);
                cf.setContext(context);
                cf.show(fm, "Edit Contact");
            }
        });



    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<CheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public void setCheckBoxes(List<CheckBox> checkBoxes) {
        this.checkBoxes = checkBoxes;
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public long getItemId(int position) {
        return contacts.get(position).hashCode();
    }
}
