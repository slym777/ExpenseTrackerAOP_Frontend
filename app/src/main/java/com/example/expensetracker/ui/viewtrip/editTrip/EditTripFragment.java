package com.example.expensetracker.ui.viewtrip.editTrip;

import static android.app.Activity.RESULT_OK;
import static com.example.expensetracker.utils.ConstantsUtils.CAMERA_REQUEST_CODE;
import static com.example.expensetracker.utils.ConstantsUtils.GALLERY_REQUEST_CODE;
import static com.example.expensetracker.utils.ConstantsUtils.PERMISSION_REQUEST_CAMERA;
import static com.example.expensetracker.utils.ConstantsUtils.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE;
import static com.example.expensetracker.utils.ConstantsUtils.TRIP_ID_EXTRA;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentEditTripBinding;
import com.example.expensetracker.model.User;
import com.example.expensetracker.ui.addtrip.SelectedUsersAdapter;
import com.example.expensetracker.ui.trips.OnClickRemoveSelectedUserListener;
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

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class EditTripFragment extends Fragment implements OnClickRemoveSelectedUserListener {
    FragmentEditTripBinding binding;
    EditTripViewModel editTripViewModel;
    private FirebaseStorage storage;
    private SelectedUsersAdapter selectedUsersAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        editTripViewModel = new ViewModelProvider(requireActivity()).get(EditTripViewModel.class);

        Long tripId = getActivity().getIntent().getLongExtra(TRIP_ID_EXTRA, -1);
        editTripViewModel.tripId = tripId;

        binding = FragmentEditTripBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        storage = FirebaseStorage.getInstance();
        binding.recyclerSelectedUsers.setHasFixedSize(true);
        binding.recyclerSelectedUsers.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false)
        );

        editTripViewModel.tripLive.observe(getViewLifecycleOwner(), trip -> {
            if (TextUtils.isEmpty(editTripViewModel.name)) {
                editTripViewModel.name = trip.getName();
                editTripViewModel.description = trip.getDescription();
                editTripViewModel.location = trip.getLocation();
                editTripViewModel.avatarUri = trip.getAvatarUri();

                setEnableSaveButton(false);
                editTripViewModel.setSelectedUsers(trip.getUsers());
                setBinding();
            }
        });

        setBinding();

        addTextChangedListener(binding.tripNameText);
        addTextChangedListener(binding.tripDescText);
        addTextChangedListener(binding.tripLocationText);

        selectedUsersAdapter = new SelectedUsersAdapter(new ArrayList<>(), this);
        binding.recyclerSelectedUsers.setAdapter(selectedUsersAdapter);

        editTripViewModel.selectedUserList.observe(getViewLifecycleOwner(), userList -> {
            selectedUsersAdapter.updateRecyclerView(userList);
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

        binding.tripAvatarImageView.setOnClickListener(l -> {
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

        binding.buttonAddMembers.setOnClickListener(v -> {
            editTripViewModel.name = binding.tripNameText.getText().toString();
            editTripViewModel.description = binding.tripDescText.getText().toString();
            editTripViewModel.location = binding.tripLocationText.getText().toString();
            setEnableSaveButton(true);

            Navigation.findNavController(view).navigate(R.id.action_navigation_edit_trip_view_to_navigation_add_member_view);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_trip_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.edit_trip_save_button);
        if (editTripViewModel.enableSaveButton) {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        } else {
            item.setEnabled(false);
            item.getIcon().setAlpha(220);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_trip_save_button) {
            saveChanges();
            setEnableSaveButton(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setEnableSaveButton(boolean value) {
        editTripViewModel.enableSaveButton = value;
        requireActivity().invalidateOptionsMenu();
    }

    private void saveChanges() {
        if (editTripViewModel.selectedUserList.getValue() == null || editTripViewModel.selectedUserList.getValue().isEmpty()) {
            Toast.makeText(getContext(), "Cannot have a trip with no members", Toast.LENGTH_SHORT).show();
            return ;
        }

        List<User> users = new ArrayList<>();
        users.add(SharedPreferencesUtils.getProfileDetails());
        users.addAll(editTripViewModel.selectedUserList.getValue());

        try {
            editTripViewModel.updateTrip(
                    binding.tripNameText.getText().toString(),
                    binding.tripDescText.getText().toString(),
                    editTripViewModel.avatarUri,
                    binding.tripLocationText.getText().toString(),
                    users
            ).subscribe(bool -> {
                                editTripViewModel.name = binding.tripNameText.getText().toString();
                                editTripViewModel.description = binding.tripDescText.getText().toString();
                                editTripViewModel.location = binding.tripLocationText.getText().toString();

                                Timber.d("Trip was updated");
                                Navigation.findNavController(requireView()).navigate(R.id.action_navigation_edit_trip_view_to_navigation_trip_view);
                                Toast.makeText(getContext(), "Trip successfully updated", Toast.LENGTH_SHORT).show();
                            }, error -> new AlertDialog.Builder(getActivity())
                                    .setTitle("Error while updating trip")
                                    .setMessage(error.getLocalizedMessage())
                                    .show()
                    );
        } catch (JSONException e) {
            Timber.d(e.getMessage());
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error while updating trip")
                    .setMessage(e.getMessage())
                    .show();
        }
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
                setEnableSaveButton(true);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setBinding() {
        binding.tripNameText.setText(editTripViewModel.name);
        binding.tripDescText.setText(editTripViewModel.description);
        binding.tripLocationText.setText(editTripViewModel.location);
        if (!TextUtils.isEmpty(editTripViewModel.avatarUri)) {
            Glide.with(BaseApp.context)
                    .load(editTripViewModel.avatarUri)
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(binding.tripAvatarImageView);
        } else {
            Glide.with(BaseApp.context).clear(binding.tripAvatarImageView);
            binding.tripAvatarImageView.setImageResource(R.drawable.default_trip_back);
        }
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
            binding.tripAvatarImageView.setImageURI(uri);
            uploadTask = avatarRef.putFile(uri, metadata);
        } else {
            binding.tripAvatarImageView.setImageBitmap(bitmap);

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
                editTripViewModel.avatarUri = downloadUri.toString();
                setEnableSaveButton(true);
            }

            binding.loadingProgressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void removeUser(User user) {
        List<User> tempList = editTripViewModel.selectedUserList.getValue();
        tempList.remove(user);
        editTripViewModel.selectedUserList.postValue(tempList);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (editTripViewModel.tripLive.getValue() == null || editTripViewModel.tripLive.getValue().getId() == null) {
            editTripViewModel.getTripById();
        }
    }
}
