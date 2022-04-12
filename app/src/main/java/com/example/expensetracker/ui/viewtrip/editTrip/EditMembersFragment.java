package com.example.expensetracker.ui.viewtrip.editTrip;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentAddMembersBinding;
import com.example.expensetracker.model.User;
import com.example.expensetracker.ui.addtrip.AddMembersAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class EditMembersFragment extends Fragment {
    private FragmentAddMembersBinding binding;
    private EditTripViewModel editTripViewModel;
    private AddMembersAdapter addMembersAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        editTripViewModel = new ViewModelProvider(requireActivity()).get(EditTripViewModel.class);
        binding = FragmentAddMembersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.famRecyclerForSelection.setHasFixedSize(true);
        binding.famRecyclerForSelection.setLayoutManager(new LinearLayoutManager(getContext()));

        addMembersAdapter = new AddMembersAdapter(new ArrayList<>());
        binding.famRecyclerForSelection.setAdapter(addMembersAdapter);

        binding.famButtonSubmit.setOnClickListener(v -> {
            editTripViewModel.addMembers();
            Navigation.findNavController(view).navigate(R.id.action_navigation_add_member_view_to_navigation_edit_trip_view);
        });

        binding.famSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String srcText = s.toString();
                editTripViewModel.searchUserLiveList.setValue(editTripViewModel.allUserList
                        .stream()
                        .filter(f -> filterFriend(f, srcText))
                        .collect(Collectors.toList())
                );
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editTripViewModel.searchUserLiveList.observe(getViewLifecycleOwner(), userList -> {
            addMembersAdapter.updateRecyclerView(userList);
        });

        editTripViewModel.errorLiveMsg.observe(getViewLifecycleOwner(), str -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Error")
                    .setMessage(str)
                    .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        });

    }

    public boolean filterFriend(User user, String srcText){
        if (StringUtils.getLevenshteinDistance(user.getFullName().toLowerCase(), srcText.toLowerCase()) < 4
                || user.getFullName().toLowerCase().contains(srcText.toLowerCase()))
            return true;
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        editTripViewModel.getAllUsers();
    }
}