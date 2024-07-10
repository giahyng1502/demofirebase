package fpt.hunghvph46928.demofirebase;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements updateUser {
    RecyclerView recyclerView;
    List<User> list;
    private Dialog loadingDialog;
    UserAdapter userAdapter;
    public static DatabaseReference myData;
    StorageReference storageRef;
    FloatingActionButton btnAdd;
    ImageView ivAvatar;
    Uri imageUri;
    ProgressBar progressBar;
    RelativeLayout progressBarContainer;
    SearchView searchView;

    private final ActivityResultLauncher<Intent> activityResultLauncher
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        if (imageUri != null) {
                            ivAvatar.setImageURI(imageUri);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhXa();
        datafirebase();
        loadData();
        setRView();
        addData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                timkiem(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                timkiem(newText);
                return false;
            }
        });
    }

    private void loadData() {
        myData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void addData() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edtName, edtID, edtOld;
                final Button btnThem, btnHuy;
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_add);
                edtName = dialog.findViewById(R.id.edtName);
                edtID = dialog.findViewById(R.id.edtID);
                edtOld = dialog.findViewById(R.id.edtOld);
                ivAvatar = dialog.findViewById(R.id.dia_Avatar);
                btnThem = dialog.findViewById(R.id.btnThem);
                btnHuy = dialog.findViewById(R.id.btnHuy);

                ivAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openAnbul();
                    }
                });

                btnThem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String id = edtID.getText().toString();
                        final String name = edtName.getText().toString();
                        final String old = edtOld.getText().toString();
                        if (!id.isEmpty() && !name.isEmpty() && !old.isEmpty() && imageUri != null) {
                            showLoading(0);
                            User user = new User(id,name,old,"");
                            themData(user,dialog);
                        } else {
                            Toast.makeText(MainActivity.this, "Vui lòng điền đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnHuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    private void openAnbul() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(intent);
    }

    private void setRView() {
        list = new ArrayList<>();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userAdapter = new UserAdapter(this, list, this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userAdapter);
    }

    private void datafirebase() {
        myData = FirebaseDatabase.getInstance().getReference("User");
        storageRef = FirebaseStorage.getInstance().getReference().child("Image User");
    }

    private void anhXa() {
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
    }
    private void timkiem(String value) {
        List<User> listFilter = new ArrayList<>();
        for (User user : list) {
            if (user.getName().contains(value.toLowerCase())) {
                listFilter.add(user);
            }
        }
            userAdapter.filterList(listFilter);
            userAdapter.notifyDataSetChanged();
    }

    private void showLoading(int type) {
        if (loadingDialog == null) {
            loadingDialog = new Dialog(this);
            loadingDialog.setContentView(R.layout.loading);
            loadingDialog.setCancelable(false);
        }

        if (type == 0) {
            loadingDialog.show();
        } else if (type == 1) {
            loadingDialog.dismiss();
        }
    }


    private void uploadImageAndSaveUser(User user, final Dialog dialog) {
        final StorageReference fileReference = storageRef.child(user.getID() + ".jpg");
        if (imageUri == null) {
            SaveData(user, dialog);
        } else {
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    user.setAvatar(imageUrl);
                                    SaveData(user, dialog);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showLoading(1);
                            Toast.makeText(MainActivity.this, "Thêm ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void themData(User user, final Dialog dialog) {
        final StorageReference fileReference = storageRef.child(user.getID() + ".jpg");
        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                user.setAvatar(imageUrl);
                                SaveData(user, dialog);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showLoading(1);
                        Toast.makeText(MainActivity.this, "Thêm ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void SaveData(User user, Dialog dialog) {
        myData.child(user.getID()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showLoading(1);
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Update Thành Công", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showLoading(1);
                Toast.makeText(MainActivity.this, "Update Thất Bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void updateUser(User user) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add);
        final EditText edtName, edtID, edtOld;
        final Button btnThem, btnHuy;
        edtName = dialog.findViewById(R.id.edtName);
        edtID = dialog.findViewById(R.id.edtID);
        edtOld = dialog.findViewById(R.id.edtOld);
        ivAvatar = dialog.findViewById(R.id.dia_Avatar);
        btnThem = dialog.findViewById(R.id.btnThem);
        btnHuy = dialog.findViewById(R.id.btnHuy);

        edtName.setText(user.getName());
        edtID.setText(user.getID());
        edtOld.setText(user.getOld());
        Glide.with(this).load(user.getAvatar()).into(ivAvatar);

        ivAvatar.setOnClickListener(v2-> openAnbul());

        btnThem.setText("Update");
        btnThem.setOnClickListener(v -> {
            String id = edtID.getText().toString();
            String name = edtName.getText().toString();
            String old = edtOld.getText().toString();
            String avatar = user.getAvatar();
            User user1 = new User(id,name,old,avatar);
            showLoading(0);
            uploadImageAndSaveUser(user1,dialog);
        });
        btnHuy.setOnClickListener(v1->dialog.dismiss());

        dialog.show();

    }
}
