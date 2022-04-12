package com.example.expensetracker.ui.addtrip;

import static android.app.Activity.RESULT_OK;
import static com.example.expensetracker.utils.ConstantsUtils.CAMERA_REQUEST_CODE;
import static com.example.expensetracker.utils.ConstantsUtils.GALLERY_REQUEST_CODE;
import static com.example.expensetracker.utils.ConstantsUtils.PERMISSION_REQUEST_CAMERA;
import static com.example.expensetracker.utils.ConstantsUtils.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentAddTripBinding;
import com.example.expensetracker.model.User;
import com.example.expensetracker.ui.trips.OnClickRemoveSelectedUserListener;
import com.example.expensetracker.utils.BaseApp;
import com.example.expensetracker.utils.SharedPreferencesUtils;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
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

public class AddTripFragment extends Fragment implements OnClickRemoveSelectedUserListener {
    private FragmentAddTripBinding binding;
    private AddTripViewModel addTripViewModel;
    private FirebaseStorage storage;
    private SelectedUsersAdapter selectedUsersAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                         ViewGroup container, Bundle savedInstanceState) {
        addTripViewModel = new ViewModelProvider(requireActivity()).get(AddTripViewModel.class);
        binding = FragmentAddTripBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        storage = FirebaseStorage.getInstance();
        binding.recyclerSelectedUsers.setHasFixedSize(true);
        binding.recyclerSelectedUsers.setLayoutManager(
                new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false)
        );

        binding.fatTripNameText.setText(addTripViewModel.name);
        binding.fatTripDescText.setText(addTripViewModel.description);
        binding.fatTripLocationText.setText(addTripViewModel.location);
        if (!TextUtils.isEmpty(addTripViewModel.avatarUri)) {
            Glide.with(BaseApp.context)
                    .load(addTripViewModel.avatarUri)
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .into(binding.tripAvatarImageView);
        } else {
            Glide.with(BaseApp.context).clear(binding.tripAvatarImageView);
            binding.tripAvatarImageView.setImageResource(R.drawable.default_trip_back);
        }

        selectedUsersAdapter = new SelectedUsersAdapter(new ArrayList<>(), this);
        binding.recyclerSelectedUsers.setAdapter(selectedUsersAdapter);

        addTripViewModel.selectedUserList.observe(getViewLifecycleOwner(), userList -> {
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
            addTripViewModel.name = binding.fatTripNameText.getText().toString();
            addTripViewModel.description = binding.fatTripDescText.getText().toString();
            addTripViewModel.location = binding.fatTripLocationText.getText().toString();

            Navigation.findNavController(view).navigate(R.id.action_navigation_add_trip_to_navigation_add_members);
        });

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_trip_menu, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_button) {
            addTrip();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addTrip() {
        if (addTripViewModel.selectedUserList.getValue() == null || addTripViewModel.selectedUserList.getValue().isEmpty()) {
            Toast.makeText(getContext(), "Cannot create a trip with no members", Toast.LENGTH_SHORT).show();
            return ;
        }

        List<User> users = new ArrayList<>();
        users.add(SharedPreferencesUtils.getProfileDetails());
        users.addAll(addTripViewModel.selectedUserList.getValue());

        try {
            addTripViewModel.createTrip(binding.fatTripNameText.getText().toString(),
                    binding.fatTripDescText.getText().toString(), addTripViewModel.avatarUri,
                    binding.fatTripLocationText.getText().toString(), users)
                    .subscribe(bool -> {
                                Timber.d("Created a new trip");
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }, error -> new AlertDialog.Builder(getActivity())
                                    .setTitle("Error")
                                    .setMessage(error.getLocalizedMessage())
                                    .show()
                    );
        } catch (JSONException e) {
            Timber.d(e.getMessage());
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error")
                    .setMessage(e.getMessage())
                    .show();
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
                addTripViewModel.avatarUri = downloadUri.toString();
            }

            binding.loadingProgressBar.setVisibility(View.GONE);
        });
    }


    @Override
    public void removeUser(User user) {
        List<User> tempList = addTripViewModel.selectedUserList.getValue();
        tempList.remove(user);
        addTripViewModel.selectedUserList.postValue(tempList);
    }
}
