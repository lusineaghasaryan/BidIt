package com.example.user.bidit.firebase;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewModels.CategoryListViewModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FirebaseHelper {

    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference mUsersRef = database.getReference("users");
    static DatabaseReference mItemsRef = database.getReference("items");
    static DatabaseReference mCategoryRef = database.getReference("categories");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference mItemImages = storageRef.child("itemImages");
    StorageReference mUserAvatar = storageRef.child("avatars");



    public ArrayList<Category> mCategoryList = new ArrayList<>();
    public ArrayList<Item> mItemsList = new ArrayList<>();

    public CategoryListViewModel categoryListViewModel;

    public FirebaseHelper() {}

    public void setCategoryToDatabase(String categoryTitle){
        Category category = new Category();
        String key = mCategoryRef.push().getKey();
        category.setCategoryId(key);
        category.setCategoryTitle(categoryTitle);

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        mCategoryRef.child("category" + n).setValue(category);
    }

    public static void getCategoryListFromDatabase(final Callback<Category> pCallback){

        mCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Category category = messageSnapshot.getValue(Category.class);
                    pCallback.callback(true, category);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pCallback.callback(false, null);
            }
        });
    }

    public void setItemToDatabase(Item item){
        String key = mItemsRef.push().getKey();
        item.setItemId(key);

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        mItemsRef.child("item" + n).setValue(item);
    }


    public static void getItemsListFromDatabase(final Callback<Item> pCallback) {

        mItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot single : dataSnapshot.getChildren()) {
                   Item item = single.getValue(Item.class);
                    pCallback.callback(true, item);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("OOOO", "PPPPP = ");
            }
        });
    }

    public static void getItemsSpecificList(String pType, String pTypeValue, final Callback<Item> pCallback){
        Query query = mItemsRef.orderByChild(pType).equalTo(pTypeValue);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot pDataSnapshot) {

                for (DataSnapshot single : pDataSnapshot.getChildren()) {
                    Item item = single.getValue(Item.class);
                    pCallback.callback(true, item);
                    Log.v("LLLL", "PPPPPP = " + item.getItemTitle());
                }
            }
            @Override
            public void onCancelled(DatabaseError pDatabaseError) {
            }
        });
    }

    public static void getItemsListByFollowersCount(){

    }


    public interface Callback<T> {
        void callback(boolean pIsSuccess, T pValue);
    }
}
