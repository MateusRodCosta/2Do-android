package com.example.mateus.a2do_trabalho;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class DrawerActivity extends AppCompatActivity {

    private final Context context = this;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private String nomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recuperarUsuario();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() != R.id.navigation_item_1) {
                    if (item.isChecked()) item.setChecked(false);
                    else item.setChecked(true);
                }

                drawerLayout.closeDrawers();

                switch (item.getItemId()){

                    case R.id.navigation_item_1:
                        Intent intent = new Intent(DrawerActivity.this, AlterarUsuarioActivity.class);
                        intent.putExtra("username", nomeUsuario);
                        startActivity(intent);
                        return true;

                    case R.id.navigation_item_2:
                        FragmentTransaction fragmentTransactionTarefa = getSupportFragmentManager().beginTransaction();
                        TarefaFragment fragmentTarefa = new TarefaFragment();
                        fragmentTransactionTarefa.replace(R.id.frame, fragmentTarefa);
                        fragmentTransactionTarefa.commit();
                        getSupportActionBar().setTitle("Tarefas");
                        return true;

                    case R.id.navigation_item_3:
                        FragmentTransaction fragmentTransactionCategoria = getSupportFragmentManager().beginTransaction();
                        CategoriaFragment fragmentCategoria = new CategoriaFragment();
                        fragmentTransactionCategoria.replace(R.id.frame, fragmentCategoria);
                        fragmentTransactionCategoria.commit();
                        getSupportActionBar().setTitle("Categorias");
                        return true;

                    default:
                        Toast.makeText(getApplicationContext(),"Algo deu errado",Toast.LENGTH_SHORT).show();
                        return true;

                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        TarefaFragment fragmentTarefa = new TarefaFragment();
        fragmentTransaction.replace(R.id.frame, fragmentTarefa);
        fragmentTransaction.commit();
        toolbar.setTitle("Tarefas");
    }

    private Usuario recuperarUsuario() {
        RequestQueue queue = Singleton.getInstance(context.getApplicationContext()).getRequestQueue();

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        nomeUsuario = username;

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

        return user;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.drawer, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        /*
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }
}
