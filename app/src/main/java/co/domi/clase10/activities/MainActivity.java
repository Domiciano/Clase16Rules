package co.domi.clase10.activities;

import androidx.appcompat.app.AppCompatActivity;
import co.domi.clase10.R;
import co.domi.clase10.comm.Actions;
import co.domi.clase10.events.OnRegisterListener;
import co.domi.clase10.model.User;
import co.domi.clase10.util.NotificationUtil;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBtn;
    private EditText usernameET;
    private FirebaseFirestore db;
    private EditText passwordET;
    private TextView singupLink, forgotPasswordLink;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.loginBtn);
        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        singupLink = findViewById(R.id.signupLink);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        loginBtn.setOnClickListener(this);
        singupLink.setOnClickListener(this);
        forgotPasswordLink.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                auth.signInWithEmailAndPassword(usernameET.getText().toString(), passwordET.getText().toString())
                    .addOnCompleteListener(
                            task -> {
                                if(task.isSuccessful()){
                                    FirebaseUser fbuser = auth.getCurrentUser();

                                    if(fbuser.isEmailVerified()){
                                        goToUserListActivity();
                                    }else{
                                        Toast.makeText(this, "Debe verificar su cuenta antes de ingresar", Toast.LENGTH_LONG).show();
                                        auth.signOut();
                                    }

                                }else{
                                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                    );

                break;

            case R.id.signupLink:
                Intent intent = new Intent(this, SignupActivity.class);
                startActivity(intent);
                break;

            case R.id.forgotPasswordLink:
                Intent intentFogotPass = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intentFogotPass);
                break;
        }
    }


    public void goToUserListActivity() {
        Intent i = new Intent(this, UserListActivity.class);
        startActivity(i);
        finish();
    }
}