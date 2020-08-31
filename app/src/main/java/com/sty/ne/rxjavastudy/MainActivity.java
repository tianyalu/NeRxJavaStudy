package com.sty.ne.rxjavastudy;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnGotoBasicConceptActivity;
    private Button btnGotoCreationOperatorActivity;
    private Button btnGotoTransformationOperatorActivity;
    private Button btnGotoFilterOperatorActivity;
    private Button btnGotoConditionOperatorActivity;
    private Button btnGotoMergeOperatorActivity;
    private Button btnGotoExceptionOperatorActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        addListeners();
    }

    private void initView() {
        btnGotoBasicConceptActivity = findViewById(R.id.btn_goto_basic_concept_activity);
        btnGotoCreationOperatorActivity = findViewById(R.id.btn_goto_creation_operator_activity);
        btnGotoTransformationOperatorActivity = findViewById(R.id.btn_goto_transformation_operator_activity);
        btnGotoFilterOperatorActivity = findViewById(R.id.btn_goto_filter_operator_activity);
        btnGotoConditionOperatorActivity = findViewById(R.id.btn_goto_condition_operator_activity);
        btnGotoMergeOperatorActivity = findViewById(R.id.btn_goto_merge_operator_activity);
        btnGotoExceptionOperatorActivity = findViewById(R.id.btn_goto_exception_operator_activity);

    }

    private void addListeners() {
        btnGotoBasicConceptActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BasicConceptActivity.class));
            }
        });
        btnGotoCreationOperatorActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreationOperatorActivity.class));
            }
        });

        btnGotoTransformationOperatorActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TransformationOperatorActivity.class));
            }
        });
        btnGotoFilterOperatorActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FilterOperatorActivity.class));
            }
        });
        btnGotoConditionOperatorActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConditionOperatorActivity.class));
            }
        });
        btnGotoMergeOperatorActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MergeOperatorActivity.class));
            }
        });
        btnGotoExceptionOperatorActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ExceptionOperatorActivity.class));
            }
        });
    }



}
