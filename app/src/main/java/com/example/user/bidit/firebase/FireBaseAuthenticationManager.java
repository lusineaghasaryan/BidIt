package com.example.user.bidit.firebase;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.user.bidit.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class FireBaseAuthenticationManager {
    private static FireBaseAuthenticationManager fireBaseAuthenticationManagerInstance;
    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static User mCurrentUser;

    private FireBaseAuthenticationManager() {
    }

    public static FireBaseAuthenticationManager getInstance() {
        if (fireBaseAuthenticationManagerInstance == null) {
            fireBaseAuthenticationManagerInstance = new FireBaseAuthenticationManager();
        }
        return fireBaseAuthenticationManagerInstance;
    }

    public void signIn(String pEmail, String pPassword, final LoginListener pLoginListener) {
        mAuth.signInWithEmailAndPassword(pEmail, pPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseHelper.mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    mCurrentUser = User.fromDataSnapshot(dataSnapshot, mAuth.getCurrentUser().getUid());
                                    pLoginListener.onResponse(true, mCurrentUser);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    pLoginListener.onResponse(false, null);
                                }
                            });
                        } else {
                            pLoginListener.onResponse(false, null);
                        }
                    }
                });
    }

    public void createAccount(final User pUser, String pPassword) {
        mAuth.createUserWithEmailAndPassword(pUser.getEmail(), pPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUserInServer(pUser);
                        }
                    }
                });
    }

    public void updateUserInServer(final User pUser) {
        String currentAuthUserID = mAuth.getCurrentUser().getUid();
        FirebaseHelper.mUsersRef.child(currentAuthUserID).setValue(pUser);
    }

    void setUserPhotoUrl(final String pPhotoUrl) {
        String currentAuthUserID = mAuth.getCurrentUser().getUid();
        FirebaseHelper.mUsersRef.child(currentAuthUserID).child("photoUrl").setValue(pPhotoUrl);
    }

    public void signOut() {
        mCurrentUser = null;
        mAuth.signOut();
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;

    }

    public void initCurrentUser(final LoginListener pLoginListener) {
        FirebaseHelper.mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot pDataSnapshot) {
                mCurrentUser = User.fromDataSnapshot(pDataSnapshot, mAuth.getCurrentUser().getUid());
                pLoginListener.onResponse(true, mCurrentUser);
            }
            @Override
            public void onCancelled(DatabaseError pDatabaseError) {
                pLoginListener.onResponse(false, null);
            }
        });
    }

    public interface LoginListener {
        void onResponse(boolean pSuccess, User pUser);
    }
}
