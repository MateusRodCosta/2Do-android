package com.example.mateus.a2do_trabalho;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mateus.a2do_trabalho.modelo.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog alertDialog;


    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Configurando os Alerts: Requisitanto
        alertDialog =  new ProgressDialog(LoginActivity.this);
        alertDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        alertDialog.setMessage("Aguarde enquando procuramos suas tarefas..");
        alertDialog.setCanceledOnTouchOutside(false);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button buttonEntrar = (Button) findViewById(R.id.buttonEntrar);
        Button buttonNovaConta = (Button) findViewById(R.id.buttonNovaConta);

        buttonEntrar.setOnClickListener(entrar());

        buttonNovaConta.setOnClickListener(criarConta());

    }

    private View.OnClickListener criarConta() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, NovaContaActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener entrar() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout editUsernameWrapper = (TextInputLayout) findViewById(R.id.editUsernameWrapper);
                TextInputLayout editPasswordWrapper = (TextInputLayout) findViewById(R.id.editPasswordWrapper);

                final Usuario user = new Usuario();
                user.setUsername(editUsernameWrapper.getEditText().getText().toString().trim());
                user.setPassword(editPasswordWrapper.getEditText().getText().toString().trim());

                RequestQueue queue = Singleton.getInstance(context.getApplicationContext()).getRequestQueue();

                String url = "http://mrc307146.esy.es/usuario.php?action=validate";
                alertDialog.show();
                StringRequest req = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("success")) {
                                    alertDialog.cancel();
                                    Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                                    intent.putExtra("username", user.getUsername());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    alertDialog.cancel();
                                    Toast.makeText(context, "Usuário e/ou senha inválidos", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alertDialog.cancel();
                        Toast.makeText(context, "Não foi possível fazer login", Toast.LENGTH_LONG).show();
                        Log.e("Volley Error", error.getMessage());
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", user.getUsername());
                        params.put("password", user.getPassword());
                        return params;
                    }
                };
                queue.add(req);
            }
        };
    }
}
