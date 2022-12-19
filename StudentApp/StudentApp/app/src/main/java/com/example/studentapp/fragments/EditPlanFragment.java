package com.example.studentapp.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentapp.R;
import com.example.studentapp.adapters.SubjectAddRecycler;
import com.example.studentapp.databinding.FragmentEditPlanBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.Questions;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditPlanFragment extends Fragment {

    FragmentEditPlanBinding binding;
    ApiInterface apiInterface;
    EditPlanFragmentArgs args;
    SubjectAddRecycler.OnItemClickListener itemClick;
    final Calendar myCalendar = Calendar.getInstance();
    Subjects subject;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemClick = new SubjectAddRecycler.OnItemClickListener() {
            @Override
            public void onClickQuestion(Questions ques, int position) {
                showItemDialog(view, ques);
            }
        };

        setQuestions();

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };

        binding.editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(),date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        binding.b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(view);
            }
        });

        binding.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Subjects> deleteSubj = apiInterface.deleteSubject(args.getId());
                deleteSubj.enqueue(new Callback<Subjects>() {
                    @Override
                    public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                        if (response.isSuccessful()){
                            NavDirections action = EditPlanFragmentDirections.actionEditPlanFragmentToListFragment();
                            Navigation.findNavController(getView()).navigate(action);
                        }
                    }

                    @Override
                    public void onFailure(Call<Subjects> call, Throwable t) {

                    }
                });
            }
        });

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subject.setDaysString(binding.editTextDate.getText().toString());
                subject.setName(binding.Text1.getText().toString());
                Call<Subjects> subjectsCall = apiInterface.updateSubject(subject.getId(), subject);
                subjectsCall.enqueue(new Callback<Subjects>() {
                    @Override
                    public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                        if (response.isSuccessful()){
                            NavDirections action = EditPlanFragmentDirections.actionEditPlanFragmentToListFragment();
                            Navigation.findNavController(getView()).navigate(action);
                        }else {
                            Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Subjects> call, Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }



    private void updateLabel(){
        String myFormat="yyyy-MM-dd";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
        binding.editTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void setQuestions(){
        Call<Subjects> subjectsCall = apiInterface.getSubjectById(args.getId());
        subjectsCall.enqueue(new Callback<Subjects>() {
            @Override
            public void onResponse(Call<Subjects> call, Response<Subjects> response) {
                if (response.body()!=null){
                    subject = response.body();
                    binding.Text1.setText(response.body().getName());
                    binding.editTextDate.setText(response.body().getDaysString().split("T")[0]);
                    binding.listVop.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.listVop.setHasFixedSize(true);
                    binding.listVop.setAdapter(new SubjectAddRecycler(getContext(), response.body().getQuestions(), itemClick));
                }
            }

            @Override
            public void onFailure(Call<Subjects> call, Throwable t) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_plan, container, false);
        Paper.init(getContext());
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        args = EditPlanFragmentArgs.fromBundle(getArguments());
        return binding.getRoot();
    }

    private void showItemDialog(View view, Questions questions) {
        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dialog_add_question,
                        null);
        builder.setView(customLayout);



        AlertDialog dialog
                = builder.create();

        EditText tvAnswer = customLayout.findViewById(R.id.tv_answer);
        EditText tvQ = customLayout.findViewById(R.id.tv_question);
        Button addBtn = customLayout.findViewById(R.id.add_question);
        AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);

        tvAnswer.setText(questions.getAnswer());
        tvQ.setText(questions.getQuestion());

        addBtn.setText("Сохранить");
        clsBtn.setText("Удалить");

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAnswer.getText().toString() == "" || tvQ.getText().toString() == ""){
                    Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }else {
                    questions.setQuestion(tvQ.getText().toString());
                    questions.setAnswer(tvAnswer.getText().toString());
                    Call<Questions> updateQuestion = apiInterface.updateQuestion(questions.getId(), questions);
                    updateQuestion.enqueue(new Callback<Questions>() {
                        @Override
                        public void onResponse(Call<Questions> call, Response<Questions> response) {
                            if (response.body()!=null){
                                setQuestions();
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Questions> call, Throwable t) {

                        }
                    });
                }
            }
        });

        clsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Questions> deleteQuestion = apiInterface.deleteQuestion(questions.getId());
                deleteQuestion.enqueue(new Callback<Questions>() {
                    @Override
                    public void onResponse(Call<Questions> call, Response<Questions> response) {
                        if (response.body()!= null){
                            setQuestions();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<Questions> call, Throwable t) {

                    }
                });
            }
        });

        dialog.show();
    }

    public void showAlertDialogButtonClicked(View view)
    {

        // Create an alert builder
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());

        // set the custom layout
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.dialog_add_question,
                        null);
        builder.setView(customLayout);



        AlertDialog dialog
                = builder.create();

        EditText tvAnswer = customLayout.findViewById(R.id.tv_answer);
        EditText tvQ = customLayout.findViewById(R.id.tv_question);
        Button addBtn = customLayout.findViewById(R.id.add_question);
        AppCompatButton clsBtn = customLayout.findViewById(R.id.cancel_window);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvAnswer.getText().toString() == "" || tvQ.getText().toString() == ""){
                    Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }else {
                    Questions newQuestion = new Questions(0,tvQ.getText().toString(),tvAnswer.getText().toString(),false,subject);//Тут было args.getId()
                    newQuestion.setId(null);
                    Call<Questions> questionsCall = apiInterface.addQuestion(newQuestion);
                    questionsCall.enqueue(new Callback<Questions>() {
                        @Override
                        public void onResponse(Call<Questions> call, Response<Questions> response) {
                            if (response.body()!= null){
                                setQuestions();
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onFailure(Call<Questions> call, Throwable t) {

                        }
                    });
                }
            }
        });

        clsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}