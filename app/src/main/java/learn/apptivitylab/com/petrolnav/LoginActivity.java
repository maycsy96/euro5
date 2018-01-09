package learn.apptivitylab.com.petrolnav;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_LOGIN = 0;

    private AppCompatImageView lImageLogo;
    private TextInputEditText lEmailEditText;
    private TextInputEditText lPasswordEditText;
    private TextView lRegisterLink;
    private AppCompatButton lLoginButton;
    private ViewGroup lContentViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lImageLogo = (AppCompatImageView) findViewById(R.id.activity_login_logo_petrolnav);
        lEmailEditText = (TextInputEditText) findViewById(R.id.activity_login_et_email);
        lPasswordEditText = (TextInputEditText) findViewById(R.id.activity_login_et_password);
        lRegisterLink = (TextView) findViewById(R.id.activity_login_textview_register);
        lLoginButton = (AppCompatButton) findViewById(R.id.activity_login_btn_login);
        lContentViewGroup = (ViewGroup) findViewById(R.id.activity_login_vg_container);

        lLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        lRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start the register Activity
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

    }

    public void login() {
        Log.d(TAG, "Login");
        //validation of login information (making sure username and password are filled)
        if(!validate()){
            //fail validation
            onLoginFailed();
            return;
        }

        //validation successful
        lLoginButton.setEnabled(false);

        //show loading
        String email = lEmailEditText.getText().toString();
        String password = lPasswordEditText.getText().toString();

        //Authentication logic

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        //On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();

                    }
                },3000
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SIGNUP && resultCode == RESULT_OK){
            //implement signup logic here
            this.finish();
        }
    }

    public void onBackPressed(){
        //disable going back to the Main Activity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(){
        lLoginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    public void onLoginFailed(){
        Toast.makeText(getBaseContext(), "Login Failed", Toast.LENGTH_LONG).show();
        lLoginButton.setEnabled(true);
    }

    public boolean validate(){
        boolean valid = true;
        String email = lEmailEditText.getText().toString();
        String password = lPasswordEditText.getText().toString();

        if(email.isEmpty()){
            lEmailEditText.setError("Please enter username");
            valid = false;
        }else{
            lEmailEditText.setError(null);
        }

        if(password.isEmpty() || password.length()<4 || password.length() >10){
            lPasswordEditText.setError("Please enter password");
            valid = false;
        }else{
            lPasswordEditText.setError(null);
        }
        return valid;
    }
}
