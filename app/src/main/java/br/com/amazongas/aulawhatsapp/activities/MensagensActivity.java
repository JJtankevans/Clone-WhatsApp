package br.com.amazongas.aulawhatsapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.amazongas.aulawhatsapp.adapters.MensagensAdapter;
import br.com.amazongas.aulawhatsapp.databinding.ActivityMensagensBinding;
import br.com.amazongas.aulawhatsapp.model.Conversa;
import br.com.amazongas.aulawhatsapp.model.Mensagem;
import br.com.amazongas.aulawhatsapp.model.Usuario;
import br.com.amazongas.aulawhatsapp.utils.Constantes;
import br.com.amazongas.aulawhatsapp.utils.Extensoes;

public class MensagensActivity extends AppCompatActivity {
    private ActivityMensagensBinding binding;
    private Usuario dadosDestinatario, dadosRemetente;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration eventoSnapshot;
    private MensagensAdapter mensagensAdapter;
    private final String idUsuarioRemetente = firebaseAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMensagensBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recuperarDadosUsuarios();
        inicializarToolbar();
        inicializarEventosClick();
        inicializarRecyclerView();
        inicilizarListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventoSnapshot.remove();
    }

    private void inicializarRecyclerView() {
        mensagensAdapter = new MensagensAdapter();
        binding.rvConversa.setAdapter(mensagensAdapter);
        binding.rvConversa.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void inicilizarListeners() {
        String idUsuarioRemetente = firebaseAuth.getCurrentUser().getUid();
        String idUsuarioDestinatario = dadosDestinatario.getId();

        if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
            eventoSnapshot = firebaseFirestore
                    .collection(Constantes.COLECAO_MENSAGENS)
                    .document(idUsuarioRemetente)
                    .collection(idUsuarioDestinatario)
                    .orderBy("data", Query.Direction.ASCENDING)
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Extensoes.exibirMensagem("Erro ao recuperar mensagens", getApplicationContext());
                        } else {
                            List<Mensagem> listaMensagens = new ArrayList<Mensagem>();

                            List<DocumentSnapshot> documentosMensagens = querySnapshot.getDocuments();

                            for (DocumentSnapshot itemMsg : documentosMensagens) {
                                Mensagem msg = itemMsg.toObject(Mensagem.class);

                                if (msg != null) {
                                    listaMensagens.add(msg);
                                    Log.i("info_data", msg.getMensagem());
                                }
                            }

                            //Adicionar os itens ao adapter
                            if (!listaMensagens.isEmpty()) {
                                mensagensAdapter.adicionarLista(listaMensagens);
                            }
                        }
                    });

        }
    }

    private void inicializarEventosClick() {
        binding.fAcBtnEnviarMsg.setOnClickListener(view -> {
            String mensagem = binding.txtEditMensagem.getText().toString();
            if (mensagem.trim().isEmpty()) {
                Extensoes.exibirMensagem("Nao pode enviar mensagem vazia", getApplicationContext());
            } else {
                salavarMensagem(mensagem);
            }
        });

    }

    private void salavarMensagem(String textoMensagem) {
        String idUsuarioDestinatario = dadosDestinatario.getId();
        String destinatarioFoto = dadosDestinatario.getFoto().isEmpty() ? "" : dadosDestinatario.getFoto();
        String remetenteFoto = dadosRemetente.getFoto().isEmpty() ? "" : dadosRemetente.getFoto();

        if (idUsuarioRemetente != null && idUsuarioDestinatario != null) {
            Mensagem mensagem = new Mensagem(idUsuarioRemetente, textoMensagem);
            //Salvar para o remetente
            salvarMensagemFireStore(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
            //Quem "inicia" a conversa precisa salvar foto e nome do destinatario
            Conversa conversaRemetente = new Conversa(idUsuarioRemetente, idUsuarioDestinatario,
                    destinatarioFoto, dadosDestinatario.getNome(), textoMensagem);
            salvarConversaFireStore(conversaRemetente);


            //Salvar para o destinatario
            salvarMensagemFireStore(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
            //Quem é o destinatario precisa salvar foto e nome do remetente
            Conversa conversaDestinatario = new Conversa(idUsuarioDestinatario, idUsuarioRemetente,
                    remetenteFoto, dadosRemetente.getNome(), textoMensagem);
            salvarConversaFireStore(conversaDestinatario);

            binding.txtEditMensagem.setText(null);
        }
    }

    private void salvarConversaFireStore(Conversa conversa) {
        firebaseFirestore
                .collection(Constantes.COLECAO_CONVERSA)
                .document(conversa.getIdUsuarioRemetente())
                .collection(Constantes.COLECAO_ULTIMAS_CONVERSAS)
                .document(conversa.getIdUsuarioDestinatario())
                .set(conversa)
                .addOnFailureListener(error -> {
                    Extensoes.exibirMensagem("Erro ao salvar conversa", getApplicationContext());
                });

    }

    private void salvarMensagemFireStore(String remetente, String destinatario, Mensagem mensagem) {

        firebaseFirestore
                .collection(Constantes.COLECAO_MENSAGENS)
                .document(remetente)
                .collection(destinatario)
                .add(mensagem)
                .addOnFailureListener(erro -> {
                    Extensoes.exibirMensagem("Erro ao enviar mensagem", getApplicationContext());
                });
    }

    private void recuperarDadosUsuarios() {
        //RECUPERANDO DADOS USUARIO LOGADO
        if (idUsuarioRemetente != null) {
            firebaseFirestore
                    .collection(Constantes.COLECAO_USUARIOS)
                    .document(idUsuarioRemetente)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Usuario usuarioLogado = documentSnapshot.toObject(Usuario.class);
                        if (usuarioLogado != null) {
                            dadosRemetente = usuarioLogado;
                        }
                    });
        }

        //RECUPERANDO DADOS DESTINATARIO
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                dadosDestinatario = intent.getParcelableExtra("destUserData", Usuario.class);
            } else {
                dadosDestinatario = intent.getParcelableExtra("destUserData");
            }
        }
    }

    private void inicializarToolbar() {
        //Primeiro atribui a toolbar a uma variavel para manipular melhor
        MaterialToolbar toolbar = binding.tbMensagem;
        //Seta o supportActionBar para ter suporte para versoes mais antigas
        setSupportActionBar(toolbar);

        ActionBar supportedToolbar = getSupportActionBar();
        if (supportedToolbar != null) {
            supportedToolbar.setTitle("");
            if (dadosDestinatario != null) {
                binding.txtNomeDestUsuario.setText(dadosDestinatario.getNome());
                String imgUrl = dadosDestinatario.getFoto();
                if (!imgUrl.isEmpty()) {
                    Picasso.get()
                            .load(dadosDestinatario.getFoto())
                            .into(binding.imgPerfilConversa);
                }

            }
        }

        supportedToolbar.setDisplayHomeAsUpEnabled(true);
    }
}