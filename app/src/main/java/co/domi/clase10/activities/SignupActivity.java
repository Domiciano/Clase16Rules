package co.domi.clase10.activities;

import androidx.appcompat.app.AppCompatActivity;
import co.domi.clase10.R;
import co.domi.clase10.model.User;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameET, cityET, emailET, passwordET, repasswordET;
    private Button signupBtn;
    private TextView loginLink;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameET = findViewById(R.id.nameET);
        cityET = findViewById(R.id.cityET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        repasswordET = findViewById(R.id.repasswordET);
        signupBtn = findViewById(R.id.signupBtn);
        loginLink = findViewById(R.id.loginLink);

        loginLink.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginLink:
                finish();
                break;
            case R.id.signupBtn:
                auth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString()).addOnCompleteListener(
                        task -> {
                            if(task.isSuccessful()){
                                FirebaseUser fbuser = auth.getCurrentUser();
                                User user = new User(
                                        fbuser.getUid(),
                                        nameET.getText().toString(),
                                        cityET.getText().toString(),
                                        emailET.getText().toString()
                                );
                                db.collection("users").document(user.getId()).set(user).addOnCompleteListener(
                                        dbtask -> {
                                            if(dbtask.isSuccessful()) sendVerification();
                                        }
                                );


                            }else{
                                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                );
                break;
        }
    }

    private void sendVerification() {
        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Email de verificación enviado, revise su bandeja de entrada", Toast.LENGTH_LONG).show();
                        auth.signOut();
                    }else{
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}