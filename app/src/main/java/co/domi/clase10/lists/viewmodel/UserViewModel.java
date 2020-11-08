package co.domi.clase10.lists.viewmodel;

import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.domi.clase10.R;

public class UserViewModel extends RecyclerView.ViewHolder {

    private Button actionRow;
    private ImageView imageRow;
    private TextView nameRow;

    public UserViewModel(@NonNull View itemView) {
        super(itemView);
        imageRow = itemView.findViewById(R.id.imgRow);
        actionRow = itemView.findViewById(R.id.actionRow);
        nameRow = itemView.findViewById(R.id.nameRow);
    }

    public Button getActionRow() {
        return actionRow;
    }

    public ImageView getImageRow() {
        return imageRow;
    }

    public TextView getNameRow() {
        return nameRow;
    }
}
