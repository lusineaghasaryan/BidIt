package com.example.user.bidit.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.AddItemPhotosRVAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.fragments.MultiSelectImageFragment;
import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;
import com.example.user.bidit.utils.DateUtil;
import com.example.user.bidit.utils.UserMessages;
import com.example.user.bidit.utils.ValidateForm;
import com.example.user.bidit.utils.DateUtil;
import com.example.user.bidit.viewModels.CategoryListViewModel;
import com.example.user.bidit.viewModels.ItemsListViewModel;
import com.example.user.bidit.viewModels.ItemsSpecificListVViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AddItemActivity extends AppCompatActivity {

    private static final String IMAGE_DIRECTORY = "/bidit";
    private int REQUEST_IMAGE_GALLERY = 1, REQUEST_IMAGE_CAPTURE = 2;
    public static String mMode;
    public static final String KEY_SAVE_ITEM = "Save";
    public static final String KEY_EDIT_ITEM = "Edit";

    public static final String TAG = "AddItemFragment";

    public Button mSaveItemBtn, mBtnAddFavorite;
    public EditText mItemTitle, mItemDescription, mStartPrice, mBuyNowPrice;
    public Spinner mCategorySpinner;
    public TextView mDateTextView, mEndDateTextView;
    private Calendar mStartDate = Calendar.getInstance();
    private Calendar mEndDate = Calendar.getInstance();
    ArrayAdapter<String> mSpinnerAdapter;
    public List<Category> mCategoryList;
    public String mCategorySelectedItemId;
    public ArrayList<String> mItemSelectedImagesList;
    public ArrayList<String> mItemImagesListStorage;

    private ConstraintLayout mParentLayout;

    public AddItemPhotosRVAdapter mAdapter;
    public RecyclerView mPhotosRV;
    public RecyclerView.LayoutManager mLayoutManager;

    MultiSelectImageFragment multiSelectImageFragment;

    public Item mItemEdit;

    private StorageReference mStorageRef;


    MultiSelectImageFragment.IOnImagesSelectedListener mOnImagesSelectedListener = new MultiSelectImageFragment.IOnImagesSelectedListener() {
        @Override
        public void onImagesSelected(ArrayList<String> selectedImages) {
            mItemSelectedImagesList.addAll(0, selectedImages);
            mPhotosRV.smoothScrollToPosition(mItemSelectedImagesList.size() - 1);
            mAdapter.notifyDataSetChanged();
        }
    };

    AddItemPhotosRVAdapter.IOnAddPhotoListener mIOnAddPhotoListener = new AddItemPhotosRVAdapter.IOnAddPhotoListener() {
        @Override
        public void addPhoto(String pImageUrl) {


            mStorageRef = FirebaseStorage.getInstance().getReference();
            Uri file = Uri.fromFile(new File(pImageUrl));
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            StorageReference riversRef = mStorageRef.child(FireBaseAuthenticationManager.getInstance().mAuth.getCurrentUser().getUid()
                    + "/items/image" + n + ".jpg");

            riversRef.putFile(file)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                            mItemImagesListStorage.add(downloadUrl.toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Log.v(TAG, "Storage chkaaaaa");
                        }
                    });
        }

        @Override
        public void removePhoto(int pPosition) {

        }

        @Override
        public void openGallery() {
            showPictureDialog();
        }
    };

    private void loadExtra() {
        Intent intent = getIntent();
        mItemEdit = (Item) intent.getSerializableExtra(AddItemActivity.KEY_EDIT_ITEM);
        if (mItemEdit != null) {
            //setFieldToEdit(mItemEdit);
            mMode = KEY_EDIT_ITEM;
        } else {
            mMode = KEY_SAVE_ITEM;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mCategoryList = new ArrayList<>();
        mItemSelectedImagesList = new ArrayList<>();
        mItemImagesListStorage = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        init();
        loadExtra();
    }

    public void init() {
        final FirebaseHelper firebaseHelper = new FirebaseHelper();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mParentLayout = findViewById(R.id.add_item_layout);

        mDateTextView = findViewById(R.id.text_view_activity_add_item_start_date);
        mEndDateTextView = findViewById(R.id.text_view_activity_add_item_end_date);
        mSaveItemBtn = findViewById(R.id.btn_activity_add_item_save);
        mItemTitle = findViewById(R.id.edit_text_activity_add_item_name);
        mItemDescription = findViewById(R.id.edit_text_activity_add_item_description);
        mStartPrice = findViewById(R.id.edit_text_activity_add_item_start_price);
        mBuyNowPrice = findViewById(R.id.edit_text_activity_add_item_buy_now_price);
        mCategorySpinner = findViewById(R.id.spinner_activity_add_item_category);
        mPhotosRV = findViewById(R.id.recycler_view_activity_add_item_poto);

        mPhotosRV.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mPhotosRV.setLayoutManager(mLayoutManager);
        mAdapter = new AddItemPhotosRVAdapter(this, mItemSelectedImagesList);
        mAdapter.setIOnAddPhotoListener(mIOnAddPhotoListener);
        mPhotosRV.smoothScrollToPosition(mAdapter.getItemCount());
        mPhotosRV.setAdapter(mAdapter);

        mBtnAddFavorite = findViewById(R.id.add_favorite);
        mBtnAddFavorite.setVisibility(View.GONE);

        updateDateLabel();
        updateDateEndLabel();

        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        mEndDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerEnd();
            }
        });


        //     GET SPECIFIC LIST
        ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders.of(this).get(ItemsSpecificListVViewModel.class);
        /*itemsSpecificListVViewModel.getItem().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item pItem) {
                //TODO get one item & use it
                Log.v(TAG, "item = " + pItem.getItemTitle());
            }
        });
        itemsSpecificListVViewModel.updateData("categoryId", "-LJVutjHBpRf_pfv0pa1");
*/
        itemsSpecificListVViewModel.getItemsList().observe(this, new Observer<ArrayList<Item>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Item> pItems) {
               // Log.v(TAG, "ItemsListCount = " + pItems.size() + "   Title = " + pItems.get(0).getItemTitle());
            }
        });
        itemsSpecificListVViewModel.setItems("categoryId", "-LJVutjHBpRf_pfv0pa1", 1);


        //   GET ALL ITEMS LIST
        ItemsListViewModel itemsListViewModel = ViewModelProviders.of(this).get(ItemsListViewModel.class);
        itemsListViewModel.getItem().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item pItem) {
               // Log.v(TAG, "All items = " + pItem.getItemTitle());
            }
        });
        itemsListViewModel.updateData();


        ///      Spinner
        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        CategoryListViewModel categoryListViewModel = ViewModelProviders.of(this).get(CategoryListViewModel.class);
        categoryListViewModel.getCategoryList().observe(this, new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Category> pCategories) {
                mCategoryList.addAll(pCategories);
                for (int i = 0; i < mCategoryList.size(); i++) {
                    mSpinnerAdapter.add(mCategoryList.get(i).getCategoryTitle());
                }
                if (mMode == KEY_EDIT_ITEM)
                    setFieldToEdit(mItemEdit);
                    setFieldToEdit(mItemEdit);
            }
        });
        categoryListViewModel.updateData();

        mSpinnerAdapter.add("Choose Category");

        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(mSpinnerAdapter);
        mCategorySpinner.setPrompt("Catergory");
        mCategorySpinner.setSelection(0);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position != 0) {
                    mCategorySelectedItemId = findCategoryId(parent.getItemAtPosition(position).toString());
                    mSpinnerAdapter.remove("Choose Category");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        mSaveItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO save item
                EditText[] allFields = createEditTextsArray();
                if (!ValidateForm.setErrorIfEmpty(allFields)) {
                    UserMessages.showSnackBarShort(mParentLayout, getString(R.string.empty_fields_message));
                    return;
                }
                if (mItemImagesListStorage.size() == 0) {
                    UserMessages.showSnackBarShort(mParentLayout, getString(R.string.add_image_messege));
                    return;
                }
                if (mEndDate.getTimeInMillis() <= mStartDate.getTimeInMillis() || mStartDate.getTimeInMillis() < System.currentTimeMillis()) {
                    UserMessages.showSnackBarShort(mParentLayout, getString(R.string.wrong_date_messege));
                    return;
                }
                if (TextUtils.isEmpty(mCategorySelectedItemId)) {
                    UserMessages.showSnackBarShort(mParentLayout, getString(R.string.choose_category_messege));
                    return;
                }
                Item item = new Item.ItemBuilder().setItemTitle(mItemTitle.getText().toString())
                        .setItemDescription(mItemDescription.getText().toString())
                        .setStartPrice(Float.parseFloat(mStartPrice.getText().toString()))
                        .setBuyNowPrice(Float.parseFloat(mBuyNowPrice.getText().toString()))
                        .setCategoryId(mCategorySelectedItemId)
                        .setStartDate(mStartDate.getTime().getTime())
                        .setEndDate(mEndDate.getTime().getTime())
                        .setCurrentPrice(Float.parseFloat(mStartPrice.getText().toString()))
                        .setUserId(FireBaseAuthenticationManager.getInstance().mAuth.getCurrentUser().getUid())
                        .setPhotoUrls(mItemImagesListStorage)
                        .build();

                switch(mMode) {
                    case KEY_SAVE_ITEM:
                        firebaseHelper.setItemToDatabase(item);
                        break;
                    case KEY_EDIT_ITEM:
                        item.setItemId(mItemEdit.getItemId());
                        firebaseHelper.updateItemInDatabase(item, mItemEdit.getItemId());
                        Intent intent = new Intent();
                        intent.putExtra("Item", item);
                        setResult(RESULT_OK, intent);
                        break;
                    default:

                }
                finish();
            }
        });


    }


    //*********************************************************************************************


public void addRemoveFavorite(Item pItem){
    Item item = pItem;
    String currentUserId = FireBaseAuthenticationManager.getInstance().mAuth.getCurrentUser().getUid();
    ArrayList<String> str;

    if(pItem.getFollowersIds() != null)
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



    public void removeFavorite(final Item pItem){
        Item item = pItem;
        String currentUserId = FireBaseAuthenticationManager.getInstance().mAuth.getCurrentUser().getUid();
        ArrayList<String> str;

        if(pItem.getFollowersIds() != null)
            str = pItem.getFollowersIds();
        else
            str = new ArrayList<>();

        if (str.contains(currentUserId)) {
            str.remove(currentUserId);
            item.setFollowersIds(str);
            item.setFollowersCount(item.getFollowersCount() - 1);
            FirebaseHelper.removeFavoriteItem(item);
        }
        else Log.v("KKKK", "Parunakvuma e");
    }

// ***********************************************************************************************************

    private EditText[] createEditTextsArray() {
        return new EditText[]{mItemTitle, mItemDescription, mStartPrice, mBuyNowPrice};
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        multiSelectImageFragment = new MultiSelectImageFragment();
        multiSelectImageFragment.setmOnImagesSelectedListener(mOnImagesSelectedListener);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container_multiselect, multiSelectImageFragment);
        fragmentTransaction.addToBackStack("ADDITEM");
        fragmentTransaction.commit();
    }


    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //TODO save image to internal memory & send Firebase storage
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            String url = saveImage(thumbnail);
            Log.v("PPPPP", "URL = " + url);
            mItemSelectedImagesList.add(url);
            mAdapter.notifyDataSetChanged();
            //TODO save image to internal memory & send Firebase storage
        }
    }

    public String findCategoryId(String title) {
        String id = "";
        for (Category category : mCategoryList) {
            if (category.getCategoryTitle().equals(title)) {
                id = category.getCategoryId();
            }
        }
        return id;
    }
    public String selectCategory(String pCategoryId){
        String categoryTitle = "";
        int position = 0;
        Log.v(TAG, "OOOOO = 1111111" + mCategoryList.size());
        for (Category category : mCategoryList) {
            if (category.getCategoryId().equals(pCategoryId)) {
                categoryTitle = category.getCategoryTitle();
                position = mSpinnerAdapter.getPosition(categoryTitle);
                Log.v(TAG, "OOOOO = " + categoryTitle);
            }
        }
        return categoryTitle;
    }


    public String saveImage(Bitmap myBitmap) {
        Log.d("MYTAG", "saveImage: " + myBitmap);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }


    /////////          Date  &   Time


    private void openDatePicker() {
        new DatePickerDialog(this, mOnDateSetListener, mStartDate.get(Calendar.YEAR),
                mStartDate.get(Calendar.MONTH),
                mStartDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePicker() {
        new TimePickerDialog(this, mOnTimeSetListener, mStartDate.get(Calendar.HOUR_OF_DAY),
                mStartDate.get(Calendar.MINUTE), true).show();
    }

    DatePickerDialog.OnDateSetListener mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mStartDate.set(Calendar.YEAR, year);
            mStartDate.set(Calendar.MONTH, monthOfYear);
            mStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            openTimePicker();
        }
    };


    TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mStartDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mStartDate.set(Calendar.MINUTE, minute);

            updateDateLabel();
        }
    };

    private void updateDateLabel() {
        mDateTextView.setText(DateUtil.formatDateToLongStyle(mStartDate.getTime()));
    }

    private void openDatePickerEnd() {
        new DatePickerDialog(this, mOnDateEndSetListener, mStartDate.get(Calendar.YEAR),
                mEndDate.get(Calendar.MONTH),
                mEndDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePickerEnd() {
        new TimePickerDialog(this, mOnTimeEndSetListener, mStartDate.get(Calendar.HOUR_OF_DAY),
                mEndDate.get(Calendar.MINUTE), true).show();
    }

    DatePickerDialog.OnDateSetListener mOnDateEndSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mEndDate.set(Calendar.YEAR, year);
            mEndDate.set(Calendar.MONTH, monthOfYear);
            mEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            openTimePickerEnd();
            //mDateTextView.setText();
        }
    };


    TimePickerDialog.OnTimeSetListener mOnTimeEndSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mEndDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mEndDate.set(Calendar.MINUTE, minute);

            updateDateEndLabel();
        }
    };

    private void updateDateEndLabel() {
        mEndDateTextView.setText(DateUtil.formatDateToLongStyle(mEndDate.getTime()));
    }



    private void setFieldToEdit(Item pItem){
        mSaveItemBtn.setText("Edit");
        mItemSelectedImagesList.addAll(0, mItemEdit.getPhotoUrls());
        mItemImagesListStorage.addAll(mItemEdit.getPhotoUrls());
        Log.v(TAG, "image = " + mItemEdit.getItemId());
        mPhotosRV.smoothScrollToPosition(mItemSelectedImagesList.size() - 1);
        mAdapter.notifyDataSetChanged();

        mItemTitle.setText(pItem.getItemTitle());
        mItemDescription.setText(pItem.getItemDescription());
        mStartPrice.setText(String.valueOf(pItem.getStartPrice()));
        mBuyNowPrice.setText(String.valueOf(pItem.getBuyNowPrice()));
        //mCategorySpinner.setSelection(selectCategory(pItem.getCategoryId()));


        String myString = selectCategory(pItem.getCategoryId()); //the value you want the position for
        ArrayAdapter myAdap = (ArrayAdapter) mCategorySpinner.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition = myAdap.getPosition(myString);
        mCategorySpinner.setSelection(spinnerPosition);



        mDateTextView.setText(new SimpleDateFormat("MMM/dd 'at' HH:mm")
                .format(pItem.getStartDate()));
        //mStartDate = pItem.getStartDate();
        mStartDate.setTimeInMillis(pItem.getStartDate());
        mEndDateTextView.setText(new SimpleDateFormat("MMM/dd 'at' HH:mm")
                .format(pItem.getEndDate()));
        mEndDate.setTimeInMillis(pItem.getEndDate());

    }
}
