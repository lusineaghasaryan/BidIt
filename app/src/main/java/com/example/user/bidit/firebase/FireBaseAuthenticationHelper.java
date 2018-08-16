package com.example.user.bidit.firebase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.user.bidit.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class FireBaseAuthenticationHelper {
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseUser mFirebaseUser;
    private static User mCurrentUser;

    public static void signIn(String email, String password, final LoginListener loginListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mFirebaseUser = mAuth.getCurrentUser();
                            FirebaseHelper.mUsersRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                       mCurrentUser = User.fromDataSnapshot(dataSnapshot, mFirebaseUser.getUid());
                                       loginListener.onResponse(true, mCurrentUser);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    loginListener.onResponse(false, null);
                                }
                            });
                        } else {
                            mFirebaseUser = null;
                        }
                    }
                });
    }

    public static void createAccount(final User user, String password) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String currentUserID = mAuth.getCurrentUser().getUid();
                            FirebaseHelper.mUsersRef.child(currentUserID).setValue(user);
                        }
                    }
                });
    }

    public static void signOut() {
        mAuth.signOut();
    }

    public static User getmCurrentUser() {
        return mCurrentUser;
    }

    public static boolean isLoggedIn() {
        return mCurrentUser != null;
    }

    public interface LoginListener {
        void onResponse(boolean pSuccess, User pUser);
    }

}
