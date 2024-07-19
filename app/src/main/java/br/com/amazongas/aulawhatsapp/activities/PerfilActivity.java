package br.com.amazongas.aulawhatsapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.amazongas.aulawhatsapp.databinding.ActivityPerfilBinding;
import br.com.amazongas.aulawhatsapp.utils.Constantes;
import br.com.amazongas.aulawhatsapp.utils.Extensoes;

public class PerfilActivity extends AppCompatActivity {
    private ActivityPerfilBinding binding;
    private boolean temPermissaoCamera = false;
    private boolean temPermissaoGaleria = false;
    private Uri selectedImageUri;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String userId = firebaseAuth.getCurrentUser().getUid();
    private String nome, imgUrl;

    private final ActivityResultLauncher<String> gerenciadorGaleria = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri o) {
                    if (o != null) {
                        binding.imgPerfil.setImageURI(o);
                        selectedImageUri = o;
//                        uploadImageStorage(o);
                    } else {
                        Extensoes.exibirMensagem("Nenhuma imagem selecionada", getApplicationContext());
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarToolbar();
        solicitarPermissoes();
        inicializarEventosClique();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosUsuario();
    }

    private void recuperarDadosUsuario() {
        if(userId != null) {
            firebaseFirestore
                    .collection(Constantes.COLECAO_USUARIOS)
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Map<String, Object> dadosUsuario = documentSnapshot.getData();
                        if(dadosUsuario != null) {
                            nome = (String) dadosUsuario.get("nome");
                            imgUrl = (String) dadosUsuario.get("foto");

                            binding.txtEditPerilNome.setText(nome);

                            if(!imgUrl.isEmpty()) {
                                Picasso.get()
                                        .load(imgUrl)
                                        .into(binding.imgPerfil);
                            }
                        }
                    });
        }
    }

    private void inicializarEventosClique() {
        binding.fAcBtnAdicionarImagem.setOnClickListener(view -> {
            if (temPermissaoGaleria) {
                //Assim permite que carregue qualquer imagem da galeria
                gerenciadorGaleria.launch("image/*");
            } else {
                Extensoes.exibirMensagem("Não tem permissão para acessar a galeria", getApplicationContext());
                solicitarPermissoes();
            }
        });


        binding.btnAtualizar.setOnClickListener(view -> {
            if(userId != null) { uploadImageStorage(selectedImageUri); }
        });
    }

    private void atualizarDadosPerfil(String userId, Map<String, Object> dados) {
        firebaseFirestore
                .collection(Constantes.COLECAO_USUARIOS)
                .document(userId)
                .update(dados)
                .addOnSuccessListener(v -> {
                    Extensoes.exibirMensagem("Sucesso ao atualizar perfil", getApplicationContext());
                })
                .addOnFailureListener(erro -> {
                    Extensoes.exibirMensagem("Erro ao atualizar perfil do usuário", getApplicationContext());
                });
    }

    private void uploadImageStorage(Uri uri) {
        //fotos -> usuarios -> id_usuario
        //Faz primeiro o upload da imagem para o storage do firebase
//        String nome = binding.txtEditPerilNome.getText().toString().trim();

        if(!nome.isEmpty() && uri == null) {
            //atualiza somente o nome
            Log.i("info_data", "Entra no primeiro if");
            nome = binding.txtEditPerilNome.getText().toString().trim();
            Map<String,Object> dados = new HashMap<>();
            dados.put("nome", nome.toString());
            atualizarDadosPerfil(userId, dados);
        } else if(nome.isEmpty() && uri != null) {
            //atualiza só a imagem
            Log.i("info_data", "Entra no segundo if");
            storage
                    .getReference("fotos")
                    .child("usuarios")
                    .child(userId)
                    .child("perfil.jpg")
                    .putFile(uri)
                    .addOnSuccessListener(task -> {
                        task.getMetadata()
                                .getReference()
                                .getDownloadUrl()
                                .addOnSuccessListener(url -> {
                                    //Se tiver sucesso ao subir a imagem para o storage atualiza o os dados no Firestore Database

                                    Map<String,Object> dados = new HashMap<>();
                                    dados.put("foto", url.toString());
                                    atualizarDadosPerfil(userId, dados);
                                })
                                .addOnFailureListener(e -> {
                                    Extensoes.exibirMensagem("Erro ao atualizar perfil do usuário", getApplicationContext());
                                });

                    })
                    .addOnFailureListener(error -> {
                        Extensoes.exibirMensagem("Erro ao fazer upload da imagem", getApplicationContext());
                    });
        } else if(!nome.isEmpty() && uri != null) {
            //atualiza imagem e nome
            Log.i("info_data", "Entra no terceiro if");
            storage
                    .getReference("fotos")
                    .child("usuarios")
                    .child(userId)
                    .child("perfil.jpg")
                    .putFile(uri)
                    .addOnSuccessListener(task -> {
                        task.getMetadata()
                                .getReference()
                                .getDownloadUrl()
                                .addOnSuccessListener(url -> {
                                    //Se tiver sucesso ao subir a imagem para o storage atualiza o os dados no Firestore Database

                                        Map<String,Object> dados = new HashMap<>();
                                        dados.put("nome", nome.toString());
                                        dados.put("foto", url.toString());
                                        atualizarDadosPerfil(userId, dados);
                                })
                                .addOnFailureListener(e -> {
                                    Extensoes.exibirMensagem("Erro ao atualizar perfil do usuário", getApplicationContext());
                                });

                    })
                    .addOnFailureListener(error -> {
                        Extensoes.exibirMensagem("Erro ao fazer upload da imagem", getApplicationContext());
                    });
        } else {
            Extensoes.exibirMensagem("Preencha o nome e imagem para atualizar", getApplicationContext());
        }
    }

    private void solicitarPermissoes() {
        //Verifica se o usuário já tem permissão
        temPermissaoCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        temPermissaoGaleria = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;

        List<String> permissoesNegadas = new ArrayList<>();
        if (!temPermissaoCamera) {
            permissoesNegadas.add( Manifest.permission.CAMERA );
        }
        if(!temPermissaoGaleria) {
            permissoesNegadas.add( Manifest.permission.READ_MEDIA_IMAGES );
        }

        if(!permissoesNegadas.isEmpty()) {
            //Solicitar multiplas permissões
            ActivityResultLauncher<String[]> gerenciadosPermissoes = registerForActivityResult(
                    new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> permissionGranted) {
                            temPermissaoCamera = Boolean.TRUE.equals(permissionGranted.get(Manifest.permission.CAMERA));
                            temPermissaoGaleria = Boolean.TRUE.equals(permissionGranted.get(Manifest.permission.READ_MEDIA_IMAGES));
                        }
                    });

            gerenciadosPermissoes.launch(permissoesNegadas.toArray(new String[0]));
        }
    }


    private void inicializarToolbar() {
        //Primeiro atribui a toolbar a uma variavel para manipular melhor
        MaterialToolbar toolbar = binding.icTbPerfil.tbMain;
        //Seta o supportActionBar para ter suporte para versoes mais antigas
        setSupportActionBar(toolbar);

        ActionBar supportedToolbar = getSupportActionBar();

        if (supportedToolbar != null) {
            //Habilita um titulo para a tela
            supportedToolbar.setTitle("Perfil");
            /* Habilita a setinha para voltar para a tela anterior
             * Só funciona se tiver habilitado no ANdroid Manifest um parentActivityName
             */
            supportedToolbar.setDisplayHomeAsUpEnabled(true);


        }
    }
}