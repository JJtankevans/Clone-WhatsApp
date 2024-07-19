package br.com.amazongas.aulawhatsapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import br.com.amazongas.aulawhatsapp.adapters.ContatosAdapter;
import br.com.amazongas.aulawhatsapp.databinding.FragmentContatosBinding;
import br.com.amazongas.aulawhatsapp.model.Usuario;
import br.com.amazongas.aulawhatsapp.utils.Constantes;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContatosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContatosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FragmentContatosBinding binding;
    private ListenerRegistration eventoSnapshot;
    private ContatosAdapter contatosAdapter;

    public ContatosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContatosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContatosFragment newInstance(String param1, String param2) {
        ContatosFragment fragment = new ContatosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContatosBinding.inflate(inflater, container, false);
        contatosAdapter = new ContatosAdapter();

        binding.rvContatos.setAdapter(contatosAdapter);
        binding.rvContatos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContatos.addItemDecoration(new DividerItemDecoration(
                getContext(),
                LinearLayoutManager.VERTICAL
        ));

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Carregar dados com um listener
        adicionarListenerContatos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Quando o fragmente for destruido o listerner também deve ser finalizado
         * para nao ficar fazendo request para o firebase de maneira desnecessária */
        eventoSnapshot.remove();
    }

    private void adicionarListenerContatos() {
        /*Importante ressaltar quando desejo recuperar os dados somente uma vez
         * usar o get no lugar do addSnapshotListener*/
        eventoSnapshot = firebaseFirestore
                .collection(Constantes.COLECAO_USUARIOS)
                .addSnapshotListener((querySnapshot, error) -> {

                    List<Usuario> listaContatos = new ArrayList<Usuario>();
                    //Retorna uma lista de usuarios
                    List<DocumentSnapshot> documentos = querySnapshot.getDocuments();

                    //Adiciona os contatos recuperados em uma lista de de usuarios
                    for (DocumentSnapshot item : documentos) {
                        String idUsuarioLogado = firebaseAuth.getCurrentUser().getUid();
                        //Converto o usuario recuperado da listagem e salvo em um objeto
                        Usuario user = item.toObject(Usuario.class);
                        if (user != null && idUsuarioLogado != null) {
                            if (!idUsuarioLogado.equals(user.getId())) {
                                listaContatos.add(user);
                            }

                        }
                    }

                    //Adicionar os itens ao adapter
                    contatosAdapter.adicionarLista(listaContatos);
                });
    }

}