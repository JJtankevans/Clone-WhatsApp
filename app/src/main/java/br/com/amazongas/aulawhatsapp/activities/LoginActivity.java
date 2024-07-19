package br.com.amazongas.aulawhatsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import br.com.amazongas.aulawhatsapp.databinding.ActivityLoginBinding;
import br.com.amazongas.aulawhatsapp.utils.Extensoes;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String email, senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.txtCadastroLink.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), CadastroActivity.class);
            startActivity(i);
        });

        binding.btnLogin.setOnClickListener(view -> {
            logarUsuario();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificaUsuarioLogado();
    }

    private void logarUsuario() {
        email = binding.editTxtEmail.getText().toString().trim();
        senha = binding.editTxtSenha.getText().toString().trim();
        if (validarCampos(email, senha)) {
            firebaseAuth.signInWithEmailAndPassword(email, senha)
                    .addOnSuccessListener(result -> {
                        Extensoes.exibirMensagem("Logado com sucesso", getApplicationContext());
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    })
                    .addOnFailureListener(e -> {
                        try {
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                //Usuario nao encontrado
                                throw new FirebaseAuthInvalidUserException("ERROR_USER_NOT_FOUND", "E-mail não cadastrado");
                            } else if(e instanceof FirebaseAuthInvalidCredentialsException) {
                                throw new FirebaseAuthInvalidCredentialsException("ERROR_USER_NOT_FOUND", "E-mail ou senha estão incorretos");
                            } else {
                                throw new FirebaseAuthException("ERROR_UNKNOWN", "Erro desconhecido ocorreu.");
                            }
                        } catch (FirebaseAuthException error) {
                            error.printStackTrace();
                            Extensoes.exibirMensagem(error.getMessage(), getApplicationContext());
                        }
                    });
        }
    }

    private void verificaUsuarioLogado() {
        FirebaseUser usuarioAtual = firebaseAuth.getCurrentUser();
        if (usuarioAtual != null) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }

    private boolean validarCampos(String email, String senha) {

        binding.txtEmailLayout.setError(null);
        binding.txtSenhaLayout.setError(null);

        if (email.isEmpty()) {
            binding.txtEmailLayout.setError("O campo e-mail precisa ser preenchido");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEmailLayout.setError("Insira um e-mail válido");
            return false;
        }

        if (senha.isEmpty()) {
            binding.txtSenhaLayout.setError("O campo senha precisa ser preenchido");
            return false;
        }

        if (senha.length() < 6) {
            binding.txtSenhaLayout.setError("Tamanho minimo de 6 caracteres");
            return false;
        }

        return true;
    }
}