package com.example.uniaxe;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class TeacherCustomAdapter extends RecyclerView.Adapter<TeacherCustomAdapter.ViewHolder> {

    private ArrayList<ModelTeacher> data;
    private Context context;

    public TeacherCustomAdapter(ArrayList<ModelTeacher> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_view_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelTeacher modelTeacher = data.get(position);

        holder.techName.setText(modelTeacher.getTeacherName()); // Set the teacher name
        holder.couName.setText(modelTeacher.getCouName());
        holder.couId.setText(modelTeacher.getCouId());
        holder.batch.setText(modelTeacher.getBatch());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(modelTeacher.getSyllabusUrl()), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "No application available to view PDF", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.popup_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.update) {
                    Intent intent = new Intent(context, UpdateGuideActivity.class);
                    intent.putExtra("key", modelTeacher.getKey());
                    intent.putExtra("couName", modelTeacher.getCouName());
                    intent.putExtra("couId", modelTeacher.getCouId());
                    intent.putExtra("syllabusUrl", modelTeacher.getSyllabusUrl()); // Corrected field name
                    context.startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.delete) {
                    FirebaseDatabase.getInstance().getReference("Syllabus")
                            .child(modelTeacher.getKey())
                            .removeValue()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(context, "Deleted successfully!", Toast.LENGTH_SHORT).show())
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
        TextView techName, couName, couId, batch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            techName = itemView.findViewById(R.id.TechName); // Set the correct ID
            couName = itemView.findViewById(R.id.couName);
            couId = itemView.findViewById(R.id.couId);
            batch = itemView.findViewById(R.id.batchInfo);
        }
    }
}
