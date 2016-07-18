package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 14/06/2016.
 */
public class ItemOrcamentoBeans {

    private int idItemOrcamento, idOrcamento, idItemOrcamentoConjunto, sequencia;
    private EstoqueBeans estoqueItemOrcamento;
    private UnidadeVendaBeans unidadeVenda;
    private EmbalagemBeans embalagemOrcamento;
    private String guidItemOrcamento, dataAlt, tipoProduto, complemento;
    private double quantidadeItemOrcamento, quantidadeConferidaItemOrcamento, valorTotalItemOrcamento;
    private boolean tagSelectContext;

    public int getIdItemOrcamento() {
        return idItemOrcamento;
    }

    public void setIdItemOrcamento(int idItemOrcamento) {
        this.idItemOrcamento = idItemOrcamento;
    }

    public int getIdOrcamento() {
        return idOrcamento;
    }

    public void setIdOrcamento(int idOrcamento) {
        this.idOrcamento = idOrcamento;
    }

    public int getIdItemOrcamentoConjunto() {
        return idItemOrcamentoConjunto;
    }

    public void setIdItemOrcamentoConjunto(int idItemOrcamentoConjunto) {
        this.idItemOrcamentoConjunto = idItemOrcamentoConjunto;
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public EstoqueBeans getEstoqueItemOrcamento() {
        return estoqueItemOrcamento;
    }

    public void setEstoqueItemOrcamento(EstoqueBeans estoqueItemOrcamento) {
        this.estoqueItemOrcamento = estoqueItemOrcamento;
    }

    public UnidadeVendaBeans getUnidadeVenda() {
        return unidadeVenda;
    }

    public void setUnidadeVenda(UnidadeVendaBeans unidadeVenda) {
        this.unidadeVenda = unidadeVenda;
    }

    public String getGuidItemOrcamento() {
        return guidItemOrcamento;
    }

    public void setGuidItemOrcamento(String guidItemOrcamento) {
        this.guidItemOrcamento = guidItemOrcamento;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getTipoProduto() {
        return tipoProduto;
    }

    public void setTipoProduto(String tipoProduto) {
        this.tipoProduto = tipoProduto;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public double getQuantidadeItemOrcamento() {
        return quantidadeItemOrcamento;
    }

    public void setQuantidadeItemOrcamento(double quantidadeItemOrcamento) {
        this.quantidadeItemOrcamento = quantidadeItemOrcamento;
    }

    public double getValorTotalItemOrcamento() {
        return valorTotalItemOrcamento;
    }

    public void setValorTotalItemOrcamento(double valorTotalItemOrcamento) {
        this.valorTotalItemOrcamento = valorTotalItemOrcamento;
    }

    public EmbalagemBeans getEmbalagemOrcamento() {
        return embalagemOrcamento;
    }

    public void setEmbalagemOrcamento(EmbalagemBeans embalagemOrcamento) {
        this.embalagemOrcamento = embalagemOrcamento;
    }

    public double getQuantidadeConferidaItemOrcamento() {
        return quantidadeConferidaItemOrcamento;
    }

    public void setQuantidadeConferidaItemOrcamento(double quantidadeConferidaItemOrcamento) {
        this.quantidadeConferidaItemOrcamento = quantidadeConferidaItemOrcamento;
    }

    public boolean isTagSelectContext() {
        return tagSelectContext;
    }

    public void setTagSelectContext(boolean tagSelectContext) {
        this.tagSelectContext = tagSelectContext;
    }
}
