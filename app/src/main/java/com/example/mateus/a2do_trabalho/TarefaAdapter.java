package com.example.mateus.a2do_trabalho;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mateus.a2do_trabalho.modelo.Categoria;
import com.example.mateus.a2do_trabalho.modelo.Tarefa;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mateus on 25/10/2016.
 */

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textId;
        public TextView textDescricao;
        public TextView textDataLimite;
        public TextView textDataLembrete;
        public TextView textIdCategoria;
        public TextView textCategoria;

        public ViewHolder(View itemView) {
            super(itemView);

            textId = (TextView) itemView.findViewById(R.id.textIdTarefa);
            textDescricao = (TextView) itemView.findViewById(R.id.textDescricaoTarefa);
            textDataLimite = (TextView) itemView.findViewById(R.id.textDataLimite);
            textDataLembrete = (TextView) itemView.findViewById(R.id.textDataLembrete);
            textIdCategoria = (TextView) itemView.findViewById(R.id.textIdCategoriaTarefa);
            textCategoria = (TextView) itemView.findViewById(R.id.textCategoria);
        }
    }

    private List<Tarefa> listaTarefas;
    private Context context;
    private String usuario;

    public TarefaAdapter(Context context, List<Tarefa> listaTarefas, String usuario) {
        this.context = context;
        this.listaTarefas = listaTarefas;
        this.usuario = usuario;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public TarefaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tarefaView = inflater.inflate(R.layout.item_tarefa, parent, false);

        final TextView textId = (TextView) tarefaView.findViewById(R.id.textIdTarefa);
        final TextView textDescricao = (TextView) tarefaView.findViewById(R.id.textDescricaoTarefa);
        final TextView textDataLimite = (TextView) tarefaView.findViewById(R.id.textDataLimite);
        final TextView textDataLembrete = (TextView) tarefaView.findViewById(R.id.textDataLembrete);
        final TextView textIdCategoria = (TextView) tarefaView.findViewById(R.id.textIdCategoriaTarefa);
        final TextView textCategoria = (TextView) tarefaView.findViewById(R.id.textCategoria);

        tarefaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.parseInt(textId.getText().toString());

                Intent intent = new Intent(context, TarefaFormActivity.class);
                intent.putExtra("isUpdate", true);
                intent.putExtra("id", id);
                intent.putExtra("username", usuario);
                intent.putExtra("descricao", textDescricao.getText().toString());
                intent.putExtra("dataLimite", textDataLimite.getText().toString());
                intent.putExtra("dataLembrete", textDataLembrete.getText().toString());
                intent.putExtra("idCategoria", Integer.parseInt(textIdCategoria.getText().toString()));
                intent.putExtra("nomeCategoria", textCategoria.getText().toString());

                context.startActivity(intent);
            }
        });

        tarefaView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int id = Integer.parseInt(textId.getText().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Finalizar tarefa");
                builder.setMessage("Deseja finalizar?");

                builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        finalizarTarefa();

                        dialog.dismiss();
                    }

                    private void finalizarTarefa() {
                        RequestQueue queue = Singleton.getInstance(context.getApplicationContext()).getRequestQueue();
                        String url = "http://mrc307146.esy.es/tarefa.php?action=updatestatus";
                        StringRequest req = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response.equals("success")) {
                                            Toast.makeText(context.getApplicationContext(), "Tarefa finalizada", Toast.LENGTH_LONG).show();
                                        } else {
                                            //alertDialog.cancel();
                                            Toast.makeText(context, "Não foi possível finalizar a tarefa", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, "Erro ao finalizar tarefa", Toast.LENGTH_LONG).show();
                                Log.e("Volley Error", error.getMessage());
                                error.printStackTrace();
                            }
                        }) {
                            @Override
                            public Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("id", String.valueOf(id));
                                return params;
                            }
                        };

                        queue.add(req);
                    }
                });

                builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            return true;
            }
        });

        TarefaAdapter.ViewHolder viewHolder = new TarefaAdapter.ViewHolder(tarefaView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TarefaAdapter.ViewHolder holder, int position) {
        Tarefa tarefa = listaTarefas.get(position);

        TextView textId = holder.textId;
        TextView textDescricao = holder.textDescricao;
        TextView textDataLimite = holder.textDataLimite;
        TextView textDataLembrete = holder.textDataLembrete;
        TextView textIdCategoria = holder.textIdCategoria;
        TextView textCategoria = holder.textCategoria;

        textId.setText(String.valueOf(tarefa.getId()));
        textDescricao.setText(tarefa.getDescricao());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        textDataLimite.setText(formatter.format(tarefa.getDataLimite()));
        textDataLembrete.setText(formatter.format(tarefa.getDataLembrete()));
        textIdCategoria.setText(String.valueOf(tarefa.getCategoria().getId()));
        textCategoria.setText(tarefa.getCategoria().getNome());
    }

    @Override
    public int getItemCount() {
        return listaTarefas.size();
    }

}
