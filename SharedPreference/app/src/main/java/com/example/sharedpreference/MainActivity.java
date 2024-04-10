package com.example.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String PREF_NAME = "MyPreferences";
    private static final String KEY_NAME = "namesList";

    private SharedPreferences sharedPreferences;
    private TextView displayTextView;
    private TextView textView;
    private List<String> namesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView = findViewById(R.id.textView);
        displayTextView = findViewById(R.id.displayTextView);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnUppercase = findViewById(R.id.btnUppercase);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        namesList = new ArrayList<>(sharedPreferences.getStringSet(KEY_NAME, new HashSet<>()));


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textView.getText().toString().trim();
                if (!name.isEmpty()) {
                    // Thêm tên mới vào danh sách
                    namesList.add(name);
                    // Lưu danh sách đã cập nhật trở lại SharedPreferences
                    saveNamesToSharedPreferences();
                }
            }
        });

        btnUppercase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textView.getText().toString().trim();
                if (!name.isEmpty()) {
                    // Chuyển tên thành chữ hoa và thêm vào danh sách
                    String uppercaseName = name.toUpperCase();
                    namesList.add(uppercaseName);
                    // Lưu danh sách đã cập nhật trở lại SharedPreferences
                    saveNamesToSharedPreferences();
                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        displayNamesList();

    }
    private void saveNamesToSharedPreferences() {
        Set<String> namesSet = new HashSet<>(namesList);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_NAME, namesSet);
        editor.apply();
    }
    private void displayNamesList() {
        StringBuilder savedNames = new StringBuilder();
        for (String name : namesList) {
            savedNames.append(name).append("\n");
        }
        displayTextView.setText(savedNames.toString());
    }

}