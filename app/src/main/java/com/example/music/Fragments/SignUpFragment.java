package com.example.music.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignUpFragment extends Fragment {

    private TextView alreadyHaveAnAcount;
    private FrameLayout frameLayout;
    private Drawable errorIcon;

    private EditText txtFullName;
    private EditText txtUserName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private Button btnSignUp;
    private ProgressBar prbSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        alreadyHaveAnAcount = view.findViewById(R.id.already_have_account);
        frameLayout = getActivity().findViewById(R.id.register_frame_layout);
        errorIcon = getResources().getDrawable(R.drawable.ic_error);

        txtFullName = view.findViewById(R.id.txtFullName);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPassword = view.findViewById(R.id.txtPassword);
        txtConfirmPassword = view.findViewById(R.id.txtPasswordConfirm);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        prbSignUp = view.findViewById(R.id.prbSignUp);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorIcon.setBounds(0,0,errorIcon.getIntrinsicWidth(),errorIcon.getIntrinsicHeight());

        alreadyHaveAnAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });
        txtFullName.addTextChangedListener(new TextWatcher() {
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
        txtUserName.addTextChangedListener(new TextWatcher() {
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
        txtPassword.addTextChangedListener(new TextWatcher() {
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
        txtConfirmPassword.addTextChangedListener(new TextWatcher() {
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

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpWithFireBase();
                setButtonEnabledAndColor(false);
            }
        });
    }
    private void signUpWithFireBase() {
        String fullName = txtFullName.getText().toString();
        String userName = txtUserName.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        if (isValidEmail(email)) {
            if (isPasswordMatch(password, confirmPassword)) {
                prbSignUp.setVisibility(View.VISIBLE);

                checkUserNameAvailability(userName, new OnUserNameCheckListener() {
                    @Override
                    public void onUserNameCheck(boolean isAvailable) {
                        if (isAvailable) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            prbSignUp.setVisibility(View.INVISIBLE);
                                            if (task.isSuccessful()) {
                                                String userId = mAuth.getCurrentUser().getUid();

                                                Map<String, Object> user = new HashMap<>();
                                                user.put("fullName", fullName);
                                                user.put("userName", userName);
                                                user.put("email", email);

                                                db.collection("users").document(userId)
                                                        .set(user)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    saveLoginInfo(fullName,userName,email);
                                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                    getActivity().startActivity(intent);
                                                                    getActivity().finish();
                                                                } else {
                                                                    Toast.makeText(getContext(), "Lỗi tạo tài khoảm", Toast.LENGTH_LONG).show();
                                                                    setButtonEnabledAndColor(true);
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getContext(),"Email đã được sử dụng", Toast.LENGTH_LONG).show();
                                                setButtonEnabledAndColor(true);
                                            }
                                        }
                                    });
                        } else {
                            txtUserName.setError("Tên người dùng đã được sử dụng", errorIcon);
                            setButtonEnabledAndColor(true);
                        }
                    }
                });
            } else {
                txtConfirmPassword.setError("Mật khẩu không khớp", errorIcon);
                setButtonEnabledAndColor(true);
            }
        } else {
            txtEmail.setError("Email không hợp lệ", errorIcon);
            setButtonEnabledAndColor(true);
        }
    }
    private void saveLoginInfo(String fullName, String userName, String email) {
        SharedPreferences preferences = getActivity().getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("fullName", fullName);
        editor.putString("userName", userName);
        editor.putString("email", email);
        editor.apply();
        editor.apply();
    }
    private void checkUserNameAvailability(String userName, OnUserNameCheckListener listener) {
        db.collection("users")
                .whereEqualTo("userName", userName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean isUserNameAvailable = task.getResult().isEmpty();
                            listener.onUserNameCheck(isUserNameAvailable);
                        } else {
                            listener.onUserNameCheck(false);
                        }
                    }
                });
    }

    interface OnUserNameCheckListener {
        void onUserNameCheck(boolean isAvailable);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.from_left,R.anim.out_from_right);
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void checkInput() {
        if (isFieldNotEmpty(txtFullName)
                && isUserNameValid(txtUserName)
                && isFieldNotEmpty(txtEmail)
                && isPasswordValid(txtPassword)
                && isFieldNotEmpty(txtConfirmPassword)) {
            setButtonEnabledAndColor(true);
        } else {
            setButtonEnabledAndColor(false);
        }
    }
    private boolean isUserNameValid(EditText editText) {
        String fullName = editText.getText().toString().trim();
        return !fullName.isEmpty() && fullName.matches("[a-zA-Z]+");
    }

    private void setButtonEnabledAndColor(boolean enabled) {
        btnSignUp.setEnabled(enabled);
        int textColor = enabled ? R.color.white : R.color.transWhite;
        btnSignUp.setTextColor(getResources().getColor(textColor));
    }

    private boolean isFieldNotEmpty(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }

    private boolean isPasswordValid(EditText editText) {
        String password = editText.getText().toString();
        return !password.isEmpty() && password.length() >= 6;
    }
    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    }

    private boolean isPasswordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

}