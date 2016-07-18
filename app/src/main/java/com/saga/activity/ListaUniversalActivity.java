package com.saga.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.saga.R;
import com.saga.activity.fragment.ListaUniversalFragment;
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
                            TELA_ITEM_NOTA_FISCAL_ENTRADA = 2,
                            TELA_ITEM_ROMANEIO = 3,
                            TELA_PEDIDO = 4,
                            TELA_ITEM_PEDIDO = 5,
                            TELA_ORCAMENTO = 6,
                            TELA_ITEM_ORCAMENTO = 7;
    public static final String KEY_TIPO_TELA = "KEY_TIPO_TELA";
    public static final String KEY_NOME_ABA = "KEY_NOME_ABA";
    public static final int REQUISICAO_DADOS_PRODUTOS = 100,
                            RETORNO_ITEM_SAIDA_CONFERIDO_OK = 200,
                            RETORNO_ITEM_SAIDA_CONFERIDO_NEG = 201;
    public static final String KEY_RETORNO_FATOR_PESQUISADO = "keyRetornoFatorP",
                               KEY_ID_ITEM_SAIDA = "keyIdItemSaida";

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
            dadosParametros.put("ID_AEASAIDA", (intentParametro.getInt("ID_AEASAIDA") > 0) ? intentParametro.getInt("ID_AEASAIDA") : -1);
            dadosParametros.put("ID_AEAORCAM", (intentParametro.getInt("ID_AEAORCAM") > 0) ? intentParametro.getInt("ID_AEAORCAM") : -1);

            // Verifica qual eh o tipo da tela
            if (tipoTela == TELA_NOTA_FISCAL_ENTRADA){
                toolbarCabecalho.setTitle(R.string.nota_fiscal_entrada);

            } else if (tipoTela == TELA_ITEM_NOTA_FISCAL_ENTRADA){
                toolbarCabecalho.setTitle(R.string.itens_nota_fiscal_entrada);
                idEntrada = intentParametro.getInt("ID_AEAENTRA");

            } else if (tipoTela == TELA_ROMANEIO){
                toolbarCabecalho.setTitle(R.string.romaneio);

            } else if (tipoTela == TELA_ITEM_ROMANEIO){
                toolbarCabecalho.setTitle(R.string.itens_romaneio);

            } else if (tipoTela == TELA_PEDIDO){
                toolbarCabecalho.setTitle(R.string.lista_pedidos);

            } else if (tipoTela == TELA_ORCAMENTO){
                toolbarCabecalho.setTitle(R.string.orcamento);

            } else if (tipoTela == TELA_ITEM_ORCAMENTO){
                toolbarCabecalho.setTitle(R.string.produtos_orcamento);

            }
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabulacao_lista_universal_pager);

        final ListaUniversalTabulacaoAdapter listaUniversalTabulacaoAdapter = new ListaUniversalTabulacaoAdapter(getSupportFragmentManager(), getApplicationContext(), dadosParametros);

        viewPager.setAdapter(listaUniversalTabulacaoAdapter);

        // Recupera os campos tabs
        SmartTabLayout tabLayout = (SmartTabLayout) findViewById(R.id.fragment_tabulacao_lista_universal_tab_layout);
        // Seta as paginas nas tabs
        tabLayout.setViewPager(viewPager);

        // Pega os cliques das abas
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                // checa se eh a segunda aba (confediro)
                if (listaUniversalTabulacaoAdapter.getPageTitle(position).toString().contains("Conferido")){
                    // Pega o fragment para manipular o mesmo
                    ListaUniversalFragment fragmentConferido = (ListaUniversalFragment) listaUniversalTabulacaoAdapter.getRegisteredFragments(position);
                    // Checa se retornou o fragment
                    if (fragmentConferido != null){
                        // Executa uma funcao dentro do fragment
                        fragmentConferido.onLoaderLista();
                    }
                } else if (listaUniversalTabulacaoAdapter.getPageTitle(position).toString().contains("Conferir")){
                    // Pega o fragment para manipular o mesmo
                    ListaUniversalFragment fragmentConferir = (ListaUniversalFragment) listaUniversalTabulacaoAdapter.getRegisteredFragments(position);

                    if (fragmentConferir != null){
                        fragmentConferir.onLoaderLista();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
