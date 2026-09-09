package co.com.glocation.dto.soap_model;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
    name = "CargarArchivo",
    namespace = "http://www.analitica.com.co/AZDigital/xsds/"
)
public class CargarArchivo {

    @XmlAttribute(name = "RadNumero")
    private String radNumero;

    @XmlAttribute(name = "PathRelativoAZ")
    private String pathRelativoAZ;

    @XmlAttribute(name = "IdDirectorio")
    private String idDirectorio;

    @XmlAttribute(name = "Nombre")
    private String nombre;

    @XmlAttribute(name = "TipoMime")
    private String tipoMime;

    @XmlAttribute(name = "Codificacion")
    private String codificacion;

    @XmlElement(name = "Archivo")
    private String archivo;

    public String getRadNumero() {
        return radNumero;
    }

    public String getPathRelativoAZ() {
        return pathRelativoAZ;
    }

    public String getIdDirectorio() {
        return idDirectorio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public String getCodificacion() {
        return codificacion;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setRadNumero(String radNumero) {
        this.radNumero = radNumero;
    }

    public void setPathRelativoAZ(String pathRelativoAZ) {
        this.pathRelativoAZ = pathRelativoAZ;
    }

    public void setIdDirectorio(String idDirectorio) {
        this.idDirectorio = idDirectorio;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public void setCodificacion(String codificacion) {
        this.codificacion = codificacion;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }
}
