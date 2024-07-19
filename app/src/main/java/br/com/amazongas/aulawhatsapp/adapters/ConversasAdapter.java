package br.com.amazongas.aulawhatsapp.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.amazongas.aulawhatsapp.activities.MensagensActivity;
import br.com.amazongas.aulawhatsapp.databinding.ItemConversasBinding;
import br.com.amazongas.aulawhatsapp.model.Conversa;
import br.com.amazongas.aulawhatsapp.model.Usuario;
import br.com.amazongas.aulawhatsapp.utils.Constantes;
import br.com.amazongas.aulawhatsapp.utils.Extensoes;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.ConversasViewHolder> {
    private List<Conversa> listaConversas = new ArrayList<>();

    public void adicionarLista(List<Conversa> lista) {
        this.listaConversas = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConversasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConversasBinding conversaView = ItemConversasBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ConversasViewHolder(conversaView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversasViewHolder holder, int position) {
        Conversa conversa = listaConversas.get(position);
        holder.bind(conversa);
    }

    @Override
    public int getItemCount() {
        return listaConversas.size();
    }

    public static class ConversasViewHolder extends RecyclerView.ViewHolder {
        private final ItemConversasBinding binding;

        public ConversasViewHolder(@NonNull ItemConversasBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Conversa conversa) {
            String imgUrl = conversa.getFoto();
            String ultMsg = conversa.getUltimaMensagem();

            if(!imgUrl.isEmpty()) {
                Picasso.get()
                        .load(imgUrl)
                        .into(binding.imgItemConversaFoto);
            }
            binding.txtItemConversaNome.setText(conversa.getNome());
            binding.txtItemConversaUltMsg.setText(ultMsg);
            Context c = binding.cLtConversaContainer.getContext();

            binding.cLtConversaContainer.setOnClickListener(v -> {
                Intent i = new Intent(c, MensagensActivity.class);
                Usuario usuario = new Usuario(conversa.getIdUsuarioDestinatario(), conversa.getNome(), "", conversa.getFoto());

                i.putExtra("destUserData", usuario);
//                i.putExtra("origem", Constantes.ORIGEM_CONVERSA);
                startActivity(c, i, null);
            });
        }
    }
}
