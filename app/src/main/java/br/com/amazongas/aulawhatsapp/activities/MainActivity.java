package br.com.amazongas.aulawhatsapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import br.com.amazongas.aulawhatsapp.R;
import br.com.amazongas.aulawhatsapp.adapters.ViewPagerAdapter;
import br.com.amazongas.aulawhatsapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        inicializarToolbar();
        inicializarNavegacaoAbas();
    }

    private void inicializarNavegacaoAbas() {
        TabLayout tbLayout = binding.tabLayoutPrincipal;
        ViewPager2 viewPager = binding.viewPagerPrincipal;
        List<String> abas = new ArrayList<>();

        abas.add("CONVERSAS");
        abas.add("CONTATOS");

        /* É necessário criar um adapter para o viewPager
         para saber qual deles está sendo selecionado pelo TabLayout*/
        ViewPagerAdapter adapter = new ViewPagerAdapter(abas, getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        tbLayout.setTabIndicatorFullWidth(true);
        new TabLayoutMediator(tbLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(abas.get(position));
            }
        }).attach();
    }

    private void inicializarToolbar() {
        //Primeiro atribui a toolbar a uma variavel para manipular melhor
        MaterialToolbar toolbar = binding.icTbMain.tbMain;
        //Seta o supportActionBar para ter suporte para versoes mais antigas
        setSupportActionBar(toolbar);

        ActionBar supportedToolbar = getSupportActionBar();

        if (supportedToolbar != null) {
            //Habilita um titulo para a tela
            supportedToolbar.setTitle("WhatsApp");
            //Adicionando menu a toolbar
            addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menuInflater.inflate(R.menu.menu_principal, menu);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    if(menuItem.getItemId() == R.id.itemPerfil) {
                        Intent i = new Intent(getApplicationContext(), PerfilActivity.class);
                        startActivity(i);
                    } else if (menuItem.getItemId() == R.id.itemSair) {
                        deslogarUsuario();
                    }

                    return true;
                }
            });
        }
    }

    private void deslogarUsuario() {
        new AlertDialog.Builder(this)
                .setTitle("Deslogar")
                .setMessage("Deseja realmente sair?")
                .setNegativeButton("Cancelar", (dialog, posicao) -> {
                    dialog.cancel();
                })
                .setPositiveButton("Sim", (dialog, posicao) -> {
                    firebaseAuth.signOut();
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                })
                .create()
                .show();
    }
}