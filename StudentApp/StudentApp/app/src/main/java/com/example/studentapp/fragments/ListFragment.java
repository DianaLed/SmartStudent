package com.example.studentapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.R;
import com.example.studentapp.adapters.SubjectAdapter;
import com.example.studentapp.databinding.FragmentListBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListFragment extends Fragment {

    FragmentListBinding binding;
    ApiInterface apiInterface;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SubjectAdapter.OnItemClickListener itemClickListener =new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onClickSubject(Subjects subject, int position) {
                ListFragmentDirections.ActionListFragmentToStatisticFragment action = ListFragmentDirections.actionListFragmentToStatisticFragment(subject.getId());

                Navigation.findNavController(getView()).navigate(action);
            }
        };

        Call<ArrayList<Subjects>> getSubjs = apiInterface.getSubjectsByUser(Users.getUser().getId());
        getSubjs.enqueue(new Callback<ArrayList<Subjects>>() {
            @Override
            public void onResponse(Call<ArrayList<Subjects>> call, Response<ArrayList<Subjects>> response) {
                if (response.isSuccessful()){
                    binding.listSubView.setHasFixedSize(true);
                    binding.listSubView.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listSubView.setAdapter(new SubjectAdapter(getContext(), response.body(), itemClickListener));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Subjects>> call, Throwable t) {

            }
        });

        binding.AddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = ListFragmentDirections.actionListFragmentToAddPlanFragment();
                Navigation.findNavController(getView()).navigate(action);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_list, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        return binding.getRoot();
    }
}