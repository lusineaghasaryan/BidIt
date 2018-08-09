package com.example.user.bidit.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.user.bidit.firebase.FirebaseHelper;
import com.example.user.bidit.models.Category;
import com.example.user.bidit.models.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;

public class AddItemFragment extends Fragment {

    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int REQUEST_IMAGE_GALLERY  = 1, REQUEST_IMAGE_CAPTURE  = 2;

    public Button mAddPhotoBtn, mSaveItemBtn;
    public EditText mItemTitle, mItemDescription, mStartPrice, mBuyNowPrice;
    public Spinner mCategorySpinner;
    public ImageView mImageView;
    public TextView mDateTextView, mTimeTextView;
    private Calendar mSelectedDate = Calendar.getInstance();
    public ArrayList<Category> mCategoryList;
    public ArrayList<String> mItemSelectedImages;

    public AddItemPhotosRVAdapter mAdapter;
    public RecyclerView mPhotosRV;
    public RecyclerView.LayoutManager mLayoutManager;

//    public final String filesDir = getContext().getFilesDir().getAbsolutePath();

    public Item mItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mCategoryList = new ArrayList<>();
        mItemSelectedImages = new ArrayList<>();
        mItem = new Item();

        mItemSelectedImages.add("/storage/emulated/0/Slack/IMG_20180804_234229.jpg");
        mItemSelectedImages.add("/storage/emulated/0/Slack/IMG_20180804_234338.jpg");
        mItemSelectedImages.add("/storage/emulated/0/Slack/IMG_20180805_184317.jpg");
        mItemSelectedImages.add("/storage/emulated/0/Slack/IMG_20180805_184317.jpg");
        mItemSelectedImages.add("/storage/emulated/0/Slack/IMG_20180805_184335.jpg");


        //SelectedImagesViewModel model = ViewModelProviders.of(getActivity()).get(SelectedImagesViewModel.class);

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
        mAddPhotoBtn = view.findViewById(R.id.btn_fragment_add_item_add_photo);
        mDateTextView = view.findViewById(R.id.text_view_fragment_add_item_date);
        mTimeTextView = view.findViewById(R.id.text_view_fragment_add_item_time);
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
        mAdapter = new AddItemPhotosRVAdapter(getContext(), mItemSelectedImages);
        mPhotosRV.setAdapter(mAdapter);

        //mCategoryList.add(new Category(1, "Ardzanner"));
        //mCategoryList.add(new Category(123, "Cars"));
        //mCategoryList.add(new Category(125, "Kopekner"));
        //mCategoryList.add(new Category(127, "Furnichure"));

        FirebaseHelper firebaseHelper = new FirebaseHelper();

        firebaseHelper.setCategoryToDatabase("Ardzanner");
        firebaseHelper.setCategoryToDatabase("Cars");
        firebaseHelper.setCategoryToDatabase("Kopekner");
        firebaseHelper.setCategoryToDatabase("Furnichure");

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

        mTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });


        ///      Spinner
        final ArrayAdapter<String> mSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

       /* mSpinnerAdapter.add("Choose Category");
        mSpinnerAdapter.add(mCategoryList.get(0).getCategoryTitle());
        mSpinnerAdapter.add(mCategoryList.get(1).getCategoryTitle());
        mSpinnerAdapter.add(mCategoryList.get(2).getCategoryTitle());
        mSpinnerAdapter.add(mCategoryList.get(3).getCategoryTitle());

        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
*/
        mCategorySpinner.setAdapter(mSpinnerAdapter);
        mCategorySpinner.setPrompt("Catergory");
        mCategorySpinner.setSelection(0);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            if (position != 0){
                   // mItem.setCategoryId(id);
                Log.v(AddItemFragment.class.getName(), "Category id = " + id);
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
        /*Intent galleryIntent = new Intent(Intent.ACTION_PICK,//EXTRA_ALLOW_MULTIPLE,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY);*/

        MultiSelectImageFragment multiSelectImageFragment = new MultiSelectImageFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.add(R.id.fragment_container, multiSelectImageFragment, "MultiSelectFragment");
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
            mImageView.setImageBitmap(thumbnail);
            //TODO save image to internal memory & send Firebase storage
        }
    }


    /*
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

    }*/




    /////////          Date  &   Time


    private void openDatePicker() {
        new DatePickerDialog(getActivity(), mOnDateSetListener, mSelectedDate.get(Calendar.YEAR),
                mSelectedDate.get(Calendar.MONTH),
                mSelectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openTimePicker() {
        new TimePickerDialog(getActivity(), mOnTimeSetListener, mSelectedDate.get(Calendar.HOUR_OF_DAY),
                mSelectedDate.get(Calendar.MINUTE), true).show();
    }

    DatePickerDialog.OnDateSetListener mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mSelectedDate.set(Calendar.YEAR, year);
            mSelectedDate.set(Calendar.MONTH, monthOfYear);
            mSelectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            //mDateTextView.setText();
        }
    };


    TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mSelectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mSelectedDate.set(Calendar.MINUTE, minute);

            //updateDateLabel();
        }
    };

}
