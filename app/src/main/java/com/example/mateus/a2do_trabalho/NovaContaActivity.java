package com.example.mateus.a2do_trabalho;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mateus.a2do_trabalho.modelo.Usuario;

import java.util.HashMap;
import java.util.Map;

public class NovaContaActivity extends AppCompatActivity {
    private ProgressDialog alertDialog;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_conta);

        //Configurando os Alerts: Requisitanto
        alertDialog =  new ProgressDialog(NovaContaActivity.this);
        alertDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        alertDialog.setMessage("Salvando suas informações..");
        alertDialog.setCanceledOnTouchOutside(false);
        Button buttonCriar = (Button) findViewById(R.id.buttonCriar);
        buttonCriar.setOnClickListener(criarConta());
    }

    private View.OnClickListener criarConta() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout editUsernameWrapper = (TextInputLayout) findViewById(R.id.editUsernameNovoWrapper);
                TextInputLayout editPasswordWrapper = (TextInputLayout) findViewById(R.id.editPasswordNovoWrapper);
                TextInputLayout editNomeWrapper = (TextInputLayout) findViewById(R.id.editNomeWrapper);
                RadioGroup radioGroupSexo = (RadioGroup) findViewById(R.id.radioGroupSexo);

                final Usuario newUser = new Usuario();

                newUser.setUsername(editUsernameWrapper.getEditText().getText().toString().trim());
                newUser.setPassword(editPasswordWrapper.getEditText().getText().toString().trim());
                newUser.setNome(editNomeWrapper.getEditText().getText().toString().trim());
                if (radioGroupSexo.getCheckedRadioButtonId() == R.id.radioFeminino) {
                    newUser.setSexo('F');
                } else {
                    newUser.setSexo('M');
                }

                RequestQueue queue = Singleton.getInstance(context.getApplicationContext()).getRequestQueue();

                String url = "http://mrc307146.esy.es/usuario.php?action=insert";
                alertDialog.show();
                StringRequest req = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equals("success")) {
                                    alertDialog.cancel();
                                    Intent intent = new Intent(NovaContaActivity.this, DrawerActivity.class);
                                    intent.putExtra("username", newUser.getUsername());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    alertDialog.cancel();
                                    Toast.makeText(context, "Usuário em uso", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alertDialog.cancel();
                        Toast.makeText(context, "Não foi possível registrar", Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", newUser.getUsername());
                        params.put("password", newUser.getPassword());
                        params.put("nome", newUser.getNome());
                        params.put("sexo", String.valueOf(newUser.getSexo()));
                        return params;
                    }
                };

                queue.add(req);
            }
        };
    }
}
