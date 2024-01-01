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
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInFragment extends Fragment {

    private TextView dontHaveAnAccount;
    private TextView resetPassword;
    private FrameLayout frameLayout;
    private Drawable errorIcon;

    private EditText txtUserName;
    private EditText txtPassword;
    private Button btnSignIn;
    private ProgressBar prbSignIn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sign_in, container, false);
        dontHaveAnAccount = view.findViewById(R.id.dont_have_an_account);
        resetPassword = view.findViewById(R.id.reset_password);
        frameLayout = getActivity().findViewById(R.id.register_frame_layout);
        errorIcon = getResources().getDrawable(R.drawable.ic_error);

        txtUserName = view.findViewById(R.id.txtUserName);
        txtPassword = view.findViewById(R.id.txtPassword);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        prbSignIn = view.findViewById(R.id.prbSignIn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorIcon.setBounds(0,0,errorIcon.getIntrinsicWidth(),errorIcon.getIntrinsicHeight());

        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new ResetPasswordFragment());
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

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithFirebase();
                setButtonEnabledAndColor(false);
            }
        });
    }

    private void signInWithFirebase() {
        String userNameOrEmail = txtUserName.getText().toString();
        String password = txtPassword.getText().toString();

        if (userNameOrEmail.contains("@")) {
            signInWithEmail(userNameOrEmail, password);
        } else {
            signInWithUserName(userNameOrEmail, password);
        }
    }

    private void signInWithEmail(String email, String password) {
        prbSignIn.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        prbSignIn.setVisibility(View.INVISIBLE);

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        } else {
                            Toast.makeText(getContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            setButtonEnabledAndColor(true);
                        }
                    }
                });
    }

    private void signInWithUserName(String userName, String password) {
        prbSignIn.setVisibility(View.VISIBLE);

        db.collection("users")
                .whereEqualTo("userName", userName)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        prbSignIn.setVisibility(View.INVISIBLE);

                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                String userEmail = querySnapshot.getDocuments().get(0).getString("email");

                                if (userEmail != null && !userEmail.isEmpty()) {
                                    signInWithEmail(userEmail, password);
                                } else {
                                    Toast.makeText(getContext(), "Không tìm thấy email cho tài khoản này", Toast.LENGTH_SHORT).show();
                                    setButtonEnabledAndColor(true);
                                }
                            } else {
                                Toast.makeText(getContext(), "Tên tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                                setButtonEnabledAndColor(true);
                            }
                        } else {
                            Toast.makeText(getContext(), "Lỗi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
                            setButtonEnabledAndColor(true);
                        }
                    }
                });
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.from_right,R.anim.out_from_left);
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void checkInput() {
        if (isFieldNotEmpty(txtUserName) && isPasswordValid(txtPassword)) {
            setButtonEnabledAndColor(true);
        } else {
            setButtonEnabledAndColor(false);
        }
    }

    private boolean isFieldNotEmpty(EditText editText) {
        return !editText.getText().toString().isEmpty();
    }

    private boolean isPasswordValid(EditText editText) {
        String password = editText.getText().toString();
        return !password.isEmpty() && password.length() >= 6;
    }

    private void setButtonEnabledAndColor(boolean enabled) {
        btnSignIn.setEnabled(enabled);
        int textColor = enabled ? R.color.white : R.color.transWhite;
        btnSignIn.setTextColor(getResources().getColor(textColor));
    }
}
