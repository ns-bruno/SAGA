package com.saga.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.saga.R;
import com.saga.banco.interno.funcoesSql.UsuarioSql;
import com.saga.funcoes.FuncoesPersonalizadas;

/**
 * Created by Bruno Nogueira Silva on 15/01/2016.
 */
public class InicioActivity extends AppCompatActivity {

    private Toolbar toolbarInicio;
    private Drawer navegacaoDrawerEsquerdo;
    private AccountHeader cabecalhoDrawer;
    private MaterialListView mListView;
    private int cliqueVoltar = 0;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Recupera o campo para manipular
        toolbarInicio = (Toolbar) findViewById(R.id.activity_inicio_toolbar_cabecalho);
        // Adiciona uma titulo para toolbar
        toolbarInicio.setTitle(this.getResources().getString(R.string.app_name));
        toolbarInicio.setTitleTextColor(getResources().getColor(R.color.branco));
        //toolbarInicio.setLogo(R.drawable.ic_launcher);
        // Seta uma toolBar para esta activiy(tela)
        setSupportActionBar(toolbarInicio);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /**
         * Pega valores passados por parametro de outra Activity
         */
        Bundle intentParametro = getIntent().getExtras();
        // Checa se foi passado algum parametro
        if (intentParametro != null) {
            usuario = intentParametro.getString(LoginActivity.KEY_USUARIO_SERVIDOR);
        } else {
            usuario = getResources().getString(R.string.usuario_desconhecido);
        }

        FuncoesPersonalizadas funcoes = new FuncoesPersonalizadas(InicioActivity.this);

        boolean enviaAutomatico = false;
        boolean recebeAutomatico = false;
        boolean digitaQuantidade = false;

        if (funcoes.getValorXml("EnviarAutomatico").equalsIgnoreCase("S")){
            enviaAutomatico = true;
        }

        if (funcoes.getValorXml("ReceberAutomatico").equalsIgnoreCase("S")){
            recebeAutomatico = true;
        }

        if (funcoes.getValorXml("DigitaQuantidade").equalsIgnoreCase("S")){
            digitaQuantidade = true;

        } else if (funcoes.getValorXml("DigitaQuantidade").equalsIgnoreCase(funcoes.NAO_ENCONTRADO)){
            funcoes.setValorXml("DigitaQuantidade", "N");
        }

        String nomeCompletoUsua = getResources().getString(R.string.usuario_desconhecido);

        if (!usuario.equalsIgnoreCase(getResources().getString(R.string.usuario_desconhecido))) {
            UsuarioSql usuarioSql = new UsuarioSql(InicioActivity.this);
            // Pega os dadosUsuario do usuario no banco de dadosUsuario
            String sql = "SELECT NOME_RAZAO FROM SMAUSUAR \n" +
                    "LEFT OUTER JOIN CFACLIFO CFACLIFO \n" +
                    "ON(SMAUSUAR.ID_CFACLIFO = CFACLIFO.ID_CFACLIFO) WHERE SMAUSUAR.NOME = '" + usuario + "'";

            Cursor dadosUsuario = usuarioSql.sqlSelect(sql);

            // Checa se retornou algum dados do usuario
            if (dadosUsuario != null && dadosUsuario.getCount() > 0){
                // move para o primeiro registro
                dadosUsuario.moveToFirst();
                // Pega o nome completo do usuario
                nomeCompletoUsua = dadosUsuario.getString(dadosUsuario.getColumnIndex("NOME_RAZAO"));
            }
        }



        // Instancia o cabecalho(conta) do drawer
        cabecalhoDrawer = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorAccent)
                .addProfiles(
                        new ProfileDrawerItem().withName(nomeCompletoUsua).withEmail(funcoes.getValorXml("Email")).withIcon(getResources().getDrawable(R.mipmap.ic_account_circle)))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .build();

        //Instancia o drawer
        navegacaoDrawerEsquerdo = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbarInicio)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.romaneio).withIcon(R.mipmap.ic_format_list_bulleted_type_light),
                        new PrimaryDrawerItem().withName(R.string.produtos).withIcon(R.mipmap.ic_action_box_produtct),
                        new PrimaryDrawerItem().withName(R.string.pedido).withIcon(R.mipmap.ic_action_order),
                        new PrimaryDrawerItem().withName(R.string.orcamento).withIcon(R.mipmap.ic_action_order),
                        new PrimaryDrawerItem().withName(R.string.entrada).withIcon(R.mipmap.ic_action_input),
                        new PrimaryDrawerItem().withName(R.string.locacao_produto).withIcon(R.mipmap.ic_map_marker_light),
                        new PrimaryDrawerItem().withName(R.string.locacao_endereco).withIcon(R.mipmap.ic_map_marker_light),
                        new PrimaryDrawerItem().withName(R.string.separar).withIcon(R.mipmap.ic_cart_light),
                        new SectionDrawerItem().withName(R.string.monitoramento),
                        new SwitchDrawerItem().withName(R.string.enviar_automatico).withIcon(R.mipmap.ic_upload_light).withChecked(enviaAutomatico).withOnCheckedChangeListener(mOnCheckedChangeListener).withTag("enviar"),
                        new SwitchDrawerItem().withName(R.string.receber_automatico).withIcon(R.mipmap.ic_download_light).withChecked(recebeAutomatico).withOnCheckedChangeListener(mOnCheckedChangeListener).withTag("receber"),
                        new SwitchDrawerItem().withName(R.string.digitar_quantidade).withIcon(R.mipmap.ic_numeric_light).withChecked(digitaQuantidade).withOnCheckedChangeListener(mOnCheckedChangeListener).withTag("digita_quantidade")

                )
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.LEFT)
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(-1)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(cabecalhoDrawer)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {

                            case 1:
                                // Abre a tela de romaneio
                                Intent intentRomaneio = new Intent(InicioActivity.this, ListaUniversalActivity.class);
                                intentRomaneio.putExtra(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_ROMANEIO);
                                startActivity(intentRomaneio);
                                return true;
                            //break;

                            case 2:
                                // Abre a tela de produtos
                                Intent intentProduto = new Intent(InicioActivity.this, ListaProdutoActivity.class);
                                startActivity(intentProduto);
                                return true;

                            case 3:
                                // Abre a tela de romaneio
                                Intent intentPedido = new Intent(InicioActivity.this, ListaUniversalActivity.class);
                                intentPedido.putExtra(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_PEDIDO);
                                startActivity(intentPedido);
                                return true;

                            case 5:
                                // Abre a tela de notas fiscais de entradas
                                Intent intent = new Intent(InicioActivity.this, ListaUniversalActivity.class);
                                intent.putExtra(ListaUniversalActivity.KEY_TIPO_TELA, ListaUniversalActivity.TELA_NOTA_FISCAL_ENTRADA);
                                startActivity(intent);
                                return true;

                            case 6:
                                // Abre a tela de locacao de produto
                                Intent intentLocacao = new Intent(InicioActivity.this, LocacaoProdutoActivity.class);
                                startActivity(intentLocacao);
                                return true;

                            case 7:
                                // Abre a tela de locacao de produto
                                Intent intentEndereco = new Intent(InicioActivity.this, LocacaoEnderecoActivity.class);
                                startActivity(intentEndereco);
                                return true;

                            default:
                                return false;
                        }
                    }
                })
                .build();


        // Recupera o campo de lista de card da activity(view)
        mListView = (MaterialListView) findViewById(R.id.activity_inicio_material_listview);
        // Pega os cliques dos cards
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(Card card, int position) {
                //String s = card.toString();
            }

            @Override
            public void onItemLongClick(Card card, int position) {
                //String s = card.toString();
            }
        });

        gerarCard();

    } // Fim onCreate

    @Override
    protected void onResume() {
        super.onResume();

        navegacaoDrawerEsquerdo.setSelectionAtPosition(-1);
        navegacaoDrawerEsquerdo.closeDrawer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("SAGA", "onDestroy - InicioActivity");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {

            if (cliqueVoltar < 1){
                // Mostra uma mensagem para clicar novamente em voltar
                //Toast.makeText(InicioActivity.this, getResources().getString(R.string.clique_sair_novamente_para_sair), Toast.LENGTH_LONG).show();

                SuperToast.create(InicioActivity.this, getResources().getString(R.string.clique_sair_novamente_para_sair), SuperToast.Duration.LONG, Style.getStyle(Style.GRAY, SuperToast.Animations.POPUP)).show();

                cliqueVoltar ++;
                // Cria um temporizador para voltar a zero o clique depois que fechar a menssagem
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cliqueVoltar = 0;
                    }
                }, 3700);

            } else if (cliqueVoltar >= 1){
                // Executa o comando sair
                //InicioMDActivity.super.onBackPressed();
                InicioActivity.this.finish();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * Pega os cliques feito no sweep do menu drawer.
     */
    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(IDrawerItem iDrawerItem, CompoundButton compoundButton, boolean b) {

            FuncoesPersonalizadas funcoesP = new FuncoesPersonalizadas(InicioActivity.this);

            // Checa se a opcao selecionada eh para enviar
            if (iDrawerItem.getTag().toString().contains("enviar")){
                // Checa se foi escolhido verdadeiro ou false
                if (b){
                    funcoesP.setValorXml("EnviarAutomatico", "S");
                } else {
                    funcoesP.setValorXml("EnviarAutomatico", "N");
                }
            }
            // Checa se a opcao selecionada eh para receber
            if (iDrawerItem.getTag().toString().contains("receber")){
                // Checa se foi escolhido verdadeiro ou false
                if (b){
                    funcoesP.setValorXml("ReceberAutomatico", "S");
                } else {
                    funcoesP.setValorXml("ReceberAutomatico", "N");
                }
            }
            // Checa se a opcao seleciona eh para mostrar imagem de produto
            if (iDrawerItem.getTag().toString().contains("digita_quantidade")){
                // Checa se foi escolhido verdadeiro ou false
                if (b){
                    funcoesP.setValorXml("DigitaQuantidade", "S");
                } else {
                    funcoesP.setValorXml("DigitaQuantidade", "N");
                }
            }
            // Executa a funcao para criar os alarmes em background
            funcoesP.criarAlarmeEnviarReceberDadosAutomatico(true, true);
        }
    };

    private void gerarCard(){
        /*Card cardCreditoVarejo = new Card.Builder(getApplicationContext())
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_buttons_card)
                .setTitle("Resumo de Credito no Varejo")
                .setDescription(descricaoCard)
                .addAction(R.id.right_text_button, new TextViewAction(getApplicationContext())
                        .setText("Action")
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                String s = card.toString();
                            }

                        }))
                .endConfig()
                .build();

        mListView.getAdapter().add(cardCreditoVarejo);*/
    }
}
