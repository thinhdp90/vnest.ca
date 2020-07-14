package com.vnest.ca.feature.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.vnest.ca.R;
import com.vnest.ca.activities.MainActivity;
import com.vnest.ca.activities.ViewModel;
import com.vnest.ca.database.sharepreference.VnestSharePreference;
import com.vnest.ca.entity.Message;
import com.vnest.ca.util.DialogActiveControl;
import com.vnest.ca.util.DialogUtils;

import java.util.Calendar;
import java.util.Objects;

public class FragmentHome extends Fragment {
    private static final String LOG_TAG = "VNest";
    private static final String HELLO_ASSISTANT = "Chào bạn, tôi có thể giúp gì cho bạn";

    private Button btnListener;
    private RecyclerView mRecyclerView;
    private AdapterHomeItemDefault adapter;
    private View backIcon;
    private TextView btnBack;
    private TextView assistantText;
    private ViewModel viewModel;
    private DialogActiveControl dialogActiveControl;
    private AlertDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initAction(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dialogActiveControl = new DialogActiveControl(requireContext(), new DialogActiveControl.OnActiveListener() {
            @Override
            public void onAccept() {
                if (progressDialog == null) {
                    progressDialog = DialogUtils.showProgressDialog(requireContext(), false);
                } else {
                    progressDialog.show();
                    dialogActiveControl.dismiss();
                    new Handler().postDelayed(() -> progressDialog.dismiss(), 2000);
                }
            }

            @Override
            public void onSuccess(String activeCode) {
                dialogActiveControl.dismiss();
                VnestSharePreference.getInstance(requireContext()).saveActiveCode(activeCode);
//                getMainActivity().startResultFragment();
//                viewModel.getLiveDataStartRecord().postValue(true);
            }

            @Override
            public void onFail() {

            }
        });
        if (!VnestSharePreference.getInstance(requireContext()).isHadActiveCode()) {
            dialogActiveControl.show();
        }
    }

    private void initView(View view) {
        btnListener = view.findViewById(R.id.btnVoice);
        mRecyclerView = view.findViewById(R.id.recyclerview_def_item);
        btnBack = view.findViewById(R.id.btn_back);
        backIcon = view.findViewById(R.id.icon_back);
        assistantText = view.findViewById(R.id.text_assistant);
    }

    private void initAction(View view) {
        viewModel.getMessage();

        btnListener.setOnClickListener(view1 -> {
            getMainActivity().startResultFragment();
            viewModel.getLiveDataStartRecord().postValue(true);
        });

        viewModel.getListMessLiveData().observe(getViewLifecycleOwner(), list -> {
            if (list.size() > 1) {
                getMainActivity().startResultFragment();
            } else {
                viewModel.saveMessage(new Message(HELLO_ASSISTANT, false, Calendar.getInstance().getTimeInMillis()));
            }
        });
    }


    public MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }
}
