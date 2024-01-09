package com.example.music.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.music.Activities.SplashActivity;
import com.example.music.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

public class ProfileFragment extends Fragment {

    private TextView textViewEmail;
    private Button btnChangePassword;
    private Button btnLogout;
    private static String savedEmail;
    private static String savedPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        textViewEmail = view.findViewById(R.id.textViewEmail);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        SharedPreferences preferences = getActivity().getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        savedEmail = preferences.getString("email", "");
        textViewEmail.setText(savedEmail);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        return view;
    }

    private void showChangePasswordDialog() {
        ChangePasswordDialogFragment dialogFragment = new ChangePasswordDialogFragment();
        dialogFragment.show(getChildFragmentManager(), "ChangePasswordDialogFragment");
    }

    public static class ChangePasswordDialogFragment extends DialogFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.change_password_dialog, container, false);

            EditText editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
            EditText editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
            Button btnConfirmChange = view.findViewById(R.id.btnConfirmChange);

            btnConfirmChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentPassword = editTextCurrentPassword.getText().toString();
                    String newPassword = editTextNewPassword.getText().toString();

                    if (!currentPassword.isEmpty() && !newPassword.isEmpty()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> reauthTask) {
                                            if (reauthTask.isSuccessful()) {
                                                user.updatePassword(newPassword)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getContext(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                                                    dismiss();
                                                                } else {
                                                                    Toast.makeText(getContext(), "Không thể thay đổi mật khẩu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getContext(), "Xác thực không thành công. Vui lòng kiểm tra lại mật khẩu hiện tại.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "Người dùng chưa được xác thực. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Vui lòng nhập mật khẩu hiện tại và mật khẩu mới", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return view;
        }
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences preferences = getActivity().getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();
        Intent intent = new Intent(getActivity(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

}
