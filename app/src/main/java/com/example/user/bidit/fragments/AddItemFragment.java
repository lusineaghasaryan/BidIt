package com.example.user.bidit.fragments;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.AddItemPhotosRVAdapter;
import com.example.user.bidit.firebase.FireBaseAuthenticationManager;
import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.app.Activity.RESULT_CANCELED;

public class AddItemFragment extends Fragment {

    private static final String IMAGE_DIRECTORY = "/bidit";
    private int REQUEST_IMAGE_GALLERY  = 1, REQUEST_IMAGE_CAPTURE  = 2;

    public static final String TAG = "AddItemFragment";

    public Button mAddPhotoBtn, mSaveItemBtn;
    public EditText mItemTitle, mItemDescription, mStartPrice, mBuyNowPrice;
    public Spinner mCategorySpinner;
    public ImageView mImageView;
    public TextView mDateTextView, mEndDateTextView;
    private Calendar mStartDate = Calendar.getInstance();
    private Calendar mEndDate = Calendar.getInstance();
    public List<Category> mCategoryList;
    public String mCategorySelectedItemId;
    public ArrayList<String> mItemSelectedImagesList;
    public ArrayList<String> mItemImagesListStorage;

    public AddItemPhotosRVAdapter mAdapter;
    public RecyclerView mPhotosRV;
    public RecyclerView.LayoutManager mLayoutManager;

    MultiSelectImageFragment multiSelectImageFragment;

    public Item mItem;

    private StorageReference mStorageRef;


    MultiSelectImageFragment.IOnImagesSelectedListener mOnImagesSelectedListener = new MultiSelectImageFragment.IOnImagesSelectedListener() {
        @Override
        public void onImagesSelected(ArrayList<String> selectedImages) {
            mItemSelectedImagesList.addAll(selectedImages);
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
                            Uri downloadUrl = taskSnapshot.getUploadSessionUri();//.getDownloadUrl();
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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mCategoryList = new ArrayList<>();
        mItemSelectedImagesList = new ArrayList<>();
        mItemImagesListStorage = new ArrayList<>();

        setHasOptionsMenu(true);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void init(View view){
        final FirebaseHelper firebaseHelper = new FirebaseHelper();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mAddPhotoBtn = view.findViewById(R.id.btn_fragment_add_item_add_photo);
        mDateTextView = view.findViewById(R.id.text_view_fragment_add_item_start_date);
        mEndDateTextView = view.findViewById(R.id.text_view_fragment_add_item_end_date);
        mSaveItemBtn = view.findViewById(R.id.btn_fragment_add_item_save);
        mItemTitle = view.findViewById(R.id.edit_text_fragment_add_item_name);
        mItemDescription = view.findViewById(R.id.edit_text_fragment_add_item_description);
        mStartPrice = view.findViewById(R.id.edit_text_fragment_add_item_start_price);
        mBuyNowPrice = view.findViewById(R.id.edit_text_fragment_add_item_buy_now_price);
        mCategorySpinner = view.findViewById(R.id.spinner_fragment_add_item_category);
        mImageView = view.findViewById(R.id.image_view_fragment_add_item_image);
        mPhotosRV= view.findViewById(R.id.recycler_view_fragment_add_item_poto);

        mPhotosRV.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mPhotosRV.setLayoutManager(mLayoutManager);
        mAdapter = new AddItemPhotosRVAdapter(getContext(), mItemSelectedImagesList);
        mAdapter.setIOnAddPhotoListener(mIOnAddPhotoListener);
        mPhotosRV.smoothScrollToPosition(mAdapter.getItemCount());
        mPhotosRV.setAdapter(mAdapter);


        updateDateLabel();
        updateDateEndLabel();

        mAddPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

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
        ItemsSpecificListVViewModel itemsSpecificListVViewModel = ViewModelProviders.of(getActivity()).get(ItemsSpecificListVViewModel.class);
        itemsSpecificListVViewModel.getItem().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item pItem) {
                //TODO get one item & use it
            }
        });
        itemsSpecificListVViewModel.updateData("categoryId", "-LJVutjJhnc_JqmS7kAs");



        //   GET ALL ITEMS LIST
        ItemsListViewModel itemsListViewModel = ViewModelProviders.of(getActivity()).get(ItemsListViewModel.class);
        itemsListViewModel.getItem().observe(this, new Observer<Item>() {
            @Override
            public void onChanged(@Nullable Item pItem) {

            }
        });
        itemsListViewModel.setItems();




        ///      Spinner
        final ArrayAdapter<String> mSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        CategoryListViewModel categoryListViewModel = ViewModelProviders.of(getActivity()).get(CategoryListViewModel.class);
        /*categoryListViewModel.getCategory().observe(this, new Observer<Category>() {
            @Override
            public void onChanged(@Nullable Category category) {
                mCategoryList.add(category);
                mSpinnerAdapter.add(category.getCategoryTitle());
            }
        });*/
        categoryListViewModel.updateData();

        mSpinnerAdapter.add("Choose Category");
        for (int i = 0; i < mCategoryList.size(); i++){
            mSpinnerAdapter.add(mCategoryList.get(i).getCategoryTitle());
        }

        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(mSpinnerAdapter);
        mCategorySpinner.setPrompt("Catergory");
        mCategorySpinner.setSelection(0);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            if (position != 0){
                mCategorySelectedItemId = findCategoryId(parent.getItemAtPosition(position).toString());
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
               Item item = new Item.ItemBuilder().setItemTitle(mItemTitle.getText().toString())
                       .setItemDescription(mItemDescription.getText().toString())
                       .setStartPrice(Float.parseFloat(mStartPrice.getText().toString()))
                       .setBuyNowPrice(Float.parseFloat(mBuyNowPrice.getText().toString()))
                       .setCategoryId(mCategorySelectedItemId)
                       .setStartDate(mStartDate.getTime().getTime())
                       .setEndDate(mEndDate.getTime().getTime())
                       //todo test
                       .setUserId(FireBaseAuthenticationManager.getInstance().mAuth.getCurrentUser().getUid())
                       .setPhotoUrls(mItemImagesListStorage)
                       .build();
                firebaseHelper.setItemToDatabase(item);
            }
        });
    }



    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
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
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, multiSelectImageFragment);
        fragmentTransaction.addToBackStack("ADDITEM");
        fragmentTransaction.commit();
    }



    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_IMAGE_GALLERY){
            if (data != null){
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                    mImageView.setImageBitmap(bitmap);
                    //TODO save image to internal memory & send Firebase storage
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE){
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            mItemSelectedImagesList.add(saveImage(thumbnail));
            mAdapter.notifyDataSetChanged();
            //TODO save image to internal memory & send Firebase storage
        }
    }

    public String findCategoryId(String title){
        Log.v("LLLL", "title = "+ title);
        String id = "";
        for (Category category : mCategoryList) {
            if (category.getCategoryTitle().equals(title)) {
                id =  category.getCategoryId();
            }
        }
        return id;
    }


    public String saveImage(Bitmap myBitmap){
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
            MediaScannerConnection.scanFile(getContext(),
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
        new DatePickerDialog(getActivity(), mOnDateSetListener, mStartDate.get(Calendar.YEAR),
                mStartDate.get(Calendar.MONTH),
                mStartDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePicker() {
        new TimePickerDialog(getActivity(), mOnTimeSetListener, mStartDate.get(Calendar.HOUR_OF_DAY),
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
        new DatePickerDialog(getActivity(), mOnDateEndSetListener, mStartDate.get(Calendar.YEAR),
                mEndDate.get(Calendar.MONTH),
                mEndDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePickerEnd() {
        new TimePickerDialog(getActivity(), mOnTimeEndSetListener, mStartDate.get(Calendar.HOUR_OF_DAY),
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
}
