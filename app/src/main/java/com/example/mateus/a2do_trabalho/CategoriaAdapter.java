package com.example.mateus.a2do_trabalho;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mateus.a2do_trabalho.modelo.Categoria;

import java.util.List;

/**
 * Created by Mateus on 24/10/2016.
 */

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textId;
        public TextView textNome;
        public ViewHolder(View itemView) {
            super(itemView);

            textId = (TextView) itemView.findViewById(R.id.textIdCategoria);
            textNome = (TextView) itemView.findViewById(R.id.textNomeCategoria);
        }
    }

    private List<Categoria> listaCategorias;
    private Context context;
    private String usuario;
    private boolean isSelection;

    public CategoriaAdapter(Context context, List<Categoria> listaCategorias, String usuario, boolean isSelection) {
        this.context = context;
        this.listaCategorias = listaCategorias;
        this.usuario = usuario;
        this.isSelection = isSelection;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public CategoriaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View categoriaView = inflater.inflate(R.layout.item_categoria, parent, false);

        final TextView textId = (TextView) categoriaView.findViewById(R.id.textIdCategoria);
        final TextView textNome = (TextView) categoriaView.findViewById(R.id.textNomeCategoria);

        categoriaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.parseInt(textId.getText().toString());
                String nome = textNome.getText().toString();
                if(!isSelection) {
                    Intent intent = new Intent(context, CategoriaFormActivity.class);
                    intent.putExtra("isUpdate", true);
                    intent.putExtra("id", id);
                    intent.putExtra("nome", nome);
                    intent.putExtra("username", usuario);

                    context.startActivity(intent);
                } else {
                    Intent intentRetorno = new Intent();
                    intentRetorno.putExtra("idCategoriaSel", id);
                    intentRetorno.putExtra("nomeCategoriaSel", nome);
                    ((Activity) context).setResult(Activity.RESULT_OK, intentRetorno);
                    ((Activity) context).finish();
                }
            }
        });

        return new ViewHolder(categoriaView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Categoria categoria = listaCategorias.get(position);

        TextView textID = holder.textId;
        TextView textNome = holder.textNome;

        textID.setText(String.valueOf(categoria.getId()));
        textNome.setText(categoria.getNome());

    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

}
