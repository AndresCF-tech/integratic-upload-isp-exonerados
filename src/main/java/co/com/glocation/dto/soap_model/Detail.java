package co.com.glocation.dto.soap_model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Detail {

    @XmlElement(
        name = "Errores",
        namespace = "http://www.analitica.com.co/Esquemas/Errores/"
    )
    private Errores errores;

    // Getters y Setters...
    public Errores getErrores() {
        return errores;
    }

    public void setErrores(Errores errores) {
        this.errores = errores;
    }
}
