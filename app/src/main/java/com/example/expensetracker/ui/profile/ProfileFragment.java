package com.example.expensetracker.ui.profile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentProfileBinding;
import com.example.expensetracker.model.User;
import com.example.expensetracker.ui.auth.AuthenticationActivity;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.SharedPreferencesUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
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

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private FirebaseStorage storage;
    private Boolean enableSaveButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        enableSaveButton = false;
        // initialize with old values
        profileViewModel.buildUser(SharedPreferencesUtils.getProfileDetails());

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        storage = FirebaseStorage.getInstance();

        User user = SharedPreferencesUtils.getProfileDetails();

        binding.firstNameEditText.setText(user.getFullName().split(" ")[0]);
        binding.lastNameEditText.setText(user.getFullName().split(" ")[1]);
        binding.emailEditText.setText(user.getEmail());
        binding.phonenumberEditText.setText(user.getPhoneNumber());

        if (!TextUtils.isEmpty(user.getAvatarUri())) {
            Glide.with(BaseApp.context)
                    .load(user.getAvatarUri())
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(binding.profileAvatarImageView);
        } else {
            Glide.with(BaseApp.context).clear(binding.profileAvatarImageView);
            binding.profileAvatarImageView.setImageResource(R.drawable.default_user_avatar);
        }

        addTextChangedListener(binding.emailEditText);
        addTextChangedListener(binding.phonenumberEditText);

        binding.fpSignoutButton.setOnClickListener(b -> {
            SharedPreferencesUtils.clearProfileDetails();

            Toast.makeText(getContext(), "You have been signed out.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getContext(), AuthenticationActivity.class);
            startActivity(intent);
        });

        binding.addPhotoImageView.setOnClickListener(l -> {
            String[] items = new String[]{"Camera", "Gallery"};

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Choose your option")
                    .setItems(items, (dialog, which) -> {
                        if (which == 0){
                            showCameraPreview();
                        } else {
                            showGalleryPreview();
                        }
                    }).show();
        });


        binding.profileAvatarImageView.setOnClickListener(l -> {
            String[] items = new String[]{"Camera", "Gallery"};

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Choose your option")
                    .setItems(items, (dialog, which) -> {
                        if (which == 0){
                            showCameraPreview();
                        } else {
                            showGalleryPreview();
                        }
                    }).show();
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_view_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.save_button);
        if (enableSaveButton) {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        } else {
            item.setEnabled(false);
            item.getIcon().setAlpha(220);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_button) {
            boolean state = saveChanges();
            if (state) {
                enableSaveButton = false;
                requireActivity().invalidateOptionsMenu();
                clearFocusAndCloseKeyboard();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean saveChanges() {
        boolean validInputs = true;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String phoneNumberPattern = "(^$|[0-9]{10})";

        if (TextUtils.isEmpty(binding.emailEditText.getText())) {
            binding.emailEditText.setError("Email is required!");
            validInputs = false;
        } else if (!binding.emailEditText.getText().toString().trim().matches(emailPattern)) {
            binding.emailEditText.setError("Invalid email address: wrong format.");
            validInputs = false;
        } else if (!TextUtils.equals(binding.emailEditText.getText(), SharedPreferencesUtils.getEmail())) {
            profileViewModel.setNewEmail(binding.emailEditText.getText().toString());
        }

        if (TextUtils.isEmpty(binding.phonenumberEditText.getText())) {
            binding.phonenumberEditText.setError("Phone number is required!");
            validInputs = false;
        } else if (!binding.phonenumberEditText.getText().toString().matches(phoneNumberPattern)) {
            binding.phonenumberEditText.setError("Invalid phone number: wrong format.");
            validInputs = false;
        } else if (!TextUtils.equals(binding.phonenumberEditText.getText(), SharedPreferencesUtils.getPhoneNumber())){
            profileViewModel.setNewPhoneNumber(binding.phonenumberEditText.getText().toString());
        }

        if (validInputs) {
            profileViewModel.updateProfile();
        }

        return validInputs;
    }

    public void clearFocusAndCloseKeyboard () {
        InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(requireView().getApplicationWindowToken(), 0);
        binding.emailEditText.clearFocus();
        binding.phonenumberEditText.clearFocus();
    }

    private void addTextChangedListener(TextInputEditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSaveButton = true;
                requireActivity().invalidateOptionsMenu();
            }
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

    public void uploadToFirebaseStorage(Uri uri, Bitmap bitmap, Boolean isUri) {
        UploadTask uploadTask;
        String path = "/avatars/" + UUID.randomUUID() + ".png";
        StorageReference avatarRef = storage.getReference(path);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("avatar", "New User")
                .build();

        if (isUri) {
            binding.profileAvatarImageView.setImageURI(uri);
            uploadTask = avatarRef.putFile(uri, metadata);
        } else {
            binding.profileAvatarImageView.setImageBitmap(bitmap);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            uploadTask = avatarRef.putBytes(imageBytes, metadata);
        }

        binding.loadingProgressBar.setVisibility(View.VISIBLE);

        uploadTask.addOnCompleteListener(getActivity(), task -> {
            Timber.d("The image has been uploaded");
            binding.loadingProgressBar.setVisibility(View.GONE);

        });

        Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(
                task -> {
                    if (!task.isSuccessful())
                        throw task.getException();

                    return avatarRef.getDownloadUrl();
                }
        );

        getDownloadUriTask.addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                profileViewModel.setNewAvatarUri(downloadUri);
                enableSaveButton = true;
                requireActivity().invalidateOptionsMenu();
            }

            binding.loadingProgressBar.setVisibility(View.GONE);
        });
    }
}