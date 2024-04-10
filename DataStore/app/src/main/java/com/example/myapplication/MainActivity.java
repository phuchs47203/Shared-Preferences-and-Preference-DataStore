package com.example.myapplication;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlinx.coroutines.runBlocking;

public class MainActivity extends AppCompatActivity {
    private static final Preferences.Key<Set<String>> KEY_NAME =
            PreferencesKeys.stringSetKey("namesList");
    private DataStore<Preferences> dataStore;
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


        dataStore = new RxPreferenceDataStoreBuilder(this, "preferences");
        namesList = new ArrayList<>();
        // Lấy dữ liệu từ DataStore
        loadDataFromDataStore();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textView.getText().toString().trim();
                if (!name.isEmpty()) {
                    // Thêm tên mới vào danh sách
                    namesList.add(name);
                    // Lưu danh sách đã cập nhật vào DataStore
                    saveDataToDataStore();
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
                    // Lưu danh sách đã cập nhật vào DataStore
                    saveDataToDataStore();
                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        displayNamesList();

    }
    private void saveDataToDataStore() {
        // Lưu danh sách vào DataStore
        Set<String> namesSet = new HashSet<>(namesList);
        runBlocking {
            dataStore.edit { preferences ->
                    preferences[KEY_NAME] = namesSet
            }
        }
    }

    private void loadDataFromDataStore() {
        runBlocking {
            val preferences = dataStore.data.first()
            val namesSet = preferences[KEY_NAME] ?: HashSet<String>()
            namesList.addAll(namesSet)
        }
    }

    private void displayNamesList() {
        StringBuilder savedNames = new StringBuilder();
        for (String name : namesList) {
            savedNames.append(name).append("\n");
        }
        displayTextView.setText(savedNames.toString());
    }
}