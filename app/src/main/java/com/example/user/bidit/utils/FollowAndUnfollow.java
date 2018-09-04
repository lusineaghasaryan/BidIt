package com.example.user.bidit.utils;


import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Item;

import java.util.ArrayList;

public class FollowAndUnfollow {

    public static void addRemoveFavorite(Item pItem) {
        Item item = pItem;
        String currentUserId = FireBaseAuthenticationManager.getInstance().mAuth.getCurrentUser().getUid();
        ArrayList<String> str;
        if (pItem.getFollowersIds() != null)
            str = pItem.getFollowersIds();
        else
            str = new ArrayList<>();
        ///////     add ////////
        if (!str.contains(currentUserId)) {
            str.add(currentUserId);
            item.setFollowersIds(str);
            item.setFollowersCount(item.getFollowersCount() + 1);
            FirebaseHelper.addFavoriteItem(item);
        }///////// remove /////////
        else {
            str.remove(currentUserId);
            item.setFollowersIds(str);
            item.setFollowersCount(item.getFollowersCount() - 1);
            FirebaseHelper.removeFavoriteItem(item);
        }
    }

}
