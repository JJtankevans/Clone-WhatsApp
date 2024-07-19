package br.com.amazongas.aulawhatsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.amazongas.aulawhatsapp.databinding.ActivityCadastroBinding;
import br.com.amazongas.aulawhatsapp.model.Usuario;
import br.com.amazongas.aulawhatsapp.utils.Constantes;
import br.com.amazongas.aulawhatsapp.utils.Extensoes;

public class CadastroActivity extends AppCompatActivity {
    private ActivityCadastroBinding binding;
    private String nome;
    private String email;
    private String senha;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarToolbar();
        inicializarEventosCLique();
    }

    private void inicializarEventosCLique() {
        binding.btnCadastrar.setOnClickListener(view -> {
            if (validarCampos()) {
                //Cadastrar usuário
                cadastrarUsuario(nome, email, senha);
            }
        });
    }

    private void cadastrarUsuario(String nome, String email, String senha) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(result -> {
                    if (result.isSuccessful()) {
                        //Salva os dados do usuário no banco de dados do firebase
                        String idUser = result.getResult().getUser().getUid();
                        if( idUser != null ) {
                            Usuario usuario = new Usuario(idUser, nome, email, "");
                            salvarUsuarioFireStore(usuario);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    try {
//                        throw e;
                        if (e instanceof FirebaseAuthWeakPasswordException) {
                            //Quando a senha é muito fraca
                            throw new FirebaseAuthWeakPasswordException("ERROR_PASSWORD_WEAK", "Senha fraca, digite outra com letras, números e caracteres especiais", "");
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            //Email nao é valido
                            throw new FirebaseAuthInvalidCredentialsException("ERRO_INVALID_CREDENTIAL", "As credenciais são inválidas");
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            //Usuario nao encontrado
                            throw new FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "Usuário não encontrado.");
                        } else if (e instanceof FirebaseAuthUserCollisionException) {
                            //Quando ja possui cadastro com o email usado
                            throw new FirebaseAuthUserCollisionException("ERROR_EMAIL_ALREDY_EXIST", "E-mail utilizado já possui cadastro no sistema");
                        } else {
                            throw new FirebaseAuthException("ERROR_UNKNOWN", "Erro desconhecido ocorreu.");
                        }
                    } catch (FirebaseAuthException erro) {
                        erro.printStackTrace();
                        Extensoes.exibirMensagem(erro.getMessage(), getApplicationContext());
                    }
                });
    }

    private void salvarUsuarioFireStore(Usuario usuario) {
        /*
        * O método addOnSuccessListener é usado quando não se tem nada de retorno
        * O método addOnCompleteListener é usado quando se precisa de algum dado de retorno*/

        firebaseFirestore.collection(Constantes.COLECAO_USUARIOS)
                .document(usuario.getId())
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Extensoes.exibirMensagem("Sucesso ao fazer seu cadastro", getApplicationContext());
                        //Redireciona para outra tela
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Extensoes.exibirMensagem("Erro ao fazer seu cadastro", getApplicationContext());
                    }
                });
    }


    private boolean validarCampos() {
        nome = binding.txtEditNome.getText().toString().trim();
        email = binding.txtEditEmail.getText().toString().trim();
        senha = binding.txtEditSenha.getText().toString().trim();

        binding.txtIpLtNome.setError(null);
        binding.txtIpLtEmail.setError(null);
        binding.txtIpLtSenha.setError(null);

        if (nome.isEmpty()) {
            binding.txtIpLtNome.setError("O campo nome precisa ser preenchido");
            return false;
        }

        if (email.isEmpty()) {
            binding.txtIpLtEmail.setError("O campo e-mail precisa ser preenchido");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtIpLtEmail.setError("Insira um e-mail válido");
            return false;
        }

        if (senha.isEmpty()) {
            binding.txtIpLtSenha.setError("O campo senha precisa ser preenchido");
            return false;
        }

        if (senha.length() < 6) {
            binding.txtIpLtSenha.setError("Tamanho minimo de 6 caracteres");
            return false;
        }

        return true;
    }

    private void inicializarToolbar() {
        //Primeiro atribui a toolbar a uma variavel para manipular melhor
        MaterialToolbar toolbar = binding.icToolbar.tbMain;
        //Seta o supportActionBar para ter suporte para versoes mais antigas
        setSupportActionBar(toolbar);

        ActionBar supportedToolbar = getSupportActionBar();

        if (supportedToolbar != null) {
            //Habilita um titulo para a tela
            supportedToolbar.setTitle("Faça o seu cadastro");
            /* Habilita a setinha para voltar para a tela anterior
             * Só funciona se tiver habilitado no ANdroid Manifest um parentActivityName
             */
            supportedToolbar.setDisplayHomeAsUpEnabled(true);


        }
    }
}