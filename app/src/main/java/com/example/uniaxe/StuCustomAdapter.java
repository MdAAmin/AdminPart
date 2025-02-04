package com.example.uniaxe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StuCustomAdapter extends RecyclerView.Adapter<StuCustomAdapter.ViewHolder> {

    private ArrayList<Model> data;
    private Context context;

    public StuCustomAdapter(ArrayList<Model> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model model = data.get(position);

        // Set the data for each field with labels
        holder.courseId.setText("Cou. ID: " + model.getCourseId());
        holder.courseName.setText("Cou. Name: " + model.getCourseName());
        holder.examType.setText("Exam: " + model.getExamType());
        holder.semester.setText("Semester: " + model.getSemester());
        holder.year.setText("Year: " + model.getYear());
        holder.pdfType.setText("PDF Type: " + model.getPdfType());

        // Handle item click to open the PDF
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(model.getPdfUrl()), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "No application available to view PDF", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseId, courseName, examType, semester, year, pdfType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            courseId = itemView.findViewById(R.id.courseId);
            courseName = itemView.findViewById(R.id.courseName);
            examType = itemView.findViewById(R.id.examType);
            semester = itemView.findViewById(R.id.semester);
            year = itemView.findViewById(R.id.year);
            pdfType = itemView.findViewById(R.id.pdfType);
        }
    }
}
