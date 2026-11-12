package com.vidhika.zepto;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AuthManager {
    private static final String PREF_NAME = "zepto_auth_pref";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_PHONE = "user_phone_number";

    public interface AuthCallback {
        void onSuccess(String phoneNumber);
        void onFailure(String error);
    }

    public static boolean isLoggedIn(Context context) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean locallyLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return locallyLoggedIn || (firebaseUser != null);
    }

    public static String getPhoneNumber(Context context) {
        if (context == null) return "+91 9876543210";
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String phone = prefs.getString(KEY_PHONE, null);
        if (phone != null && !phone.isEmpty()) {
            return phone;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.getPhoneNumber() != null && !firebaseUser.getPhoneNumber().isEmpty()) {
                return firebaseUser.getPhoneNumber();
            }
            if (firebaseUser.getDisplayName() != null && !firebaseUser.getDisplayName().isEmpty()) {
                return firebaseUser.getDisplayName();
            }
        }
        return "+91 8103126630";
    }

    public static void signInWithPhoneOnly(Context context, String phoneNumber, AuthCallback callback) {
        String formattedPhone = phoneNumber.startsWith("+91") ? phoneNumber : "+91 " + phoneNumber;

        // Persist local authenticated session
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_PHONE, formattedPhone)
                .apply();

        // Connect with Firebase Authentication session
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // Already has a Firebase user session, attach phone to profile
            FirebaseUser user = mAuth.getCurrentUser();
            user.updateProfile(new UserProfileChangeRequest.Builder()
                    .setDisplayName(formattedPhone)
                    .build());
            if (callback != null) callback.onSuccess(formattedPhone);
        } else {
            // Sign in anonymously in Firebase without SMS/OTP
            mAuth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FirebaseUser user = task.getResult().getUser();
                            if (user != null) {
                                user.updateProfile(new UserProfileChangeRequest.Builder()
                                        .setDisplayName(formattedPhone)
                                        .build());
                            }
                        }
                        // Always succeed for the user so they can proceed immediately
                        if (callback != null) callback.onSuccess(formattedPhone);
                    });
        }
    }

    public static void signOut(Context context) {
        try {
            FirebaseAuth.getInstance().signOut();
        } catch (Exception ignored) {}

        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
        }
    }
}

