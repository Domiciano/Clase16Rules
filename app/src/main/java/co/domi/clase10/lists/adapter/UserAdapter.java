package co.domi.clase10.lists.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.domi.clase10.R;
import co.domi.clase10.lists.viewmodel.UserViewModel;
import co.domi.clase10.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserViewModel> {


    private ArrayList<User> users;
    private OnUserClickListener listener;

    public UserAdapter(){
        users = new ArrayList<>();
    }

    public void addUser(User user){
        users.add(user);
        notifyDataSetChanged();
    }

    public void clear(){
        users.clear();
        notifyDataSetChanged();
    }

    @Override
    public UserViewModel onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.userrow, parent, false);
        UserViewModel userViewModel = new UserViewModel(view);
        return userViewModel;
    }

    @Override
    public void onBindViewHolder(UserViewModel holder, int position) {
        User user = users.get(position);

        holder.getNameRow().setText(user.getUsername());
        holder.getActionRow().setOnClickListener( v->listener.onUserClick(user) );

        if(user.getPhotoId() != null){
            FirebaseStorage.getInstance().getReference().child("profiles").child(user.getPhotoId()).getDownloadUrl()
                    .addOnCompleteListener(
                            task -> {
                                String url = task.getResult().toString();
                                Glide.with(holder.getImageRow()).load(url).into(holder.getImageRow());
                            }
                    );
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setListener(OnUserClickListener listener){
        this.listener = listener;
    }

    public interface OnUserClickListener{
        void onUserClick(User user);
    }

}
