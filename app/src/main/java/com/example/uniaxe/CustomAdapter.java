package com.example.uniaxe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<Model> data;
    private Context context;

    public CustomAdapter(ArrayList<Model> data, Context context) {
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

        // Set the data for the fields in each item
        holder.courseId.setText(model.getCourseId());
        holder.courseName.setText(model.getCourseName());
        holder.examType.setText(model.getExamType());
        holder.semester.setText(model.getSemester());
        holder.year.setText(model.getYear());
        holder.pdfType.setText(model.getPdfType());

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

        // Handle item long click to open the pop-up menu
        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.popup_menu); // Define popup_menu.xml in res/menu folder

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.update) {
                    Intent intent = new Intent(context, UpdateActivity.class);
                    intent.putExtra("key", model.getKey());
                    intent.putExtra("courseId", model.getCourseId());
                    intent.putExtra("courseName", model.getCourseName());
                    intent.putExtra("examType", model.getExamType());
                    intent.putExtra("semester", model.getSemester());
                    intent.putExtra("year", model.getYear());
                    intent.putExtra("pdfType", model.getPdfType());
                    intent.putExtra("pdfUrl", model.getPdfUrl());
                    context.startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.delete) {
                    FirebaseDatabase.getInstance().getReference("PDFs")
                            .child(model.getKey())
                            .removeValue()
                            .addOnSuccessListener(unused -> {
                                // Display the success toast
                                Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show();

                                // Navigate to AdminDashBoard activity
                                Intent intent = new Intent(context, AdminDashBoard.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // To start a new activity
                                context.startActivity(intent);
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    return true;
                }
                return false;

            });

            popupMenu.show();
            return true;
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
