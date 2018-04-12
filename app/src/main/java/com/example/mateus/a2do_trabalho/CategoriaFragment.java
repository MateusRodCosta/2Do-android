package com.example.mateus.a2do_trabalho;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CategoriaFragment extends Fragment {

    private Context context;
    private List<Categoria> listaCategorias;
    static RecyclerView recyclerViewCategorias;
    private String username;

    public CategoriaFragment() {
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

        recyclerViewCategorias.removeAllViews();
        recuperarCategorias();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_categoria, container, false);

        username = getActivity().getIntent().getStringExtra("username");

        recyclerViewCategorias = (RecyclerView) v.findViewById(R.id.recyclerViewCategorias);
        FloatingActionButton fabCategoria = (FloatingActionButton) v.findViewById(R.id.fabCategoria);

        listaCategorias = new ArrayList<Categoria>();

        recuperarCategorias();

        fabCategoria.setOnClickListener(adicionarCategoria());

        return v;
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
                        CategoriaAdapter adapter = new CategoriaAdapter(context, listaCategorias, username, false);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);

                        recyclerViewCategorias.setAdapter(adapter);
                        recyclerViewCategorias.setLayoutManager(layoutManager);

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

    private View.OnClickListener adicionarCategoria() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoriaFormActivity.class);
                intent.putExtra("isUpdate", false);
                startActivity(intent);
            }
        };
    }

}
