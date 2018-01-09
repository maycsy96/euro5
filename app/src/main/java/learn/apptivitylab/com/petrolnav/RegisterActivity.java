package learn.apptivitylab.com.petrolnav;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by apptivitylab on 08/01/2018.
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private AppCompatImageView rImageLogo;
    private AppCompatEditText rNameEditText;
    private AppCompatEditText rEmailEditText;
    private AppCompatEditText rPasswordEditText;
    private TextView rLoginLink;
    private AppCompatButton rRegisterButton;
    private ViewGroup rContentViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rImageLogo = (AppCompatImageView) findViewById(R.id.activity_register_logo_petrolnav);
        rNameEditText = (AppCompatEditText) findViewById(R.id.activity_register_et_name);
        rEmailEditText = (AppCompatEditText) findViewById(R.id.activity_register_et_email);
        rPasswordEditText = (AppCompatEditText) findViewById(R.id.activity_register_et_password);
        rLoginLink = (TextView) findViewById(R.id.activity_register_textview_login);
        rRegisterButton = (AppCompatButton) findViewById(R.id.activity_register_btn_register);
        rContentViewGroup = (ViewGroup) findViewById(R.id.activity_register_vg_container);

        rRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        rLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish this registration screen and return to login activity
                finish();
            }
        });

    }

    public void register(){
        Log.d(TAG,"Register");
        if(!validate()){
            onRegisterFailed();
            return;
        }
        rRegisterButton.setEnabled(false);
        String name = rNameEditText.getText().toString();
        String email = rEmailEditText.getText().toString();
        String password = rPasswordEditText.getText().toString();

        //registration logic

        onRegisterSuccess();
    }

    public void onRegisterSuccess(){
        rRegisterButton.setEnabled(true);
        /*
        setResult(RESULT_OK);
        asd//should be finish this activity and return to login screen
        */
        finish();
    }
    public void onRegisterFailed(){
        Toast.makeText(getBaseContext(), "Register failed", Toast.LENGTH_LONG).show();

        rRegisterButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = rNameEditText.getText().toString();
        String email = rEmailEditText.getText().toString();
        String password = rPasswordEditText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            rNameEditText.setError("at least 3 characters");
            valid = false;
        } else {
            rNameEditText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            rEmailEditText.setError("enter a valid email address");
            valid = false;
        } else {
            rEmailEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            rPasswordEditText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            rPasswordEditText.setError(null);
        }

        return valid;
    }
}
