package com.example.mateus.a2do_trabalho;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mateus.a2do_trabalho.modelo.Categoria;

import java.util.HashMap;
import java.util.Map;

public class CategoriaFormActivity extends AppCompatActivity {

    private final Context context = this;
    private Toolbar toolbar;
    private boolean isUpdate;
    private RequestQueue queue;
    private Categoria cat;
    private TextView textIdCategoria;
    private TextInputLayout editNomeCategoriaWrapper;
    private String baseUrl = "http://mrc307146.esy.es/categoria.php";
    private ProgressDialog alertDialog;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_form);

        //Configurando os Alerts: Requisitanto
        alertDialog =  new ProgressDialog(CategoriaFormActivity.this);
        alertDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        alertDialog.setMessage("Aguarde..");
        alertDialog.setCanceledOnTouchOutside(false);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");


        isUpdate = intent.getBooleanExtra("isUpdate", false);
        int id = intent.getIntExtra("id", -1);
        String nome = intent.getStringExtra("nome");

        textIdCategoria = (TextView) findViewById(R.id.textIdCategoriaForm);
        editNomeCategoriaWrapper = (TextInputLayout) findViewById(R.id.editNomeCategoriaWrapperForm);

        toolbar = (Toolbar) findViewById(R.id.toolbarCatForm);
        setSupportActionBar(toolbar);

        queue = Singleton.getInstance(context.getApplicationContext()).getRequestQueue();

        if(isUpdate) {
            getSupportActionBar().setTitle("Atualizar Categoria");
            recuperarCategoria(id, nome);
        } else {
            getSupportActionBar().setTitle("Adicionar Categoria");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void recuperarCategoria(int id, String nome) {
        cat = new Categoria();

        cat.setId(id);
        cat.setNome(nome);

        textIdCategoria.setText(String.valueOf(cat.getId()));
        editNomeCategoriaWrapper.getEditText().setText(cat.getNome());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if(isUpdate)
            getMenuInflater().inflate(R.menu.form_update, menu);
        else
            getMenuInflater().inflate(R.menu.form_insert, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.form_insert_action:
                inserirCategoria();
                return true;

            case R.id.form_update_action:
                atualizarCategoria();
                return true;

            case R.id.form_delete_action:
                deletarCategoria();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inserirCategoria() {
        String nome = editNomeCategoriaWrapper.getEditText().getText().toString().trim();

        cat = new Categoria();
        cat.setNome(nome);

        String url = baseUrl + "?action=insert";
        alertDialog.show();
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")) {
                            alertDialog.cancel();
                            finish();
                            Toast.makeText(context.getApplicationContext(), "Categoria inserida", Toast.LENGTH_LONG).show();
                        } else {
                            alertDialog.cancel();
                            Toast.makeText(context, "Não foi possível inserir a categoria", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.cancel();
                Toast.makeText(context, "Erro ao inserir categoria", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nome", cat.getNome());
                return params;
            }
        };

        queue.add(req);
    }

    private void atualizarCategoria() {
        int id = Integer.parseInt(textIdCategoria.getText().toString());
        String nome = editNomeCategoriaWrapper.getEditText().getText().toString().trim();

        cat = new Categoria();
        cat.setId(id);
        cat.setNome(nome);

        String url = baseUrl +"?action=update";
        alertDialog.show();
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")) {
                            alertDialog.cancel();
                            finish();Toast.makeText(context.getApplicationContext(), "Categoria atualizada", Toast.LENGTH_LONG).show();
                        } else {
                            alertDialog.cancel();
                            Toast.makeText(context, "Não foi possível atualizar a categoria", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.cancel();
                Toast.makeText(context, "Erro ao atualizar categoria", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(cat.getId()));
                params.put("nome", cat.getNome());
                params.put("username", username);
                return params;
            }
        };
        queue.add(req);

    }

    private void deletarCategoria() {
        int id = Integer.parseInt(textIdCategoria.getText().toString());

        cat = new Categoria();
        cat.setId(id);

        String url = baseUrl + "?action=delete";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")) {
                            finish();
                            Toast.makeText(context.getApplicationContext(), "Categoria deletada", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Não foi possível atualizar a categoria", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Erro ao atualizar categoria", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(cat.getId()));
                return params;
            }
        };
        queue.add(req);
    }

}
