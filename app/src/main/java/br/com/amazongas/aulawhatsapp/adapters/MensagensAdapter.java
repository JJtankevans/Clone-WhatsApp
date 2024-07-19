package br.com.amazongas.aulawhatsapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import br.com.amazongas.aulawhatsapp.databinding.ItemMensagensDestinatarioBinding;
import br.com.amazongas.aulawhatsapp.databinding.ItemMensagensRemetenteBinding;
import br.com.amazongas.aulawhatsapp.model.Mensagem;
import br.com.amazongas.aulawhatsapp.utils.Constantes;

public class MensagensAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Mensagem> listaMensagens = new ArrayList<Mensagem>();

    public void adicionarLista(List<Mensagem> mensagens) {
        this.listaMensagens = mensagens;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        //Pega a mensagem
        Mensagem msgAtual = listaMensagens.get(position);
        //Pega o id do usuario Logado
        String idUsuarioLogado = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        if(idUsuarioLogado.equals(msgAtual.getIdUsuario())) {
            return Constantes.TIPO_REMETENTE;
        } else {
            return Constantes.TIPO_DESTINATARIO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*É preciso descobrir se a mensagem é de destinatário ou não e para isso
        * é necessário usar o método getItemViewType */
        if (viewType == Constantes.TIPO_DESTINATARIO) {
            ItemMensagensDestinatarioBinding destView = ItemMensagensDestinatarioBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            );

            return new MensagensDestinatarioViewHolder(destView);
        }

        ItemMensagensRemetenteBinding remetView = ItemMensagensRemetenteBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new MensagensRemetenteViewHolder(remetView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mensagem msg = listaMensagens.get(position);

        if(holder instanceof MensagensRemetenteViewHolder) {
            ((MensagensRemetenteViewHolder) holder).bind(msg);
        } else {
            ((MensagensDestinatarioViewHolder) holder).bind(msg);
        }
    }

    @Override
    public int getItemCount() {
        return listaMensagens.size();
    }

    public class MensagensRemetenteViewHolder extends RecyclerView.ViewHolder {
        private final ItemMensagensRemetenteBinding binding;

        public MensagensRemetenteViewHolder(@NonNull ItemMensagensRemetenteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Mensagem msg) {
            String txtMensagem = msg.getMensagem();
            binding.txtMensagemRemetente.setText(txtMensagem);
        }
    }

    public class MensagensDestinatarioViewHolder extends RecyclerView.ViewHolder {
        private final ItemMensagensDestinatarioBinding binding;

        public MensagensDestinatarioViewHolder(@NonNull ItemMensagensDestinatarioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Mensagem msg) {
            String txtMensagem = msg.getMensagem();
            binding.txtMensagemDestinatario.setText(txtMensagem);
        }
    }
}
