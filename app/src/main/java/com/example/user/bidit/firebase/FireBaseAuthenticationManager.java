package com.example.user.bidit.firebase;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.user.bidit.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.example.user.bidit.firebase.FirebaseHelper.mUsersRef;

public class FireBaseAuthenticationManager {
    private static FireBaseAuthenticationManager fireBaseAuthenticationManagerInstance;
    public final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static User mCurrentUser;

    private FireBaseAuthenticationManager() {
    }

    public static FireBaseAuthenticationManager getInstance() {
        if (fireBaseAuthenticationManagerInstance == null) {
            fireBaseAuthenticationManagerInstance = new FireBaseAuthenticationManager();
        }
        return fireBaseAuthenticationManagerInstance;
    }


    private void sendEmailVerification(final FirebaseUser firebaseUser) {
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("asd", "onComplete: " + "Verification email sent to " + firebaseUser.getEmail());
                        } else {
                            Log.d("asd", "onComplete: Failed to send verification email.");
                            Log.e("asd", "sendEmailVerification", task.getException());
                        }
                    }
                });
    }

    public void signIn(String pEmail, String pPassword, final LoginListener pLoginListener) {
        mAuth.signInWithEmailAndPassword(pEmail, pPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            sendEmailVerification(mAuth.getCurrentUser());
                            Log.d("asd", "onComplete: " + mAuth.getCurrentUser());
                        }
                    }
                });
    }

    public void updateUserInServer(final User pUser) {
        String currentAuthUserID = mAuth.getCurrentUser().getUid();
        mUsersRef.child(currentAuthUserID).setValue(pUser);
    }

    void setUserPhotoUrl(final String pPhotoUrl) {
        String currentAuthUserID = mAuth.getCurrentUser().getUid();
        mUsersRef.child(currentAuthUserID).child("photoUrl").setValue(pPhotoUrl);
    }

    public void updateUserBalance(final String pBalance) {
        String currentAuthUserID = mAuth.getCurrentUser().getUid();
        mUsersRef.child(currentAuthUserID).child("ballance").setValue(pBalance);
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
        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void getUserById(final String pUId, final LoginListener pLoginListener) {
        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot pDataSnapshot) {
                User user = User.fromDataSnapshot(pDataSnapshot, pUId);
                pLoginListener.onResponse(true, user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError pDatabaseError) {
            }
        });
    }

    public interface LoginListener {
        void onResponse(boolean pSuccess, User pUser);
    }
}
