package com.example.expensetracker.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentSignupBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.example.expensetracker.utils.ConstantsUtils.CAMERA_REQUEST_CODE;
import static com.example.expensetracker.utils.ConstantsUtils.GALLERY_REQUEST_CODE;
import static com.example.expensetracker.utils.ConstantsUtils.PERMISSION_REQUEST_CAMERA;
import static com.example.expensetracker.utils.ConstantsUtils.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE;

public class SignUpFragment extends Fragment {
    SignUpViewModel signUpViewModel;
    private FragmentSignupBinding binding;
    private Boolean validInputs;
    private FirebaseStorage storage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        signUpViewModel = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storage = FirebaseStorage.getInstance();

        binding.fsContinueButton.setOnClickListener(b -> {
            validInputs = true;
            if (TextUtils.isEmpty(binding.fsFirstNameEditText.getText())) {
                binding.fsFirstNameEditText.setError("First name is required!");
                validInputs = false;
            }

            if (TextUtils.isEmpty(binding.fsLastNameEditText.getText())) {
                binding.fsLastNameEditText.setError("Last name is required!");
                validInputs = false;
            }

            if (validInputs) {
                signUpViewModel.saveUserDetails(
                        binding.fsFirstNameEditText.getText().toString(),
                        binding.fsLastNameEditText.getText().toString()
                );

                Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_emailPasswordFragment);
            }
        });

        binding.fsSignupCameraButton.setOnClickListener(l -> {
            showCameraPreview();
        });

        binding.fsSignupGalleryButton.setOnClickListener(l -> {
            showGalleryPreview();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCameraPreview();
            } else {
                Snackbar.make(binding.getRoot(), R.string.camera_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }

        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showGalleryPreview();
            } else {
                Snackbar.make(binding.getRoot(), R.string.gallery_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void showCameraPreview() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera, CAMERA_REQUEST_CODE);
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {

            Snackbar.make(binding.getRoot(), R.string.camera_access_required,
                    Snackbar.LENGTH_LONG).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    private void requestGalleryPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Snackbar.make(binding.getRoot(), R.string.gallery_access_required,
                    Snackbar.LENGTH_LONG).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private void showGalleryPreview(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST_CODE);
        } else {
            requestGalleryPermission();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super method removed
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri uri = data.getData();
                uploadToFirebaseStorage(uri, null, true);
            }

            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap bitmapImage = (Bitmap) data.getExtras().get("data");
                uploadToFirebaseStorage(null, bitmapImage, false);
            }

        }
    }

    public void uploadToFirebaseStorage(Uri uri, Bitmap bitmap, Boolean isUri){
        UploadTask uploadTask;
        String path = "/avatars/" + UUID.randomUUID() + ".png";
        StorageReference avatarRef = storage.getReference(path);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("avatar", "New User")
                .build();

        if (isUri) {
            binding.fsSignupAvatarImageView.setImageURI(uri);
            uploadTask = avatarRef.putFile(uri, metadata);
        } else {
            binding.fsSignupAvatarImageView.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            uploadTask = avatarRef.putBytes(imageBytes, metadata);

        }

        binding.fsProgressBar.setVisibility(View.VISIBLE);
        binding.fsSignupCameraButton.setEnabled(false);
        binding.fsSignupGalleryButton.setEnabled(false);

        uploadTask.addOnCompleteListener(getActivity(), task -> {
            Timber.d("The image has been uploaded");

            binding.fsProgressBar.setVisibility(View.GONE);
            binding.fsSignupCameraButton.setEnabled(true);
            binding.fsSignupGalleryButton.setEnabled(true);
        });

        Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(
                task -> {
                    if (!task.isSuccessful())
                        throw task.getException();

                    return avatarRef.getDownloadUrl();
                }
        );

        getDownloadUriTask.addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()){
                Uri downloadUri = task.getResult();
                signUpViewModel.saveDownloadUri(downloadUri);
            }

            binding.fsProgressBar.setVisibility(View.GONE);
            binding.fsSignupCameraButton.setEnabled(true);
            binding.fsSignupGalleryButton.setEnabled(true);

//            binding.fsContinueButton.setVisibility(View.VISIBLE);
//            binding.fsContinueButton.setEnabled(true);
        });

    }

}
