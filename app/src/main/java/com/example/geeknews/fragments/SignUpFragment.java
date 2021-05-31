package com.example.geeknews.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.geeknews.R;
import com.example.geeknews.models.User;
import com.example.geeknews.retrofit.ApiInterface;
import com.example.geeknews.retrofit.RetrofitFactory;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class SignUpFragment extends Fragment {

        private View view ;
        private Button signUpBtn ;
        private TextInputEditText userNameEditText ;
        private TextInputEditText passEditText ;
    private TextInputEditText passConfEditText ;
    private TextInputEditText firstNameEditText ;
        private TextInputEditText lastNameEditText ;
        private TextInputEditText emailEditText ;
        private String username;
        private String password ;
        private String firstName ;
        private String lastName ;
        private String email ;
        private String passwordConf ;
        private ApiInterface apiInterface;
        private ProgressBar progressBar ;
        private SharedPreferences sharedPreferences;
        private SharedPreferences.Editor editor;
        private JSONArray usernameError;
        private JSONArray emailError;
        private JSONArray passwordError ;
        private String passwordConfError ;
        JSONObject jObjError;
        String userError ;
        String passsError ;
        String emaillError ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        signUpBtn = view.findViewById(R.id.signUp_btn);
        userNameEditText = view.findViewById(R.id.userNameEd);
        passEditText = view.findViewById(R.id.passEd);
        passConfEditText = view.findViewById(R.id.passConfEd);
        firstNameEditText = view.findViewById(R.id.firstNameEd);
        lastNameEditText = view.findViewById(R.id.lastNameEd);
        emailEditText = view.findViewById(R.id.emailEd);
        progressBar=view.findViewById(R.id.progressBar);
        // Inflate the layout for this fragment
        return view ;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onBackPressed();
        clickSignUpBtn();
    }

    private void getDataFromEd(){
        if (!userNameEditText.getText().toString().trim().equalsIgnoreCase("")) {

            username = userNameEditText.getText().toString();
        }
        if (!passEditText.getText().toString().trim().equalsIgnoreCase("")) {

            password = passEditText.getText().toString();
        }
        if (!passConfEditText.getText().toString().trim().equalsIgnoreCase("")) {

            passwordConf = passConfEditText.getText().toString();
        }
        if (!firstNameEditText.getText().toString().trim().equalsIgnoreCase("")) {

            firstName = firstNameEditText.getText().toString();
        }
        if (!lastNameEditText.getText().toString().trim().equalsIgnoreCase("")) {

            lastName = lastNameEditText.getText().toString();
        }
        if (!emailEditText.getText().toString().trim().equalsIgnoreCase("")) {

            email = emailEditText.getText().toString();
        }

    }
    private boolean isValid() {
        boolean valid;
        if (!userNameEditText.getText().toString().trim().equalsIgnoreCase("")) {
            valid = true;
        }
        if (!passEditText.getText().toString().trim().equalsIgnoreCase("")) {
            valid = true;
        }
        if (!passConfEditText.getText().toString().trim().equalsIgnoreCase("")) {
            valid = true;
        }
        if (!firstNameEditText.getText().toString().trim().equalsIgnoreCase("")) {
            valid = true;
        }
        if (!lastNameEditText.getText().toString().trim().equalsIgnoreCase("")) {
            valid = true;
        }
        if (!emailEditText.getText().toString().trim().equalsIgnoreCase("")) {
            valid = true;
        }
        else valid = false;
        return valid;

    }
    private void postRegister() {
        getDataFromEd();
        apiInterface = RetrofitFactory.getRetrofit().create(ApiInterface.class);
       ArrayList<String> categories = new ArrayList<String>() ;
       categories.add("AI");

        User user = new User(username, password , passwordConf , firstName , lastName , email , categories);
        Call<User> postLogin = apiInterface.postRegister(user);
        postLogin.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.INVISIBLE);

                if (response.code() == 201) {

                    if (isAdded()) {

                        Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_categoriesFragment);
                    }
                    //save user login
                    Boolean saveUserLogin = true;
                    sharedPreferences = requireActivity().getSharedPreferences("saveUserLogin", 0);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("saveUserLogin", saveUserLogin);
                    editor.commit();

                }

                else if (response.code()== 400){
                    try {
                        jObjError = new JSONObject(response.errorBody().string());
                        try {
                            usernameError = jObjError.getJSONArray("username");
                            userError=usernameError.getString(0);
                        }
                        catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                            if (e.getMessage().equals("No value for username") ){
                                userError="";
                            }
                        }
                        try {
                            passwordError = jObjError.getJSONArray("password");
                            for (int i=0 ; i<passwordError.length() ; i++) {
                                passsError = passwordError.getString(i);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());
                            if (e.getMessage().equals("No value for password")){
                                passsError="";
                            }
                        }
                        try {
                            emailError = jObjError.getJSONArray("email");
                            emaillError=emailError.getString(0);
                        } catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e.getMessage());

                            if (e.getMessage().equals("No value for email")){
                                emaillError="";
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (userError=="") {
                        Toast.makeText(getContext(),  emaillError + "\n" + passsError, Toast.LENGTH_LONG).show();
                    }else if (emaillError==""){
                        Toast.makeText(getContext(), userError + "\n"  + passsError, Toast.LENGTH_LONG).show();
                    }
                    else if (passsError==""){
                        Toast.makeText(getContext(), userError + "\n" + emaillError , Toast.LENGTH_LONG).show();
                    }
                    else if (userError=="" && emaillError==""){
                        Toast.makeText(getContext(),  passsError, Toast.LENGTH_LONG).show();
                    }
                    else if (userError=="" && passsError==""){
                        Toast.makeText(getContext(),  emaillError, Toast.LENGTH_LONG).show();
                    }
                    else if (emaillError=="" && passsError==""){
                        Toast.makeText(getContext(),  userError, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), userError + "\n" + emaillError + "\n" + passsError, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Something Error", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if (isAdded()) {
                    progressBar.setVisibility(View.INVISIBLE);

                    Toast.makeText(requireContext(), "No Internet Connection" , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void clickSignUpBtn(){
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                signUpBtn.getBackground().setAlpha(100);
                progressBar.setVisibility(View.VISIBLE);

                if (isValid()) {
//                    Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_categoriesFragment);

                    postRegister();
                }
                else {
                    Toast.makeText(requireContext(), "please enter all fields ", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }

            }
        });
    }

    public void onBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(view).popBackStack();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

    }

}
