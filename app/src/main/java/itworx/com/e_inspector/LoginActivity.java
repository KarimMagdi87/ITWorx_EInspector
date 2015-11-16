package itworx.com.e_inspector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.editUserName);
        etPassword = (EditText) findViewById(R.id.editPassword);
        sessionManager = new SessionManager(this);
    }

    public void doLogin(View v) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty())
            Toast.makeText(this, "Please enter your email / password", Toast.LENGTH_SHORT).show();
        else {
            HashMap<String, String> user = sessionManager.getUserDetails();
            String un = user.get(SessionManager.KEY_USERNAME);
            String ps = user.get(SessionManager.KEY_PASSWORD);

            if (un == null || ps == null) {
                Toast.makeText(this, "your email is not registered yet please do register", Toast.LENGTH_SHORT).show();
                return;
            }
            if (un != null && ps != null) {
                if (username.equals(un) && password.equals(ps)) {
                    startActivity(new Intent(this, CasesListActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    public void doRegister(View v) {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

}
