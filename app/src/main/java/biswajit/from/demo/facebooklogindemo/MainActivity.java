package biswajit.from.demo.facebooklogindemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken _accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(_accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            if (null != response) {
                                JSONObject jsonObject = new JSONObject(response.getRawResponse());
                                String facebookFullName = jsonObject.has("name") ? jsonObject.getString("name") : "";     // get Facebook Name
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    LoginManager.getInstance().logOut();
                                }
                                displayMessage(response.getRawResponse());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                            displayMessage(e.getLocalizedMessage());
                        }
                    }
                });
                Bundle parameters = new Bundle();
                /**
                 *
                 * to get details about user access list
                 * ref URL https://developers.facebook.com/docs/graph-api/reference/user/
                 *
                 */
                parameters.putString("fields", "id,name,birthday,friendlists,hometown,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
                displayMessage(getContext().getString(R.string.facebook_authenticaiton_request_cancelled));
            }

            @Override
            public void onError(FacebookException error) {
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
                displayMessage(error.getLocalizedMessage());
            }
        });

        findViewById(R.id.facebook_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions((Activity) getContext(), Arrays.asList("public_profile", "email", "user_friends", "user_photos", "user_birthday", "user_hometown"));
            }
        });

    }

    private void displayMessage(String __message) {
        ((TextView) findViewById(R.id.response_from_facebook)).setText(__message);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private Context getContext() {
        return this;
    }
}
