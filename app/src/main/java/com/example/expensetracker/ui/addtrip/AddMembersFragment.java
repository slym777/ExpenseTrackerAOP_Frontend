package com.example.expensetracker.ui.addtrip;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AddMembersFragment extends Fragment {
    private FragmentAddMembersBinding binding;
    private AddTripViewModel addTripViewModel;
    private AddMembersAdapter addMembersAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addTripViewModel = new ViewModelProvider(requireActivity()).get(AddTripViewModel.class);
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
            addTripViewModel.addMembers();
            Navigation.findNavController(view).navigate(R.id.action_navigation_add_members_to_navigation_add_trip);
        });

        binding.famSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String srcText = s.toString();
                addTripViewModel.searchUserLiveList.setValue(addTripViewModel.allUserList
                        .stream()
                        .filter(f -> filterFriend(f, srcText))
                        .collect(Collectors.toList())
                );
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addTripViewModel.searchUserLiveList.observe(getViewLifecycleOwner(), userList -> {
            addMembersAdapter.updateRecyclerView(userList);
        });

        addTripViewModel.errorLiveMsg.observe(getViewLifecycleOwner(), str -> {
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
        addTripViewModel.getAllUsers();
    }
}