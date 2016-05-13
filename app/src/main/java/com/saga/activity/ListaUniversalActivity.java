package com.saga.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.saga.R;
import com.saga.adapter.ListaUniversalTabulacaoAdapter;

/**
 * Created by Bruno Nogueira Silva on 21/01/2016.
 */
public class ListaUniversalActivity extends AppCompatActivity {

    private Toolbar toolbarCabecalho;
    private int tipoTela = -1;
    private int idEntrada = -1;
    private ContentValues dadosParametros;
    public static final int TELA_ROMANEIO = 0,
                            TELA_NOTA_FISCAL_ENTRADA = 1,
                            TELA_ITEM_NOTA_FISCAL_ENTRADA = 2;
    public static final String KEY_TIPO_TELA = "KEY_TIPO_TELA";
    public static final String KEY_NOME_ABA = "KEY_NOME_ABA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tabulacao_lista_universal);

        recuperaCampo();

        /**
        * Pega valores passados por parametro de outra Activity
        */
        Bundle intentParametro = getIntent().getExtras();
        if (intentParametro != null) {
            dadosParametros = new ContentValues();

            tipoTela = intentParametro.getInt(KEY_TIPO_TELA);

            dadosParametros.put(KEY_TIPO_TELA, tipoTela);
            dadosParametros.put("ID_AEAENTRA", (intentParametro.getInt("ID_AEAENTRA") > 0) ? intentParametro.getInt("ID_AEAENTRA") : -1);
            dadosParametros.put("ID_AEAROMAN", (intentParametro.getInt("ID_AEAROMAN") > 0) ? intentParametro.getInt("ID_AEAROMAN") : -1);

            // Verifica qual eh o tipo da tela
            if (tipoTela == TELA_NOTA_FISCAL_ENTRADA){
                toolbarCabecalho.setTitle(R.string.nota_fiscal_entrada);

            } else if (tipoTela == TELA_ITEM_NOTA_FISCAL_ENTRADA){
                toolbarCabecalho.setTitle(R.string.itens_nota_fiscal_entrada);
                idEntrada = intentParametro.getInt("ID_AEAENTRA");

            } else if (tipoTela == TELA_ROMANEIO){
                toolbarCabecalho.setTitle(R.string.romaneio);
            }
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabulacao_lista_universal_pager);

        ListaUniversalTabulacaoAdapter listaUniversalTabulacaoAdapter = new ListaUniversalTabulacaoAdapter(getSupportFragmentManager(), getApplicationContext(), dadosParametros);

        viewPager.setAdapter(listaUniversalTabulacaoAdapter);

        // Recupera os campos tabs
        SmartTabLayout tabLayout = (SmartTabLayout) findViewById(R.id.fragment_tabulacao_lista_universal_tab_layout);
        // Seta as paginas nas tabs
        tabLayout.setViewPager(viewPager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void recuperaCampo(){
        toolbarCabecalho = (Toolbar) findViewById(R.id.fragment_tabulacao_lista_universal_toolbar_cabecalho);
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarCabecalho);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarCabecalho.setTitleTextColor(getResources().getColor(R.color.branco));
    }

}
