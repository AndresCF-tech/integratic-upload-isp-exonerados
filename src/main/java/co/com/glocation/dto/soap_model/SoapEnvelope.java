package co.com.glocation.dto.soap_model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
    name = "Envelope",
    namespace = "http://schemas.xmlsoap.org/soap/envelope/"
)
public class SoapEnvelope {

    @XmlElement(
        name = "Body",
        namespace = "http://schemas.xmlsoap.org/soap/envelope/"
    )
    private SoapBody body;

    // Getters y Setters
    public SoapBody getBody() {
        return body;
    }

    public void setBody(SoapBody body) {
        this.body = body;
    }
}
