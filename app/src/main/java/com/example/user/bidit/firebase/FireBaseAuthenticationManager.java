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

    public void signIn(String email, String password, final LoginListener loginListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseHelper.mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d("MYTAG", "onDataChange: signIn" + mAuth.getCurrentUser().getUid());
                                    mCurrentUser = User.fromDataSnapshot(dataSnapshot, mAuth.getCurrentUser().getUid());
                                    loginListener.onResponse(true, mCurrentUser);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    loginListener.onResponse(false, null);
                                }
                            });
                        } else {
                            loginListener.onResponse(false, null);
                        }
                    }
                });
    }

    public void createAccount(final User user, String password) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUserInServer(user);
                        }
                    }
                });
    }

    public void updateUserInServer(final User user) {
        String currentAuthUserID = mAuth.getCurrentUser().getUid();
        FirebaseHelper.mUsersRef.child(currentAuthUserID).setValue(user);
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

    public void initCurrentUser() {
        FirebaseHelper.mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("MYTAG", "onDataChange: initCurrentUser");
                mCurrentUser = User.fromDataSnapshot(dataSnapshot, mAuth.getCurrentUser().getUid());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public interface LoginListener {
        void onResponse(boolean pSuccess, User pUser);
    }

}
