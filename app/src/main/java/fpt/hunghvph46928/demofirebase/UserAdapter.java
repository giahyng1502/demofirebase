package fpt.hunghvph46928.demofirebase;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import fpt.hunghvph46928.demofirebase.R;
import fpt.hunghvph46928.demofirebase.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context context;
    List<User> list;
    updateUser updateUser;

    public UserAdapter(Context context, List<User> list,updateUser mupdateUser) {
        this.context = context;
        this.list = list;
        this.updateUser = mupdateUser;
    }
    public void filterList(List<User> listfilter) {
        this.list = listfilter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = list.get(position);

        Glide.with(context).load(user.getAvatar()).into(holder.ivAvatar);
        holder.tvID.setText("ID : "+user.getID());
        holder.tvName.setText("Name : "+user.getName());
        holder.tvOld.setText("Age : "+user.getOld());
        holder.ivDel.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Xóa");
            builder.setMessage("Bạn có muốn xóa hay không");
            builder.setNegativeButton("Không",(dialog, which) -> dialog.dismiss());
            builder.setPositiveButton("Có",(dialog, which) -> {
                MainActivity.myData.child(user.getID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            builder.show();
        });
        holder.ivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser.updateUser(user);
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvID,tvName,tvOld;
        ImageView ivAvatar,ivDel,ivUpdate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tvID);
            tvName = itemView.findViewById(R.id.tvName);
            tvOld = itemView.findViewById(R.id.tvOld);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivUpdate = itemView.findViewById(R.id.ivUpdate);
            ivDel = itemView.findViewById(R.id.ivDelete);
        }
    }
}