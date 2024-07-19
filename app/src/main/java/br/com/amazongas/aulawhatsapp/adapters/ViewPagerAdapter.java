package br.com.amazongas.aulawhatsapp.adapters;

import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import br.com.amazongas.aulawhatsapp.fragments.ContatosFragment;
import br.com.amazongas.aulawhatsapp.fragments.ConversasFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private FragmentManager fragmentManager;
    private Lifecycle lifecycle;
    private List<String> listaAbas;
    public ViewPagerAdapter(List<String> mAbas, FragmentManager MFragmentManager, Lifecycle MLifecycle) {
        super(MFragmentManager, MLifecycle);
        listaAbas = mAbas;
        fragmentManager = MFragmentManager;
        lifecycle = MLifecycle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String selectedFragment = listaAbas.get(position);
        switch (position) {
            case 1 :
                return new ContatosFragment();
        }
        return new ConversasFragment();
    }

    @Override
    public int getItemCount() {
        return listaAbas.size();
    }
}
