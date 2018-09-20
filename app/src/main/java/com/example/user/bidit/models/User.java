package com.example.user.bidit.models;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;

public class User {
    private String name, surname, phoneNumber, id, email, passportSeries, photoUrl, balance;

    public User() {
    }

    public User(String pName, String balance, String pSurname, String pPhoneNumber, String pId, String pEmail, String pPassportSeries, String pPhotoUrl) {
        this.name = pName;
        this.balance = balance;
        this.surname = pSurname;
        this.phoneNumber = pPhoneNumber;
        this.id = pId;
        this.email = pEmail;
        this.passportSeries = pPassportSeries;
        this.photoUrl = pPhotoUrl;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBallance() {
        return balance;
    }

    public void setBallance(String balance) {
        this.balance = balance;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassportSeries() {
        return passportSeries;
    }

    public void setPassportSeries(String passportSeries) {
        this.passportSeries = passportSeries;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    private User(final Builder builder) {
        name = builder.name;
        balance = builder.balance;
        surname = builder.surname;
        phoneNumber = builder.phoneNumber;
        id = builder.id;
        email = builder.email;
        passportSeries = builder.passportSeria;
        photoUrl = builder.photoUrl;
    }

    public static class Builder {
        private String name, surname, phoneNumber, id, email, passportSeria, photoUrl, balance;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder setBalance(String balance) {
            this.balance = balance;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassportSeries(String pPassportSeria) {
            this.passportSeria = pPassportSeria;
            return this;
        }

        public Builder setPhotoUrl(String pPhotoUrl) {
            this.photoUrl = pPhotoUrl;
            return this;
        }

        public User create() {
            return new User(this);
        }
    }

    public static User fromDataSnapshot(DataSnapshot pDataSnapshot, String pCurrentUId) {
        Log.d("MYTAG", "fromDataSnapshot: ");
        User user = new User();
        user.setName((String) pDataSnapshot.child(pCurrentUId).child("name").getValue());
        user.setSurname((String) pDataSnapshot.child(pCurrentUId).child("surname").getValue());
        user.setEmail((String) pDataSnapshot.child(pCurrentUId).child("email").getValue());
        user.setPassportSeries((String) pDataSnapshot.child(pCurrentUId).child("passportSeries").getValue());
        user.setPhoneNumber((String) pDataSnapshot.child(pCurrentUId).child("phoneNumber").getValue());
        user.setPhotoUrl((String) pDataSnapshot.child(pCurrentUId).child("photoUrl").getValue());
        user.setBallance((String) pDataSnapshot.child(pCurrentUId).child("ballance").getValue());
        return user;
    }
}
