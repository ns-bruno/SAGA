package com.saga.beans;

/**
 * Created by Bruno Nogueira Silva on 13/06/2016.
 */
public class OrcamentoBeans {

    private int idOrcamento, idEmpresa, idClifoCliente, idRomaneio, numeroOrcamento;
    private String dataAlt, guidOrcamento, dataOrcamento, dataValidade, dataEmissao, atacadoVarejo, pessoaCliente,
                    nomeCliente, ieRgCliente, cpfCgcCliente, enderecoCliente, bairroCliente, cepCliente,
                    foneCliente, obsOrcamento, tipoBaixaOrcamento, tipoEntregaOrcamento;
    private double valorTotalOrcamento;
    private SerieBeans serieOrcamento;
    private EstadoBeans estadoOrcamento;
    private CidadeBeans cidadeOrcamento;

    public int getIdOrcamento() {
        return idOrcamento;
    }

    public void setIdOrcamento(int idOrcamento) {
        this.idOrcamento = idOrcamento;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getGuidOrcamento() {
        return guidOrcamento;
    }

    public void setGuidOrcamento(String guidOrcamento) {
        this.guidOrcamento = guidOrcamento;
    }

    public int getIdClifoCliente() {
        return idClifoCliente;
    }

    public void setIdClifoCliente(int idClifoCliente) {
        this.idClifoCliente = idClifoCliente;
    }

    public int getIdRomaneio() {
        return idRomaneio;
    }

    public void setIdRomaneio(int idRomaneio) {
        this.idRomaneio = idRomaneio;
    }

    public int getNumeroOrcamento() {
        return numeroOrcamento;
    }

    public void setNumeroOrcamento(int numeroOrcamento) {
        this.numeroOrcamento = numeroOrcamento;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public String getDataOrcamento() {
        return dataOrcamento;
    }

    public void setDataOrcamento(String dataOrcamento) {
        this.dataOrcamento = dataOrcamento;
    }

    public String getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(String dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getAtacadoVarejo() {
        return atacadoVarejo;
    }

    public void setAtacadoVarejo(String atacadoVarejo) {
        this.atacadoVarejo = atacadoVarejo;
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

    public String getIeRgCliente() {
        return ieRgCliente;
    }

    public void setIeRgCliente(String ieRgCliente) {
        this.ieRgCliente = ieRgCliente;
    }

    public String getCpfCgcCliente() {
        return cpfCgcCliente;
    }

    public void setCpfCgcCliente(String cpfCgcCliente) {
        this.cpfCgcCliente = cpfCgcCliente;
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

    public String getFoneCliente() {
        return foneCliente;
    }

    public void setFoneCliente(String foneCliente) {
        this.foneCliente = foneCliente;
    }

    public String getObsOrcamento() {
        return obsOrcamento;
    }

    public void setObsOrcamento(String obsOrcamento) {
        this.obsOrcamento = obsOrcamento;
    }

    public String getTipoBaixaOrcamento() {
        return tipoBaixaOrcamento;
    }

    public void setTipoBaixaOrcamento(String tipoBaixaOrcamento) {
        this.tipoBaixaOrcamento = tipoBaixaOrcamento;
    }

    public String getTipoEntregaOrcamento() {
        return tipoEntregaOrcamento;
    }

    public void setTipoEntregaOrcamento(String tipoEntregaOrcamento) {
        this.tipoEntregaOrcamento = tipoEntregaOrcamento;
    }

    public double getValorTotalOrcamento() {
        return valorTotalOrcamento;
    }

    public void setValorTotalOrcamento(double valorTotalOrcamento) {
        this.valorTotalOrcamento = valorTotalOrcamento;
    }

    public SerieBeans getSerieOrcamento() {
        return serieOrcamento;
    }

    public void setSerieOrcamento(SerieBeans serieOrcamento) {
        this.serieOrcamento = serieOrcamento;
    }

    public EstadoBeans getEstadoOrcamento() {
        return estadoOrcamento;
    }

    public void setEstadoOrcamento(EstadoBeans estadoOrcamento) {
        this.estadoOrcamento = estadoOrcamento;
    }

    public CidadeBeans getCidadeOrcamento() {
        return cidadeOrcamento;
    }

    public void setCidadeOrcamento(CidadeBeans cidadeOrcamento) {
        this.cidadeOrcamento = cidadeOrcamento;
    }
}
