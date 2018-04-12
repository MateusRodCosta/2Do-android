package com.example.mateus.a2do_trabalho.modelo;

import java.util.Date;

/**
 * Created by Mateus on 24/10/2016.
 */

public class Tarefa {

    private int id;
    private String descricao;
    private Date dataLimite;
    private Date dataLembrete;
    private Date dataRealizacao;
    private char status;
    private Usuario usuario;
    private Categoria categoria;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(Date dataLimite) {
        this.dataLimite = dataLimite;
    }

    public Date getDataLembrete() {
        return dataLembrete;
    }

    public void setDataLembrete(Date dataLembrete) {
        this.dataLembrete = dataLembrete;
    }

    public Date getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(Date dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

}
