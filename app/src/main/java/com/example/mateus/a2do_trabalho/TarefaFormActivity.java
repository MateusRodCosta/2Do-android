package com.example.mateus.a2do_trabalho;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mateus.a2do_trabalho.modelo.Categoria;
import com.example.mateus.a2do_trabalho.modelo.Tarefa;
import com.example.mateus.a2do_trabalho.modelo.Usuario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TarefaFormActivity extends AppCompatActivity {

    private TextView textIdTarefa;
    private TextInputLayout editDescricaoWrapper;
    private TextView textIdCategoriaSel;
    private TextView textCategoriaSel;
    private boolean isUpdate;
    private Context context;
    private TextView textDataLimite;
    private TextView textDataLembrete;
    private RequestQueue queue;
    private String baseUrl = "http://mrc307146.esy.es/tarefa.php";
    private Toolbar toolbar;
    private String usuario;
    private Tarefa tar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefa_form);

        context = this;

        textIdTarefa = (TextView) findViewById(R.id.textIdTarefaForm);
        editDescricaoWrapper = (TextInputLayout) findViewById(R.id.editDescricaoTarefaWrapperForm);
        textDataLimite = (TextView) findViewById(R.id.textDataLimiteForm);
        textDataLembrete = (TextView) findViewById(R.id.textDataLembreteForm);
        textIdCategoriaSel = (TextView) findViewById(R.id.textIdCategoriaSel);
        textCategoriaSel = (TextView) findViewById(R.id.textCategoriaSel);

        Intent intent = getIntent();

        usuario = intent.getStringExtra("username");

        isUpdate = intent.getBooleanExtra("isUpdate", false);

        toolbar = (Toolbar) findViewById(R.id.toolbarTarForm);
        setSupportActionBar(toolbar);

        queue = Singleton.getInstance(context.getApplicationContext()).getRequestQueue();

        if(isUpdate) {
            getSupportActionBar().setTitle("Atualizar Tarefa");
            recuperarTarefa(intent);
        } else {
            getSupportActionBar().setTitle("Adicionar Tarefa");

            Date dataAtual = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            textDataLimite.setText(formatter.format(dataAtual));
            textDataLembrete.setText(formatter.format(dataAtual));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textDataLimite.setOnClickListener(escolherDataLimite());
        textDataLembrete.setOnClickListener(escolherDataLembrete());
        textCategoriaSel.setOnClickListener(selecionarCategoria());
    }

    private View.OnClickListener selecionarCategoria() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TarefaFormActivity.this, SelecionarCategoriaActivity.class);
                startActivityForResult(intent, 1);
            }
        };
    }

    private View.OnClickListener escolherDataLimite() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dp = new DatePickerFragment();
                Bundle b = new Bundle();
                b.putString("campo", "limite");
                dp.setArguments(b);
                dp.show(getSupportFragmentManager(), "Data Limite");
            }
        };
    }

    private View.OnClickListener escolherDataLembrete() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dp = new DatePickerFragment();
                Bundle b = new Bundle();
                b.putString("campo", "lembrete");
                dp.setArguments(b);
                dp.show(getSupportFragmentManager(), "Data Lembrete");
            }
        };
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
                inserirTarefa();
                return true;

            case R.id.form_update_action:
                atualizarTarefa();
                return true;

            case R.id.form_delete_action:
                deletarTarefa();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recuperarTarefa(Intent intent) {

        int id = intent.getIntExtra("id", -1);
        String descricao = intent.getStringExtra("descricao");
        String dataLimite = intent.getStringExtra("dataLimite");
        String dataLembrete = intent.getStringExtra("dataLembrete");
        int idCategoria = intent.getIntExtra("idCategoria", -1);
        String nomeCategoria = intent.getStringExtra("nomeCategoria");

        textIdTarefa.setText(String.valueOf(id));
        editDescricaoWrapper.getEditText().setText(descricao);
        textDataLimite.setText(dataLimite);
        textDataLembrete.setText(dataLembrete);
        textIdCategoriaSel.setText(String.valueOf(idCategoria));
        textCategoriaSel.setText(nomeCategoria);
    }

    private void inserirTarefa() {

        tar = new Tarefa();

        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");

        tar.setDescricao(editDescricaoWrapper.getEditText().getText().toString());
        try {
            tar.setDataLimite(parser.parse(textDataLimite.getText().toString()));
            tar.setDataLembrete(parser.parse(textDataLembrete.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        final String idCategoria = textIdCategoriaSel.getText().toString();


        String url = baseUrl + "?action=insert";
        //alertDialog.show();
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")) {
                            //alertDialog.cancel();
                            finish();
                            Toast.makeText(context.getApplicationContext(), "Tarefa inserida", Toast.LENGTH_LONG).show();
                        } else {
                            //alertDialog.cancel();
                            Toast.makeText(context, "Não foi possível inserir a tarefa", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //alertDialog.cancel();
                Toast.makeText(context, "Erro ao inserir tarefa", Toast.LENGTH_LONG).show();
                Log.e("Volley Error", error.getMessage());
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Map<String, String> params = new HashMap<>();
                params.put("descricao", tar.getDescricao());
                params.put("datalimite", formatter.format(tar.getDataLimite()));
                params.put("datalembrete", formatter.format(tar.getDataLembrete()));
                params.put("username", usuario);
                params.put("idcategoria", idCategoria);
                return params;
            }
        };

        queue.add(req);
    }

    private void atualizarTarefa() {

        tar = new Tarefa();

        SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");

        tar.setId(Integer.parseInt(textIdTarefa.getText().toString()));
        tar.setDescricao(editDescricaoWrapper.getEditText().getText().toString());
        try {
            tar.setDataLimite(parser.parse(textDataLimite.getText().toString()));
            tar.setDataLembrete(parser.parse(textDataLembrete.getText().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Categoria cat = new Categoria();
        cat.setId(Integer.parseInt(textIdCategoriaSel.getText().toString()));
        cat.setNome(textCategoriaSel.getText().toString());
        tar.setCategoria(cat);

        String url = baseUrl +"?action=update";
        //alertDialog.show();
        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")) {
                            //alertDialog.cancel();
                            finish();
                            Toast.makeText(context.getApplicationContext(), "Tarefa atualizada", Toast.LENGTH_LONG).show();
                        } else {
                            //alertDialog.cancel();
                            Toast.makeText(context, "Não foi possível atualizar a tarefa", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //alertDialog.cancel();
                Toast.makeText(context, "Erro ao atualizar tarefa", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(tar.getId()));
                params.put("descricao", tar.getDescricao());
                params.put("datalimite", formatter.format(tar.getDataLimite()));
                params.put("datalembrete", formatter.format(tar.getDataLembrete()));
                params.put("idcategoria", String.valueOf(tar.getCategoria().getId()));
                return params;
            }
        };
        queue.add(req);

    }

    private void deletarTarefa() {
        int id = Integer.parseInt(textIdTarefa.getText().toString());

        tar = new Tarefa();
        tar.setId(id);

        String url = baseUrl + "?action=delete";

        StringRequest req = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("success")) {
                            finish();
                            Toast.makeText(context.getApplicationContext(), "Tarefa deletada", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Não foi possível deletar a tarefa", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Erro ao deletar tarefa", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(tar.getId()));
                return params;
            }
        };
        queue.add(req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int idCategoria = data.getIntExtra("idCategoriaSel", -1);
                String nomeCategoria = data.getStringExtra("nomeCategoriaSel");
                textIdCategoriaSel.setText(String.valueOf(idCategoria));
                textCategoriaSel.setText(nomeCategoria);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Nenhuma categoria selecionada", Toast.LENGTH_LONG).show();
            }
        }
    }
}
