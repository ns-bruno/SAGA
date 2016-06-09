package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 06/06/2016.
 */
public class ItemSaidaBeans {

    private int idItemSaida, idSaida, sequencia, idClifoVendedor;
    private String guid, dataAlt, dataVenda, tipoProduto, atacadoVarejo, tipoBaixa, tipoSaida, complemento, baixaPorConferencia;
    private double quantidade, quantidadeDevolvida, quantidadeRetorno, quantidadeConferido, valorTotalLiquido, fatorProdutoPesquisado;
    private UnidadeVendaBeans unidadeVenda;
    private EmbalagemBeans embalagemSaida;
    private EstoqueBeans estoque;
    private boolean tagSelectContext;

    public int getIdItemSaida() {
        return idItemSaida;
    }

    public void setIdItemSaida(int idItemSaida) {
        this.idItemSaida = idItemSaida;
    }

    public int getIdSaida() {
        return idSaida;
    }

    public void setIdSaida(int idSaida) {
        this.idSaida = idSaida;
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public int getIdClifoVendedor() {
        return idClifoVendedor;
    }

    public void setIdClifoVendedor(int idClifoVendedor) {
        this.idClifoVendedor = idClifoVendedor;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(String dataVenda) {
        this.dataVenda = dataVenda;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public String getAtacadoVarejo() {
        return atacadoVarejo;
    }

    public void setAtacadoVarejo(String atacadoVarejo) {
        this.atacadoVarejo = atacadoVarejo;
    }

    public String getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(String tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public String getTipoSaida() {
        return tipoSaida;
    }

    public void setTipoSaida(String tipoSaida) {
        this.tipoSaida = tipoSaida;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBaixaPorConferencia() {
        return baixaPorConferencia;
    }

    public void setBaixaPorConferencia(String baixaPorConferencia) {
        this.baixaPorConferencia = baixaPorConferencia;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getQuantidadeDevolvida() {
        return quantidadeDevolvida;
    }

    public void setQuantidadeDevolvida(double quantidadeDevolvida) {
        this.quantidadeDevolvida = quantidadeDevolvida;
    }

    public double getQuantidadeRetorno() {
        return quantidadeRetorno;
    }

    public void setQuantidadeRetorno(double quantidadeRetorno) {
        this.quantidadeRetorno = quantidadeRetorno;
    }

    public double getQuantidadeConferido() {
        return quantidadeConferido;
    }

    public void setQuantidadeConferido(double quantidadeConferido) {
        this.quantidadeConferido = quantidadeConferido;
    }

    public double getValorTotalLiquido() {
        return valorTotalLiquido;
    }

    public void setValorTotalLiquido(double valorTotalLiquido) {
        this.valorTotalLiquido = valorTotalLiquido;
    }

    public UnidadeVendaBeans getUnidadeVenda() {
        return unidadeVenda;
    }

    public void setUnidadeVenda(UnidadeVendaBeans unidadeVenda) {
        this.unidadeVenda = unidadeVenda;
    }

    public EstoqueBeans getEstoque() {
        return estoque;
    }

    public void setEstoque(EstoqueBeans estoque) {
        this.estoque = estoque;
    }

    public boolean isTagSelectContext() {
        return tagSelectContext;
    }

    public void setTagSelectContext(boolean tagSelectContext) {
        this.tagSelectContext = tagSelectContext;
    }

    public EmbalagemBeans getEmbalagemSaida() {
        return embalagemSaida;
    }

    public void setEmbalagemSaida(EmbalagemBeans embalagemSaida) {
        this.embalagemSaida = embalagemSaida;
    }

    public double getFatorProdutoPesquisado() {
        return fatorProdutoPesquisado;
    }

    public void setFatorProdutoPesquisado(double fatorProdutoPesquisado) {
        this.fatorProdutoPesquisado = fatorProdutoPesquisado;
    }
}
