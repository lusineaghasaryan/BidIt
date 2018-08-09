package com.example.user.bidit.fragments;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.bidit.R;
import com.example.user.bidit.adapters.MultiSelectImageAdapter;

import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MultiSelectImageFragment extends Fragment {

    private MultiSelectImageAdapter multiSelectImageAdapter;
    private static final int REQUEST_FOR_STORAGE_PERMISSION = 123;
    public Button mSelectedImagesbtn;
    public IOnImagesSelectedListener mOnImagesSelectedListener;

    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multiselect_image, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        mSelectedImagesbtn = view.findViewById(R.id.btn_fragment_multiselect_image);
        mSelectedImagesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ArrayList<String> selectedItems = multiSelectImageAdapter.getCheckedItems();

                ArrayList<String> selectedItems = multiSelectImageAdapter.getSelectedItemsList();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
                if (selectedItems!= null && selectedItems.size() > 0) {
                    Toast.makeText(getContext(), "Total photos selected: " + selectedItems.size(), Toast.LENGTH_SHORT).show();
                    Log.d(MultiSelectImageFragment.class.getSimpleName(), "Selected Items: " + selectedItems.toString());
                }
            }
        });
        populateImagesFromGallery();
    }


    private void populateImagesFromGallery() {
        if (!mayRequestGalleryImages()) {
            return;
        }

        ArrayList<String> imageUrls = loadPhotosFromNativeGallery();
        initializeRecyclerView(imageUrls, mView);
    }

    private boolean mayRequestGalleryImages() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            //promptStoragePermission();
            showPermissionRationaleSnackBar();
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_FOR_STORAGE_PERMISSION);
        }

        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_FOR_STORAGE_PERMISSION: {

                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        populateImagesFromGallery();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), READ_EXTERNAL_STORAGE)) {
                            showPermissionRationaleSnackBar();
                        } else {
                            Toast.makeText(getContext(), "Go to settings and enable permission", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                break;
            }
        }
    }

    private ArrayList<String> loadPhotosFromNativeGallery() {
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = getActivity().managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");

        ArrayList<String> imageUrls = new ArrayList<String>();

        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));
        }

        return imageUrls;
    }

    private void initializeRecyclerView(ArrayList<String> imageUrls, View view) {
        multiSelectImageAdapter = new MultiSelectImageAdapter(getContext(), imageUrls);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),4);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_fragment_multiselect);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // recyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_offset));
        recyclerView.setAdapter(multiSelectImageAdapter);
    }

    private void showPermissionRationaleSnackBar() {

        Snackbar.make(mView.findViewById(R.id.btn_fragment_multiselect_image), getString(R.string.permission_rationale),
                Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{READ_EXTERNAL_STORAGE},
                        REQUEST_FOR_STORAGE_PERMISSION);
            }
        }).show();

    }

    public void setmOnImagesSelectedListener(IOnImagesSelectedListener mOnImagesSelectedListener) {
        this.mOnImagesSelectedListener = mOnImagesSelectedListener;
    }

    public interface IOnImagesSelectedListener {
        void onImagesSelected(ArrayList<String> selectedImages);
        void onImagesRemovedFormList(int position);
    }

}
