package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 01/06/2016.
 */
public class SaidaBeans {

    private int idSaida, idEmpresa, idClifo, numeroSaida;
    private String guidSaida, dataCad, dataAlt, dataVenda, dataSaida, dataEmissao, dataCancelado,
            atacadoVarejo, situacao, observacao, pessoaCliente, nomeCliente, cpfCgcCliente, ieRgCliente,
            enderecoCliente, bairroCliente, cepCliente, tipoSaida, tipoEntrega;
    private CidadeBeans cidadeSaida;
    private EstadoBeans estadoSaida;
    private SerieBeans serieSaida;
    private double valorTotalSaida;

    public int getIdSaida() {
        return idSaida;
    }

    public void setIdSaida(int idSaida) {
        this.idSaida = idSaida;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdClifo() {
        return idClifo;
    }

    public void setIdClifo(int idClifo) {
        this.idClifo = idClifo;
    }

    public int getNumeroSaida() {
        return numeroSaida;
    }

    public void setNumeroSaida(int numeroSaida) {
        this.numeroSaida = numeroSaida;
    }

    public String getGuidSaida() {
        return guidSaida;
    }

    public void setGuidSaida(String guidSaida) {
        this.guidSaida = guidSaida;
    }

    public String getDataCad() {
        return dataCad;
    }

    public void setDataCad(String dataCad) {
        this.dataCad = dataCad;
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

    public String getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(String dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getDataCancelado() {
        return dataCancelado;
    }

    public void setDataCancelado(String dataCancelado) {
        this.dataCancelado = dataCancelado;
    }

    public String getAtacadoVarejo() {
        return atacadoVarejo;
    }

    public void setAtacadoVarejo(String atacadoVarejo) {
        this.atacadoVarejo = atacadoVarejo;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getPessoaCliente() {
        return pessoaCliente;
    }

    public void setPessoaCliente(String pessoaCliente) {
        this.pessoaCliente = pessoaCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCpfCgcCliente() {
        return cpfCgcCliente;
    }

    public void setCpfCgcCliente(String cpfCgcCliente) {
        this.cpfCgcCliente = cpfCgcCliente;
    }

    public String getIeRgCliente() {
        return ieRgCliente;
    }

    public void setIeRgCliente(String ieRgCliente) {
        this.ieRgCliente = ieRgCliente;
    }

    public String getEnderecoCliente() {
        return enderecoCliente;
    }

    public void setEnderecoCliente(String enderecoCliente) {
        this.enderecoCliente = enderecoCliente;
    }

    public String getBairroCliente() {
        return bairroCliente;
    }

    public void setBairroCliente(String bairroCliente) {
        this.bairroCliente = bairroCliente;
    }

    public String getCepCliente() {
        return cepCliente;
    }

    public void setCepCliente(String cepCliente) {
        this.cepCliente = cepCliente;
    }

    public String getTipoSaida() {
        return tipoSaida;
    }

    public void setTipoSaida(String tipoSaida) {
        this.tipoSaida = tipoSaida;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public CidadeBeans getCidadeSaida() {
        return cidadeSaida;
    }

    public void setCidadeSaida(CidadeBeans cidadeSaida) {
        this.cidadeSaida = cidadeSaida;
    }

    public EstadoBeans getEstadoSaida() {
        return estadoSaida;
    }

    public void setEstadoSaida(EstadoBeans estadoSaida) {
        this.estadoSaida = estadoSaida;
    }

    public double getValorTotalSaida() {
        return valorTotalSaida;
    }

    public void setValorTotalSaida(double valorTotalSaida) {
        this.valorTotalSaida = valorTotalSaida;
    }

    public SerieBeans getSerieSaida() {
        return serieSaida;
    }

    public void setSerieSaida(SerieBeans serieSaida) {
        this.serieSaida = serieSaida;
    }
}
