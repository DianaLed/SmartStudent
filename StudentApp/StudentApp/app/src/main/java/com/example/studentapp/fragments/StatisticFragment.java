package com.example.studentapp.fragments;

import static java.time.temporal.ChronoUnit.DAYS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studentapp.MainActivity;
import com.example.studentapp.R;
import com.example.studentapp.al.PlanToSub;
import com.example.studentapp.databinding.FragmentStatisticBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;

import java.time.LocalDate;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StatisticFragment extends Fragment {

    FragmentStatisticBinding binding;
    ApiInterface apiInterface;
    StatisticFragmentArgs args;
    PlanToSub planToSub;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatisticFragmentDirections.ActionStatisticFragmentToEditPlanFragment action = StatisticFragmentDirections.actionStatisticFragmentToEditPlanFragment(args.getId());
                Navigation.findNavController(getView()).navigate(action);
            }
        });
        planToSub =  MainActivity.myDBManager.set().stream()
                .filter( c -> c.getId() == args.getId()).collect(Collectors.toList()).get(0);
        binding.kolvop.setText("Количество вопросов: "+planToSub.getSub().getSizeAllQuest());
        binding.kolzap.setText("Колличество запомненых вопросов: "+planToSub.getSub().getSizeKnow());
        binding.dateob.setText("Количество невыученных вопросов:" +planToSub.getSub().getSizeNoKnow());
        binding.dateex.setText("Дата экзамена: "+ planToSub.dateToString().split("T")[0]);
        LocalDate date = LocalDate.parse(planToSub.dateToString().split("T")[0]);
        long days = DAYS.between(LocalDate.now(), date);
        binding.kold.setText("Количество дней до экзамена: "+days);


      /*  Call<Subjects> getSubj = apiInterface.getSubjectById(args.getId());
        getSubj.enqueue(new Callback<Subjects>() {
            @Override
            public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                if (response.body()!= null){
                    Subjects subj = response.body();
                    binding.kolvop.setText("Количество вопросов: "+subj.getQuestions().size());
                    int completed=0;
                    for(int i =0; i < subj.getQuestions().size(); i++){
                        //if (subj.getQuestions().get(i).isCompleted()) completed++;
                    }
                    binding.kolzap.setText("Колличество запомненых вопросов: "+completed);
                    int uncompleted = 0;
                    for(int i =0; i < subj.getQuestions().size(); i++){
                       // if (!subj.getQuestions().get(i).isCompleted()) uncompleted++;
                    }
                    binding.dateob.setText("Количество невыученных вопросов:" +uncompleted);
                    binding.dateex.setText(subj.getDaysString().split("T")[0]);
                    LocalDate date = LocalDate.parse(subj.getDaysString().split("T")[0]);
                    long days = DAYS.between(LocalDate.now(), date);
                    binding.kold.setText("Количество дней до экзамена: "+days);
                }else{
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Subjects> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_statistic, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = StatisticFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }
}