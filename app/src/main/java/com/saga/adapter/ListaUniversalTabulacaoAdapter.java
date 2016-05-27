package com.saga.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.saga.R;
import com.saga.activity.ListaUniversalActivity;
import com.saga.activity.fragment.ListaUniversalFragment;

/**
 * Created by Bruno Nogueira Silva on 21/01/2016.
 */
public class ListaUniversalTabulacaoAdapter extends FragmentStatePagerAdapter {

    private ContentValues dadosParamentros;
    private Context context;
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public ListaUniversalTabulacaoAdapter(FragmentManager fm, Context context, ContentValues dadosParamentros) {
        super(fm);
        this.dadosParamentros = dadosParamentros;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        fragment = new ListaUniversalFragment();
        // Cria uma vareavel para salvar os paramentros
        Bundle argumentos = new Bundle();
        argumentos.putInt(ListaUniversalActivity.KEY_TIPO_TELA, dadosParamentros.getAsInteger(ListaUniversalActivity.KEY_TIPO_TELA));
        argumentos.putString(ListaUniversalActivity.KEY_NOME_ABA, context.getResources().getStringArray(R.array.tab_nota_fiscal_entrada)[position]);
        argumentos.putInt("ID_AEAENTRA", dadosParamentros.getAsInteger("ID_AEAENTRA"));

        // Coloca o argumento dentro do fragment
        fragment.setArguments(argumentos);

        registeredFragments.put(position, fragment);

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String[] titulos = new String[0];

        if (dadosParamentros.getAsInteger(ListaUniversalActivity.KEY_TIPO_TELA) == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {
            titulos = context.getResources().getStringArray(R.array.tab_nota_fiscal_entrada);

        } else if (dadosParamentros.getAsInteger(ListaUniversalActivity.KEY_TIPO_TELA) == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {
            titulos = context.getResources().getStringArray(R.array.tab_item_nota_fiscal_entrada);
        }

        return titulos[position];
    }

    @Override
    public int getCount() {

        if (dadosParamentros.getAsInteger(ListaUniversalActivity.KEY_TIPO_TELA) == ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA) {
            // retorna o numero de abas a ser criado
            return context.getResources().getStringArray(R.array.tab_nota_fiscal_entrada).length;

        } else if (dadosParamentros.getAsInteger(ListaUniversalActivity.KEY_TIPO_TELA) == ListaUniversalActivity.TELA_ITEM_NOTA_FISCAL_ENTRADA) {
            // retorna o numero de abas a ser criado
            return context.getResources().getStringArray(R.array.tab_item_nota_fiscal_entrada).length;

        } else {
            return 0;
        }
    }

    public Fragment getRegisteredFragments(int position) {
        return registeredFragments.get(position);
    }
}
