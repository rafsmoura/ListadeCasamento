package br.com.listadecasamento.model;

import java.util.Date;

/**
 * Created by root on 06/04/16.
 */
public class Contact implements Comparable<Contact> {
    private int id;
    private String contactName;
    private String contactNumber;
    private Date registerDate;
    private boolean alreadySent;


    public Contact(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.registerDate = new Date();
        this.alreadySent = false;
        this.id = hashCode();
    }

    public Contact() {
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public boolean isAlreadySent() {
        return alreadySent;
    }

    public void setAlreadySent(boolean alreadySent) {
        this.alreadySent = alreadySent;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", contactName='" + contactName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", registerDate=" + registerDate +
                ", alreadySent=" + alreadySent +
                '}';
    }

    @Override
    public int compareTo(Contact another) {
        return getRegisterDate().compareTo(another.getRegisterDate());
    }
}
