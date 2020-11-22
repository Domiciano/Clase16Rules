package co.domi.clase10.activities;

import androidx.appcompat.app.AppCompatActivity;
import co.domi.clase10.R;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button recoveryBtn;
    private EditText emailForgotET;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        recoveryBtn = findViewById(R.id.recoveryBtn);
        emailForgotET = findViewById(R.id.emailForgotET);
        auth = FirebaseAuth.getInstance();


        recoveryBtn.setOnClickListener(
                v->{
                    auth.sendPasswordResetEmail(emailForgotET.getText().toString()).addOnCompleteListener(
                            task -> {
                                if(task.isSuccessful()){
                                    Toast.makeText(this, "Email enviado", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }
        );
    }
}