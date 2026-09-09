package co.com.glocation.dto.soap_model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
    name = "RespuestaOperacion",
    namespace = "http://www.analitica.com.co/AZDigital/xsds/"
)
public class RespuestaOperacion {

    @XmlAttribute(name = "Estado")
    private String estado;

    @XmlAttribute(name = "NuevoArId")
    private String nuevoArId;

    @XmlAttribute(name = "DirectorioPadreDiId")
    private String directorioPadreDiId;

    // Getters y Setters
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNuevoArId() {
        return nuevoArId;
    }

    public void setNuevoArId(String nuevoArId) {
        this.nuevoArId = nuevoArId;
    }

    public String getDirectorioPadreDiId() {
        return directorioPadreDiId;
    }

    public void setDirectorioPadreDiId(String directorioPadreDiId) {
        this.directorioPadreDiId = directorioPadreDiId;
    }
}
