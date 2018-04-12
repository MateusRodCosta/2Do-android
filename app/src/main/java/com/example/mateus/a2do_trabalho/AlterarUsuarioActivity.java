package com.example.mateus.a2do_trabalho;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.mateus.a2do_trabalho.modelo.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AlterarUsuarioActivity extends AppCompatActivity {

    private Context context = this;

    private TextView textNomeUser;
    private TextInputLayout editNomeWrapper;
    private TextInputLayout editPassword;
    private TextInputLayout editPasswordNovo;
    private RadioGroup radioSexo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_usuario);

        textNomeUser = (TextView) findViewById(R.id.textNomeUser);
        editNomeWrapper = (TextInputLayout) findViewById(R.id.editNomeWrapperForm);
        editPassword = (TextInputLayout) findViewById(R.id.editPasswordWrapperForm);
        editPasswordNovo = (TextInputLayout) findViewById(R.id.editPasswordNovoWrapperForm);
        radioSexo = (RadioGroup) findViewById(R.id.radioGroupSexoForm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarUserForm);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Alterar Usuário");
        recuperarUsuario();
    }

    private void recuperarUsuario() {
        RequestQueue queue = Singleton.getInstance(context.getApplicationContext()).getRequestQueue();

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        final Usuario user = new Usuario();

        String url = "http://mrc307146.esy.es/usuario.php?action=select&username=" + username;

        JsonObjectRequest req = new JsonObjectRequest
                (url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user.setUsername(response.getString("username"));
                            user.setNome(response.getString("nome"));
                            user.setSexo(response.getString("sexo").charAt(0));

                            textNomeUser.setText(user.getUsername());
                            editNomeWrapper.getEditText().setText(user.getNome());
                            RadioButton radioM = (RadioButton) findViewById(R.id.radioMasculinoForm);
                            RadioButton radioF = (RadioButton) findViewById(R.id.radioFemininoForm);

                            if(user.getSexo() == 'F')
                                radioF.setChecked(true);
                            else
                                radioM.setChecked(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        queue.add(req);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_update, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.form_update_action:
                atualizarUsuario();
                return true;

            case R.id.form_delete_action:
                Toast.makeText(this, "Não implementado", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void atualizarUsuario() {
        final Usuario user = new Usuario();
        user.setUsername(textNomeUser.getText().toString());
        user.setNome(editNomeWrapper.getEditText().getText().toString());
        final String senha, novasenha;
        senha = editPassword.getEditText().getText().toString();
        novasenha = editPasswordNovo.getEditText().getText().toString();

        if(radioSexo.getCheckedRadioButtonId() == -1) {
            user.setSexo('0');
        } else if (radioSexo.getCheckedRadioButtonId() == R.id.radioFemininoForm) {
            user.setSexo('F');
        } else {
            user.setSexo('M');
        }

        RequestQueue queue = Singleton.getInstance(context.getApplicationContext()).getRequestQueue();

        String url = "http://mrc307146.esy.es/usuario.php?action=update";
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")) {
                            Toast.makeText(context, "Sucesso ao atualizar", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(context, "Erro ao atulizar", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Não foi possível atualizar", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                params.put("password", senha);
                params.put("nome", user.getNome());
                params.put("sexo", String.valueOf(user.getSexo()));
                params.put("newpassword", novasenha);
                return params;
            }
        };

        queue.add(req);
    }
}
