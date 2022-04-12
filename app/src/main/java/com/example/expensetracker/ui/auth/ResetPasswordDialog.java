package com.example.expensetracker.ui.auth;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.expensetracker.databinding.DialogResetPasswordBinding;

import static android.content.Context.WINDOW_SERVICE;

public class ResetPasswordDialog extends DialogFragment {
    private DialogResetPasswordBinding binding;

    public static ResetPasswordDialog newInstance() {
        Bundle args = new Bundle();
        ResetPasswordDialog fragment = new ResetPasswordDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DialogResetPasswordBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            WindowManager manager = (WindowManager) requireActivity().getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;

            this.getDialog().getWindow().setLayout((int) (width * 0.8), (int) (height * 0.45));
//
        }
    }
}
