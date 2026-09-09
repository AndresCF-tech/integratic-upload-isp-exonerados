package co.com.glocation.dto;

public class NewFile {

    private String RadNumero;
    private String PathRelativoAZ;
    private String IdDirectorio;
    private String Nombre;
    private String TipoMime;
    private String Codificacion;
    private String Base64;

    public NewFile(
        String radNumero,
        String pathRelativoAZ,
        String idDirectorio,
        String nombre,
        String tipoMime,
        String codificacion,
        String base64
    ) {
        RadNumero = radNumero;
        PathRelativoAZ = pathRelativoAZ;
        IdDirectorio = idDirectorio;
        Nombre = nombre;
        TipoMime = tipoMime;
        Codificacion = codificacion;
        Base64 = base64;
    }

    public String getRadNumero() {
        return RadNumero;
    }

    public String getPathRelativoAZ() {
        return PathRelativoAZ;
    }

    public String getIdDirectorio() {
        return IdDirectorio;
    }

    public String getNombre() {
        return Nombre;
    }

    public String getTipoMime() {
        return TipoMime;
    }

    public String getCodificacion() {
        return Codificacion;
    }

    public String getBase64() {
        return Base64;
    }

    public void setRadNumero(String radNumero) {
        RadNumero = radNumero;
    }

    public void setPathRelativoAZ(String pathRelativoAZ) {
        PathRelativoAZ = pathRelativoAZ;
    }

    public void setIdDirectorio(String idDirectorio) {
        IdDirectorio = idDirectorio;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public void setTipoMime(String tipoMime) {
        TipoMime = tipoMime;
    }

    public void setCodificacion(String codificacion) {
        Codificacion = codificacion;
    }

    public void setBase64(String base64) {
        this.Base64 = base64;
    }

    @Override
    public String toString() {
        return (
            "NewFile{" +
            "PathRelativoAZ='" +
            PathRelativoAZ +
            '\'' +
            ", IdDirectorio='" +
            IdDirectorio +
            '\'' +
            ", Nombre='" +
            Nombre +
            '\'' +
            ", TipoMime='" +
            TipoMime +
            '\'' +
            ", Codificacion='" +
            Codificacion +
            '\'' +
            ", Base64='" +
            Base64 +
            '\'' +
            '}'
        );
    }
}
