package com.example.uniaxe;

import android.content.ActivityNotFoundException;
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

public class SyllabusReadCustomAdapter extends RecyclerView.Adapter<SyllabusReadCustomAdapter.ViewHolder> {

    private ArrayList<ModelTeacher> data;
    private Context context;

    public SyllabusReadCustomAdapter(ArrayList<ModelTeacher> data, Context context) {
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
