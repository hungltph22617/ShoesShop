package com.pro.ShoesShop.fragment.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.pro.ShoesShop.ControllerApplication;
import com.pro.ShoesShop.activity.AddProductActivity;
import com.pro.ShoesShop.activity.AdminMainActivity;
import com.pro.ShoesShop.adapter.AdminProductAdapter;
import com.pro.ShoesShop.constant.GlobalFunction;
import com.pro.ShoesShop.listener.IOnManagerFoodListener;
import com.pro.ShoesShop.model.Food;
import com.pro.ShoesShop.utils.StringUtil;
import com.pro.ShoesShop.R;
import com.pro.ShoesShop.constant.Constant;
import com.pro.ShoesShop.databinding.FragmentAdminHomeBinding;
import com.pro.ShoesShop.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends BaseFragment {

    private FragmentAdminHomeBinding mFragmentAdminHomeBinding;
    private List<Food> mListFood;
    private AdminProductAdapter mAdminProductAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminHomeBinding = FragmentAdminHomeBinding.inflate(inflater, container, false);

        initView();
        initListener();
        getListFood("");
        return mFragmentAdminHomeBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((AdminMainActivity) getActivity()).setToolBar(getString(R.string.home));
        }
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentAdminHomeBinding.rcvFood.setLayoutManager(linearLayoutManager);
        mListFood = new ArrayList<>();
        mAdminProductAdapter = new AdminProductAdapter(mListFood, new IOnManagerFoodListener() {
            @Override
            public void onClickUpdateFood(Food food) {
                onClickEditFood(food);
            }

            @Override
            public void onClickDeleteFood(Food food) {
                deleteFoodItem(food);
            }
        });
        mFragmentAdminHomeBinding.rcvFood.setAdapter(mAdminProductAdapter);
    }

    private void initListener() {
        mFragmentAdminHomeBinding.btnAddFood.setOnClickListener(v -> onClickAddFood());

        mFragmentAdminHomeBinding.imgSearch.setOnClickListener(view1 -> searchFood());

        mFragmentAdminHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFood();
                return true;
            }
            return false;
        });

        mFragmentAdminHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    searchFood();
                }
            }
        });
    }

    private void onClickAddFood() {
        GlobalFunction.startActivity(getActivity(), AddProductActivity.class);
    }

    private void onClickEditFood(Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_FOOD_OBJECT, food);
        GlobalFunction.startActivity(getActivity(), AddProductActivity.class, bundle);
    }

    private void deleteFoodItem(Food food) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    ControllerApplication.get(getActivity()).getFoodDatabaseReference()
                            .child(String.valueOf(food.getId())).removeValue((error, ref) ->
                            Toast.makeText(getActivity(),
                                    getString(R.string.msg_delete_movie_successfully), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchFood() {
        String strKey = mFragmentAdminHomeBinding.edtSearchName.getText().toString().trim();
        if (mListFood != null) {
            mListFood.clear();
        } else {
            mListFood = new ArrayList<>();
        }
        getListFood(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    public void getListFood(String keyword) {
        if (getActivity() == null) {
            return;
        }
        ControllerApplication.get(getActivity()).getFoodDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null || mAdminProductAdapter == null) {
                            return;
                        }
                        if (StringUtil.isEmpty(keyword)) {
                            mListFood.add(0, food);
                        } else {
                            if (GlobalFunction.getTextSearch(food.getName()).toLowerCase().trim()
                                    .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                                mListFood.add(0, food);
                            }
                        }
                        mAdminProductAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null
                                || mListFood.isEmpty() || mAdminProductAdapter == null) {
                            return;
                        }
                        for (int i = 0; i < mListFood.size(); i++) {
                            if (food.getId() == mListFood.get(i).getId()) {
                                mListFood.set(i, food);
                                break;
                            }
                        }
                        mAdminProductAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Food food = dataSnapshot.getValue(Food.class);
                        if (food == null || mListFood == null
                                || mListFood.isEmpty() || mAdminProductAdapter == null) {
                            return;
                        }
                        for (Food foodObject : mListFood) {
                            if (food.getId() == foodObject.getId()) {
                                mListFood.remove(foodObject);
                                break;
                            }
                        }
                        mAdminProductAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}
