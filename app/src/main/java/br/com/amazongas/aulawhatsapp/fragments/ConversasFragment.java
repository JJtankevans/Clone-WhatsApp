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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import br.com.amazongas.aulawhatsapp.R;
import br.com.amazongas.aulawhatsapp.adapters.ConversasAdapter;
import br.com.amazongas.aulawhatsapp.databinding.FragmentConversasBinding;
import br.com.amazongas.aulawhatsapp.model.Conversa;
import br.com.amazongas.aulawhatsapp.utils.Constantes;
import br.com.amazongas.aulawhatsapp.utils.Extensoes;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConversasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ListenerRegistration eventoSnapshot;
    private FragmentConversasBinding binding;
    private ConversasAdapter conversasAdapter;

    public ConversasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConversasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConversasFragment newInstance(String param1, String param2) {
        ConversasFragment fragment = new ConversasFragment();
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
        // Inflate the layout for this fragment
        binding = FragmentConversasBinding.inflate(inflater, container, false);
        conversasAdapter = new ConversasAdapter();

        binding.rvConversas.setAdapter(conversasAdapter);
        binding.rvConversas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvConversas.addItemDecoration(new DividerItemDecoration(
                getContext(),
                LinearLayoutManager.VERTICAL
        ));

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        adicionarListenerConversas();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventoSnapshot.remove();
    }

    private void adicionarListenerConversas() {
        String idUsuarioLogado = firebaseAuth.getCurrentUser().getUid();
        if (idUsuarioLogado != null) {
            eventoSnapshot = firebaseFirestore
                    .collection(Constantes.COLECAO_CONVERSA)
                    .document(idUsuarioLogado)
                    .collection(Constantes.COLECAO_ULTIMAS_CONVERSAS)
                    .orderBy("data", Query.Direction.DESCENDING)
                    .addSnapshotListener((querySnapshot, error) -> {
                        if (error != null) {
                            Extensoes.exibirMensagem("Erro ao carregar conversas", getContext());
                        }

                        List<Conversa> listaConversas = new ArrayList<>();
                        //Retorna uma lista de usuarios
                        List<DocumentSnapshot> documentos = querySnapshot.getDocuments();

                        for (DocumentSnapshot item : documentos) {
                            Conversa conversa = item.toObject(Conversa.class);
                            if (conversa != null) {
                                listaConversas.add(conversa);
                                Log.i("info_data", "conversa: " + conversa.getNome());

                            }
                        }

                        //Atualiza o adapter
                        if (!listaConversas.isEmpty()) {
                            conversasAdapter.adicionarLista(listaConversas);
                        }

                    });
        }
    }

}