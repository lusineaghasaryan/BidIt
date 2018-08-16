package com.example.user.bidit.firebase;

import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FirebaseHelper {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference mUsersRef = database.getReference("users");
    DatabaseReference mItemsRef = database.getReference("items");
    DatabaseReference mCategoryRef = database.getReference("categories");

    public FirebaseHelper() {
    }

    public void setItemToDatabase(Item item){
        //mItemsRef.setValue(item);
    }

    public void setCategoryToDatabase(String categoryTitle){
        String categoryId = mCategoryRef.push().getKey();
        //Category category = new Category(categoryTitle);
        mCategoryRef.child(categoryId).child("title").setValue(categoryTitle);
    }

    public ArrayList<Category> getCategoryList(){
        ArrayList<Category> categoryArrayList = new ArrayList<>();

        return categoryArrayList;
    }

}
