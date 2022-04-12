package com.example.expensetracker.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.databinding.FragmentLoginBinding;
import com.example.expensetracker.utils.BaseApp;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText email = binding.emailLoginEditText;
        EditText password = binding.passwordLoginEditText;
        binding.loginButton.setOnClickListener(b ->
                loginViewModel.login(email.getText().toString(), password.getText().toString())
        );

        binding.signupButton.setOnClickListener(b ->
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment)
        );

        binding.loginResetPasswordButton.setOnClickListener(b ->
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_resetPassword)
        );

        loginViewModel.loginState.subscribe(bool -> {
            if (bool) {
                Toast.makeText(BaseApp.context, "Authentication succeeded", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                Toast.makeText(BaseApp.context, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }, err -> Toast.makeText(BaseApp.context, err.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
