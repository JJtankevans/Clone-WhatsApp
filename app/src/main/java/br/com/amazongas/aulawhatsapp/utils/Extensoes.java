package br.com.amazongas.aulawhatsapp.utils;

import android.content.Context;
import android.widget.Toast;

public class Extensoes {
    public static void exibirMensagem(String menssagem, Context context) {
        Toast.makeText(context, menssagem, Toast.LENGTH_LONG).show();
    }
}
