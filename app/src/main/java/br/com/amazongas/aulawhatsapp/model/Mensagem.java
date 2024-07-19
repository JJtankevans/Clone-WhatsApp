package br.com.amazongas.aulawhatsapp.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Mensagem {
    private String idUsuario;
    private String mensagem;
    @ServerTimestamp
    private Date data;

    public Mensagem() {
        this.idUsuario = "";
        this.mensagem = "";
        this.data = null;
    }
    public Mensagem(String idUsuario, String mensagem) {
        this.idUsuario = idUsuario;
        this.mensagem = mensagem;
        this.data = null;
    }
    public Mensagem(String idUsuario, String mensagem, Date data) {
        this.idUsuario = idUsuario;
        this.mensagem = mensagem;
        this.data = data;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Mensagem{" +
                "idUsuario='" + idUsuario + '\'' +
                ", mensagem='" + mensagem + '\'' +
                ", data=" + data +
                '}';
    }
}
