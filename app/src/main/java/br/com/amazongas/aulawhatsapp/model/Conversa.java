package br.com.amazongas.aulawhatsapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Conversa implements Parcelable {
    String idUsuarioRemetente;
    String idUsuarioDestinatario;
    String foto;
    String nome;
    String ultimaMensagem;
    @ServerTimestamp
    Date data;

    public Conversa() {
    }

    public Conversa(String idUsuarioRemetente, String idUsuarioDestinatario, String foto,
                    String nome, String ultimaMensagem, Date data) {
        this.idUsuarioRemetente = idUsuarioRemetente;
        this.idUsuarioDestinatario = idUsuarioDestinatario;
        this.foto = foto;
        this.nome = nome;
        this.ultimaMensagem = ultimaMensagem;
        this.data = data;
    }

    public Conversa(String idUsuarioRemetente, String idUsuarioDestinatario, String foto,
                    String nome, String ultimaMensagem) {
        this.idUsuarioRemetente = idUsuarioRemetente;
        this.idUsuarioDestinatario = idUsuarioDestinatario;
        this.foto = foto;
        this.nome = nome;
        this.ultimaMensagem = ultimaMensagem;
    }

    protected Conversa(Parcel in) {
        idUsuarioRemetente = in.readString();
        idUsuarioDestinatario = in.readString();
        foto = in.readString();
        nome = in.readString();
        ultimaMensagem = in.readString();
    }

    public static final Creator<Conversa> CREATOR = new Creator<Conversa>() {
        @Override
        public Conversa createFromParcel(Parcel in) {
            return new Conversa(in);
        }

        @Override
        public Conversa[] newArray(int size) {
            return new Conversa[size];
        }
    };

    public String getIdUsuarioRemetente() {
        return idUsuarioRemetente;
    }

    public void setIdUsuarioRemetente(String idUsuarioRemetente) {
        this.idUsuarioRemetente = idUsuarioRemetente;
    }

    public String getIdUsuarioDestinatario() {
        return idUsuarioDestinatario;
    }

    public void setIdUsuarioDestinatario(String idUsuarioDestinatario) {
        this.idUsuarioDestinatario = idUsuarioDestinatario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(idUsuarioRemetente);
        dest.writeString(idUsuarioDestinatario);
        dest.writeString(foto);
        dest.writeString(nome);
        dest.writeString(ultimaMensagem);
    }
}
