package br.com.amazongas.aulawhatsapp.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.amazongas.aulawhatsapp.activities.MensagensActivity;
import br.com.amazongas.aulawhatsapp.databinding.ItemContatosBinding;
import br.com.amazongas.aulawhatsapp.model.Usuario;
import br.com.amazongas.aulawhatsapp.utils.Constantes;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.ContatosViewHolder> {

    private List<Usuario> listaContatos = new ArrayList<Usuario>();
    public void adicionarLista(List<Usuario> lista) {
        this.listaContatos = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContatosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContatosBinding contatoView = ItemContatosBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ContatosViewHolder(contatoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContatosViewHolder holder, int position) {
        Usuario user = listaContatos.get(position);
        holder.bind(user, this);
    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    public static class ContatosViewHolder extends RecyclerView.ViewHolder {
        private final ItemContatosBinding binding;
        public ContatosViewHolder(@NonNull ItemContatosBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Usuario user, final ContatosAdapter myAdapter) {
            String imgUrl = user.getFoto();
            if (!imgUrl.isEmpty()) {
                Picasso.get()
                        .load(imgUrl)
                        .into(binding.imgItemFoto);
            }
            binding.txtItemNome.setText(user.getNome());
            Context c = binding.cLtContatoContainer.getContext();

            binding.cLtContatoContainer.setOnClickListener(view -> {
                Intent i = new Intent(c, MensagensActivity.class);
                i.putExtra("destUserData", user);
//                i.putExtra("origem", Constantes.ORIGEM_CONTATO);
                startActivity(c, i, null);
            });
        }
    }
}
