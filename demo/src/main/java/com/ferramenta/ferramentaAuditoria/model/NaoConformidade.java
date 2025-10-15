package com.ferramenta.ferramentaAuditoria.model;

public class NaoConformidade {
    private String auditor;
    private String responsavel;
    private String nc;
    private String situacao;

    public NaoConformidade(String auditor, String responsavel, String nc, String situacao) {
        this.auditor = auditor;
        this.responsavel = responsavel;
        this.nc = nc;
        this.situacao = situacao;
    }

    public String getAuditor() { return auditor; }
    public String getResponsavel() { return responsavel; }
    public String getNc() { return nc; }
    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
    public void setNc(String nc) { this.nc = nc; }
}