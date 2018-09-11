package com.example.user.bidit.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.viewModels.CategoryListViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class FirebaseHelper {

    static FirebaseDatabase database = FirebaseDatabase.getInstance();
    static DatabaseReference mUsersRef = database.getReference("users");
    static DatabaseReference mItemsRef = database.getReference("items");
    static DatabaseReference mCategoryRef = database.getReference("categories");
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static StorageReference storageRef = storage.getReference();
    StorageReference mItemImages = storageRef.child("itemImages");
    StorageReference mUserAvatar = storageRef.child("avatars");

    public static final int TOTAL_ITEMS_TO_LOAD = 5;

    public ArrayList<Category> mCategoryList = new ArrayList<>();
    public ArrayList<Item> mItemsList = new ArrayList<>();

    public CategoryListViewModel categoryListViewModel;

    public FirebaseHelper() {
    }

    public void setCategoryToDatabase(String categoryTitle) {
        Category category = new Category();
        String key = mCategoryRef.push().getKey();
        category.setCategoryId(key);
        category.setCategoryTitle(categoryTitle);

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        mCategoryRef.child("category" + n).setValue(category);
    }

    public static void getCategoryListFromDatabase(final Callback<ArrayList<Category>> pCallback) {

        mCategoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Category> tempList = new ArrayList<>();
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Category category = messageSnapshot.getValue(Category.class);
                    tempList.add(category);
                }
                pCallback.callback(true, tempList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pCallback.callback(false, null);
            }
        });
    }

    public void setItemToDatabase(Item item) {
        String key = mItemsRef.push().getKey();
        item.setItemId(key);

        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        mItemsRef.child(item.getItemId()).setValue(item);//"item" + n
    }

    public static void updateItemInDatabase(Item pItem, String pItemId) {
        Log.v("LLLLL", "pItemId = " + pItemId);
        mItemsRef.child(pItemId).setValue(pItem);
    }


    public static void getItemsListFromDatabase(final Callback<ArrayList<Item>> pCallback) {

        mItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Item> temp = new ArrayList<>();
                for (DataSnapshot single : dataSnapshot.getChildren()) {
                    Item item = single.getValue(Item.class);
                    temp.add(item);
                }
                pCallback.callback(true, temp);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("OOOO", "PPPPP = ");
            }
        });
    }

    public static void getItemsSpecificList(String pType, String pTypeValue, int pPageNumber, final Callback<Item> pCallback) {
        Query query = mItemsRef.orderByChild(pType).equalTo(pTypeValue);
//        Query query = mItemsRef.orderByChild(pType).startAt(pTypeValue);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot pDataSnapshot) {

                for (DataSnapshot single : pDataSnapshot.getChildren()) {
                    Item item = single.getValue(Item.class);
                    pCallback.callback(true, item);
                }
            }

            @Override
            public void onCancelled(DatabaseError pDatabaseError) {
            }
        });
    }

    public static void getItemsSpecific(String pType, String pTypeValue, int pPageNumber, final Callback<ArrayList<Item>> pCallback) {
        Query query = mItemsRef.orderByChild(pType).equalTo(pTypeValue).limitToFirst(TOTAL_ITEMS_TO_LOAD * pPageNumber);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot pDataSnapshot) {
                ArrayList<Item> temp = new ArrayList<>();
                for (DataSnapshot single : pDataSnapshot.getChildren()) {
                    Item item = single.getValue(Item.class);
                    temp.add(item);
                }
                pCallback.callback(true, temp);
            }

            @Override
            public void onCancelled(DatabaseError pDatabaseError) {
            }
        });
    }


    public static void getHotItemsList(String pType, final Callback<ArrayList<Item>> pCallback) {
        Query query = mItemsRef.orderByChild(pType);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot pDataSnapshot) {
                ArrayList<Item> temp = new ArrayList<>();
                for (DataSnapshot single : pDataSnapshot.getChildren()) {
                    Item item = single.getValue(Item.class);
                    temp.add(item);
                }
                pCallback.callback(true, temp);
            }

            @Override
            public void onCancelled(DatabaseError pDatabaseError) {
            }
        });
    }


    public static void getItemListBySearch(String pType, String pTypeValue, int pPageNumber, final Callback<Item> pCallback) {
        Query query = mItemsRef.orderByChild(pType).startAt(pTypeValue);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot pDataSnapshot) {

                for (DataSnapshot single : pDataSnapshot.getChildren()) {
                    Item item = single.getValue(Item.class);
                    pCallback.callback(true, item);
                }
            }

            @Override
            public void onCancelled(DatabaseError pDatabaseError) {
            }
        });
    }

    public static void addFavoriteItem(Item pItem) {
        mItemsRef.child(pItem.getItemId()).child("followersIds").setValue(pItem.getFollowersIds());
        mItemsRef.child(pItem.getItemId()).child("followersCount").setValue(pItem.getFollowersCount());
    }

    public static void removeFavoriteItem(Item pItem) {

        mItemsRef.child(pItem.getItemId()).child("followersIds").setValue(pItem.getFollowersIds());
        mItemsRef.child(pItem.getItemId()).child("followersCount").setValue(pItem.getFollowersCount());
    }

    public static void removeItem(Item pItem) {
        mItemsRef.child(pItem.getItemId()).removeValue();
    }

    public static void sendAvatarToStorage(String pImageUrl) {
        storageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(pImageUrl));
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        StorageReference riversRef = storageRef.child(FireBaseAuthenticationManager.getInstance().mAuth.getCurrentUser().getUid()
                + "/user/avatar" + n + ".jpg");
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();//.getDownloadUrl();
                        FireBaseAuthenticationManager.getInstance().setUserPhotoUrl(downloadUrl.toString());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    public static void getMyFavoriteList(final String pUserId, final Callback<ArrayList<Item>> pCallback) {
        Query query = mItemsRef.orderByChild("followersIds");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot pDataSnapshot) {
                ArrayList<Item> temp = new ArrayList<>();
                for (DataSnapshot single : pDataSnapshot.getChildren()) {
                    Item item = single.getValue(Item.class);
                    if (item.getFollowersIds() != null) {
                        if (item.getFollowersIds().contains(pUserId)) {
                            temp.add(item);
                        }
                    }
                }
                pCallback.callback(true, temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError pDatabaseError) {

            }
        });
    }

    public interface Callback<T> {
        void callback(boolean pIsSuccess, T pValue);
    }
}
