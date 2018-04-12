package com.example.mateus.a2do_trabalho;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mateus.a2do_trabalho.modelo.Categoria;
import com.example.mateus.a2do_trabalho.modelo.Tarefa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TarefaFragment extends Fragment {

    private Context context;
    private List<Tarefa> listaTarefas;
    private RecyclerView recyclerViewTarefas;
    String username;

    public TarefaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        recyclerViewTarefas.removeAllViews();
        recuperarTarefas(username);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tarefa, container, false);

        username = getActivity().getIntent().getStringExtra("username");

        recyclerViewTarefas = (RecyclerView) v.findViewById(R.id.recyclerViewTarefas);
        listaTarefas = new ArrayList<Tarefa>();

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabTarefa);

        recuperarTarefas(username);

        fab.setOnClickListener(adicionarTarefa());

        return v;
    }

    private View.OnClickListener adicionarTarefa() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TarefaFormActivity.class);
                intent.putExtra("isUpdate", false);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        };
    }

    private void recuperarTarefas(final String username) {
        RequestQueue queue = Singleton.getInstance(context).getRequestQueue();

        String url = "http://mrc307146.esy.es/tarefa.php?action=select&username=" + username;

        JsonArrayRequest req = new JsonArrayRequest
                (url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        listaTarefas.clear();
                        for(int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonTarefa = response.getJSONObject(i);
                                int id = jsonTarefa.getInt("id");
                                String descricao = jsonTarefa.getString("descricao");

                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                Date dataLimite = formatter.parse(jsonTarefa.getString("dataLimite"));
                                Date dataLembrete = formatter.parse(jsonTarefa.getString("dataLembrete"));

                                int idCategoria = jsonTarefa.getInt("idCategoria");
                                String nomeCategoria = jsonTarefa.getString("nomeCategoria");

                                Tarefa t = new Tarefa();
                                t.setId(id);
                                t.setDescricao(descricao);
                                t.setDataLimite(dataLimite);
                                t.setDataLembrete(dataLembrete);

                                Categoria c = new Categoria();
                                c.setId(idCategoria);
                                c.setNome(nomeCategoria);

                                t.setCategoria(c);

                                listaTarefas.add(t);

                            } catch (JSONException | ParseException e) {
                                Toast.makeText(context, "Não foi possível ler os dados", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                        TarefaAdapter adapter = new TarefaAdapter(context, listaTarefas, username);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);

                        recyclerViewTarefas.setAdapter(adapter);
                        recyclerViewTarefas.setLayoutManager(layoutManager);



                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("Volley Error", error.getMessage());
                        error.printStackTrace();
                    }
                });

        queue.add(req);
    }

}
