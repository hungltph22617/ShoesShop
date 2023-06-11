package com.pro.ShoesShop.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.pro.ShoesShop.activity.ChangePasswordActivity;
import com.pro.ShoesShop.activity.MainActivity;
import com.pro.ShoesShop.activity.OrderHistoryActivity;
import com.pro.ShoesShop.activity.SignInActivity;
import com.pro.ShoesShop.activity.SignUpActivity;
import com.pro.ShoesShop.constant.GlobalFunction;
import com.pro.ShoesShop.model.User;
import com.pro.ShoesShop.prefs.DataStoreManager;
import com.pro.ShoesShop.R;
import com.pro.ShoesShop.databinding.FragmentAccountBinding;

public class AccountFragment extends BaseFragment {
    Button btndk, btndn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentAccountBinding fragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false);
        fragmentAccountBinding.btndn.setOnClickListener(v ->  onClickLogin());
        fragmentAccountBinding.btndk.setOnClickListener(v -> onClickSignup());
        fragmentAccountBinding.tvEmail.setText(DataStoreManager.getUser().getEmail());
        fragmentAccountBinding.layoutSignOut.setOnClickListener(v -> onClickSignOut());
        fragmentAccountBinding.layoutChangePassword.setOnClickListener(v -> onClickChangePassword());
        fragmentAccountBinding.layoutOrderHistory.setOnClickListener(v -> onClickOrderHistory());
        return fragmentAccountBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, getString(R.string.account));
        }
    }

    private void onClickOrderHistory() {
        GlobalFunction.startActivity(getActivity(), OrderHistoryActivity.class);
    }

    private void onClickChangePassword() {
        GlobalFunction.startActivity(getActivity(), ChangePasswordActivity.class);
    }
    private void settt(@Nullable User user){
        if(user != null){
            btndk.setVisibility(View.VISIBLE);
            btndn.setVisibility(View.VISIBLE);
        }else{
            btndk.setVisibility(View.VISIBLE);
            btndn.setVisibility(View.VISIBLE);
        }
    }
    private void onClickLogin(){
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        startActivity(intent);
    }
    private void onClickSignup(){
        Intent intent = new Intent(getActivity(), SignUpActivity.class);
        startActivity(intent);
    }

    private void onClickSignOut() {
        if (getActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Bạn có chắc chắn muốn đăng xuất không?");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                DataStoreManager.setUser(null);
                GlobalFunction.startActivity(getActivity(), MainActivity.class);
                getActivity().finishAffinity();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
