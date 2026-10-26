package com.example.midterm;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;

    // UI Elements
    private TextView tvFormTitle, tvListTitle;
    private EditText etName, etStudentId;
    private CheckBox cbEnrolled;
    private Button btnAdd, btnEdit, btnDelete, btnClear, btnView;
    private ListView lvStudents;


    private List<Student> students = new ArrayList<>();
    private Student selectedStudent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        initViews();
        setupListeners();
        setAddMode();
        refreshList();
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        tvListTitle = findViewById(R.id.tvListTitle);
        etStudentId = findViewById(R.id.etStudentId);
        etName = findViewById(R.id.etName);
        cbEnrolled = findViewById(R.id.cbEnrolled);

        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnClear = findViewById(R.id.btnClear);
        btnView = findViewById(R.id.btnView);

        lvStudents = findViewById(R.id.lvStudents);
    }

    private void setupListeners() {
        btnAdd.setOnClickListener(v -> handleAdd());
        btnEdit.setOnClickListener(v -> handleEdit());
        btnDelete.setOnClickListener(v -> handleDelete());
        btnClear.setOnClickListener(v -> setAddMode());
        btnView.setOnClickListener(v -> handleView());


        lvStudents.setOnItemClickListener((parent, view, position, id) -> {
            Student s = students.get(position);
            setEditMode(s);
        });


        lvStudents.setOnItemLongClickListener((parent, view, position, id) -> {
            Student s = students.get(position);
            showOptionsDialog(s);
            return true;
        });
    }



    private void handleAdd() {
        if (!validateInput()) return;

        String id = etStudentId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        boolean enrolled = cbEnrolled.isChecked();

        if (db.isStudentExists(id)) {
            etStudentId.setError("Student ID is already registered!");
            toast("Failed: Student ID " + id + " already exists");
            return;
        }

        Student student = new Student(id, name, enrolled);
        if (db.addStudent(student)) {
            toast("Student data added successfully");
            setAddMode();
            refreshList();
        } else {
            toast("Failed to add student data");
        }
    }

    private void handleEdit() {
        if (selectedStudent == null) {
            toast("Please select a student from the list to edit");
            return;
        }

        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name cannot be empty");
            return;
        }

        String name = etName.getText().toString().trim();
        boolean enrolled = cbEnrolled.isChecked();

        // Lock Student ID during edit to maintain Primary Key integrity
        Student updatedStudent = new Student(selectedStudent.getStudentId(), name, enrolled);
        if (db.updateStudent(updatedStudent)) {
            toast("Student data (" + selectedStudent.getStudentId() + ") updated successfully");
            setAddMode();
            refreshList();
        } else {
            toast("Failed to update student data");
        }
    }

    private void handleDelete() {
        final String targetId = (!etStudentId.getText().toString().trim().isEmpty())
                ? etStudentId.getText().toString().trim()
                : (selectedStudent != null ? selectedStudent.getStudentId() : "");

        if (targetId.isEmpty()) {
            etStudentId.setError("Enter Student ID or select a student to delete");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete student with ID: " + targetId + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (db.deleteStudent(targetId)) {
                        toast("Student ID " + targetId + " deleted successfully");
                        setAddMode();
                        refreshList();
                    } else {
                        toast("Failed: Student ID not found");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }




    private void handleView() {
        refreshList();
        if (students.isEmpty()) {
            toast("Student list is empty");
        } else {
            toast("Displaying " + students.size() + " student(s)");
        }
    }



    private void setEditMode(Student s) {
        selectedStudent = s;
        etStudentId.setText(s.getStudentId());
        etName.setText(s.getName());
        cbEnrolled.setChecked(s.isEnrolled());


        etStudentId.setEnabled(false);
        btnAdd.setEnabled(false);
        btnEdit.setEnabled(true);
        tvFormTitle.setText(getString(R.string.edit_form_title, s.getStudentId()));

        toast("Edit Mode: " + s.getName());
    }

    private void setAddMode() {
        selectedStudent = null;
        clearFields();

        etStudentId.setEnabled(true);
        btnAdd.setEnabled(true);
        btnEdit.setEnabled(false);
        tvFormTitle.setText(R.string.section_form);
    }

    private void clearFields() {
        etStudentId.getText().clear();
        etName.getText().clear();
        cbEnrolled.setChecked(false);
        etStudentId.setError(null);
        etName.setError(null);
    }

    private void refreshList() {
        students = db.getAllStudents();
        lvStudents.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, students));
        tvListTitle.setText(getString(R.string.list_title_count, students.size()));
    }

    private boolean validateInput() {
        boolean valid = true;
        if (etStudentId.getText().toString().trim().isEmpty()) {
            etStudentId.setError("Student ID cannot be empty");
            valid = false;
        }
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name cannot be empty");
            valid = false;
        }
        return valid;
    }

    private void showOptionsDialog(Student s) {
        String[] options = {"Edit", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(s.getStudentId() + " - " + s.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        setEditMode(s);
                    } else if (which == 1) {
                        setEditMode(s);
                        handleDelete();
                    }
                })
                .show();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}