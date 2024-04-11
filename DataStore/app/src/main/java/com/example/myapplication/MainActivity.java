package com.example.myapplication;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final Preferences.Key<Set<String>> KEY_NAME =
            PreferencesKeys.stringSetKey("namesList");
    private RxDataStore<Preferences> dataStore;
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


        dataStore = new RxPreferenceDataStoreBuilder(getApplicationContext(), "preferences").build();
        namesList = new ArrayList<>();

        // Load dữ liệu từ DataStore
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
        Set<String> namesSet = new HashSet<>(namesList);
        dataStore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            // Xóa dữ liệu hiện tại của khóa KEY_NAME
            mutablePreferences.remove(KEY_NAME);
            // Thêm dữ liệu mới cho khóa KEY_NAME
            mutablePreferences.set(KEY_NAME, namesSet);
            return Single.just(mutablePreferences);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private void loadDataFromDataStore() {
        dataStore.data()
                .map(preferences -> preferences.get(KEY_NAME))
                .subscribeOn(Schedulers.io())
                .subscribe(namesSet -> {
                    if (namesSet != null) {
                        namesList.addAll(namesSet);
                        // Hiển thị danh sách tên sau khi dữ liệu đã được tải hoàn toàn
                        displayNamesList();
                    } else {
                        // Xử lý trường hợp khi namesSet là null
                        // Ví dụ: hiển thị thông báo hoặc thực hiện hành động khác
                        displayTextView.setText("Data from DataStore is null");
                    }
                }, throwable -> {
                    // Xử lý lỗi trong quá trình xử lý dữ liệu
                    displayTextView.setText("Error loading data from DataStore: " + throwable.getMessage());
                });
    }



    private void displayNamesList() {
        // Kiểm tra kích thước của namesList và in ra log
        Log.d("NamesListSize", "Size of namesList: " + namesList.size());
        // Duyệt qua danh sách tên và in ra log từng tên
        for (String name : namesList) {
            Log.d("Name", "Name: " + name);
        }

        // Tạo một StringBuilder để tạo ra chuỗi hiển thị
        StringBuilder savedNames = new StringBuilder();
        // Duyệt qua danh sách tên và thêm từng tên vào StringBuilder
        for (String name : namesList) {
            savedNames.append(name).append("\n");
        }
        // Đặt văn bản đã tạo vào displayTextView
        displayTextView.setText(savedNames.toString());
    }

}