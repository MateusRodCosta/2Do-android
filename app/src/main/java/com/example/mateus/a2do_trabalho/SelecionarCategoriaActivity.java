package com.example.mateus.a2do_trabalho;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.mateus.a2do_trabalho.modelo.Categoria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelecionarCategoriaActivity extends AppCompatActivity {

    private Context context;
    private List<Categoria> listaCategorias;
    static RecyclerView recyclerViewCategoriasSel;
    private String username;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecionar_categoria);

        context = this;
        username = getIntent().getStringExtra("username");

        recyclerViewCategoriasSel = (RecyclerView) findViewById(R.id.recyclerViewCategoriasSel);

        toolbar = (Toolbar) findViewById(R.id.toolbarSelCat);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Selecionar Categoria");


        listaCategorias = new ArrayList<Categoria>();

        recuperarCategorias();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void recuperarCategorias() {
        RequestQueue queue = Singleton.getInstance(context).getRequestQueue();

        String url = "http://mrc307146.esy.es/categoria.php?action=select";

        JsonArrayRequest req = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        listaCategorias.clear();
                        for(int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonCategoria = response.getJSONObject(i);
                                int id = jsonCategoria.getInt("id");
                                String nome = jsonCategoria.getString("nome");

                                Categoria c = new Categoria();
                                c.setId(id);
                                c.setNome(nome);
                                listaCategorias.add(c);
                            } catch (JSONException e) {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                        CategoriaAdapter adapter = new CategoriaAdapter(context, listaCategorias, username, true);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);

                        recyclerViewCategoriasSel.setAdapter(adapter);
                        recyclerViewCategoriasSel.setLayoutManager(layoutManager);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        queue.add(req);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentRetorno = new Intent();
                setResult(Activity.RESULT_CANCELED, intentRetorno);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intentRetorno = new Intent();
        setResult(Activity.RESULT_CANCELED, intentRetorno);
        finish();
        super.onBackPressed();  // optional depending on your needs
    }
}
