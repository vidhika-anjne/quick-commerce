package com.vidhika.zepto;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public EditText phoneInput;
    public Button continueButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        if (AuthManager.isLoggedIn(this)) {
            navigateToHome(AuthManager.getPhoneNumber(this));
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialize offline cache managers
        CartManager.getInstance(this);
        ProductCacheManager.loadProducts(this);

        initViews();
        setupTermsText();
        setupClickListeners();
    }

    private void initViews() {
        phoneInput = findViewById(R.id.phoneInput);
        continueButton = findViewById(R.id.continueButton);
        progressBar = findViewById(R.id.progress_bar_auth);
    }

    private void setupTermsText() {
        TextView termsText = findViewById(R.id.termText);
        if (termsText != null) {
            String fullText = "By continuing, you agree to our \n Terms of Use & Privacy Policy";
            SpannableString spannable = new SpannableString(fullText);
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF7050")),
                    32, fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            termsText.setText(spannable);
        }
    }

    private void setupClickListeners() {
        continueButton.setOnClickListener(v -> {
            String phoneNumber = phoneInput.getText().toString().trim();

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(MainActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phoneNumber.length() < 10) {
                Toast.makeText(MainActivity.this, "Phone number must be at least 10 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            setLoading(true);

            // Direct sign-in with phone number — no SMS or OTP required
            AuthManager.signInWithPhoneOnly(MainActivity.this, phoneNumber, new AuthCallbackHandler(phoneNumber));
        });
    }

    private class AuthCallbackHandler implements AuthManager.AuthCallback {
        private final String phone;

        public AuthCallbackHandler(String phone) {
            this.phone = phone;
        }

        @Override
        public void onSuccess(String formattedPhone) {
            setLoading(false);
            CartManager.getInstance(MainActivity.this).switchUser();
            Toast.makeText(MainActivity.this, "⚡ Welcome to Zepto!", Toast.LENGTH_SHORT).show();
            navigateToHome(formattedPhone);
        }

        @Override
        public void onFailure(String error) {
            setLoading(false);
            CartManager.getInstance(MainActivity.this).switchUser();
            // Even if background sync has an issue, proceed with formatted phone
            navigateToHome("+91 " + phone);
        }
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (continueButton != null) {
            continueButton.setEnabled(!isLoading);
        }
        if (phoneInput != null) {
            phoneInput.setEnabled(!isLoading);
        }
    }

    private void navigateToHome(String phoneNumber) {
        Intent intent = new Intent(MainActivity.this, ProductPage.class);
        intent.putExtra("phone_number", phoneNumber);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
