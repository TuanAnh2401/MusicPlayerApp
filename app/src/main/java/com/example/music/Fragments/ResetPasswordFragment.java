package com.example.music.Fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music.Activities.MainActivity;
import com.example.music.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordFragment extends Fragment {
    private TextView back;
    private FrameLayout frameLayout;
    private Drawable errorIcon;

    private EditText txtEmail;
    private ProgressBar prbResetPassword;
    private TextView lbResetPassword;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_reset_password, container, false);
        back = view.findViewById(R.id.back);
        frameLayout = getActivity().findViewById(R.id.register_frame_layout);
        errorIcon = getResources().getDrawable(R.drawable.ic_error);

        txtEmail = view.findViewById(R.id.txtEmail);
        prbResetPassword = view.findViewById(R.id.prbResetPassword);
        lbResetPassword = view.findViewById(R.id.lbResetPassword);
        btnResetPassword = view.findViewById(R.id.btnResetPassword);

        mAuth = FirebaseAuth.getInstance();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorIcon.setBounds(0,0,errorIcon.getIntrinsicWidth(),errorIcon.getIntrinsicHeight());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInput();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }
    private void resetPassword() {
        String email = txtEmail.getText().toString();

        if (isValidEmail(email)) {
            prbResetPassword.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(txtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        lbResetPassword.setText("Kiểm tra Email của bạn");
                        lbResetPassword.setTextColor(getResources().getColor(R.color.green));
                    }else {
                        lbResetPassword.setText("Xảy ra vấn đề khi gửi Email");
                        lbResetPassword.setTextColor(getResources().getColor(R.color.red));
                    }
                    prbResetPassword.setVisibility(View.INVISIBLE);
                    lbResetPassword.setVisibility(View.VISIBLE);
                }
            });
        } else {
            txtEmail.setError("Email không hợp lệ",errorIcon);
            setButtonEnabledAndColor(true);
        }
    }
    private void checkInput() {
        if (isFieldNotEmpty(txtEmail)) {
            setButtonEnabledAndColor(true);
        } else {
            setButtonEnabledAndColor(false);
        }
    }
    private void setButtonEnabledAndColor(boolean enabled) {
        btnResetPassword.setEnabled(enabled);
        int textColor = enabled ? R.color.white : R.color.transWhite;
        btnResetPassword.setTextColor(getResources().getColor(textColor));
    }
    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    }
    private boolean isFieldNotEmpty(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.from_left,R.anim.out_from_right);
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}