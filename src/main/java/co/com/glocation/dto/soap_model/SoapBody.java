package co.com.glocation.dto.soap_model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SoapBody {

    @XmlElement(
        name = "Fault",
        namespace = "http://schemas.xmlsoap.org/soap/envelope/"
    )
    private SoapFault fault;

    @XmlElement(
        name = "RespuestaOperacion",
        namespace = "http://www.analitica.com.co/AZDigital/xsds/"
    )
    private RespuestaOperacion respuestaOperacion;

    // Getters y Setters
    public SoapFault getFault() {
        return fault;
    }

    public void setFault(SoapFault fault) {
        this.fault = fault;
    }

    public RespuestaOperacion getRespuestaOperacion() {
        return respuestaOperacion;
    }

    public void setRespuestaOperacion(RespuestaOperacion respuestaOperacion) {
        this.respuestaOperacion = respuestaOperacion;
    }
}
