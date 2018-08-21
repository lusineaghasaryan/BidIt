package com.example.user.bidit.models;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;

public class User {
    private String name, surname, phoneNumber, id, email, passportSeria;

    public User() {
    }

    public User(String name, String surname, String phoneNumber, String id, String email, String passportSeria) {
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.email = email;
        this.passportSeria = passportSeria;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassportSeria() {
        return passportSeria;
    }

    public void setPassportSeria(String passportSeria) {
        this.passportSeria = passportSeria;
    }

        private User(final Builder builder) {
        name = builder.name;
        surname = builder.surname;
        phoneNumber = builder.phoneNumber;
        id = builder.id;
        email = builder.email;
        passportSeria = builder.passportSeria;
    }

    public static class Builder {
        private String name, surname, phoneNumber, id, email, passportSeria;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setSurname(String surname) {
            this.surname = surname;
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

        public Builder setPassportSeries(String passportSeria) {
            this.passportSeria = passportSeria;
            return this;
        }

        public User create() {
            return new User(this);
        }
    }

    public static User fromDataSnapshot(DataSnapshot dataSnapshot, String currentUId) {
        Log.d("MYTAG", "fromDataSnapshot: ");
        //TODO use builder
        User user = new User();
//        user = dataSnapshot.getValue(User.class);
        user.setName((String) dataSnapshot.child(currentUId).child("name").getValue());
        user.setSurname((String) dataSnapshot.child(currentUId).child("surname").getValue());
        user.setEmail((String) dataSnapshot.child(currentUId).child("email").getValue());
        user.setPassportSeria((String) dataSnapshot.child(currentUId).child("passportSeria").getValue());
        user.setPhoneNumber((String) dataSnapshot.child(currentUId).child("phoneNumber").getValue());
        return user;
    }
}
