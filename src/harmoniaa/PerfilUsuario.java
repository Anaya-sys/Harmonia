/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

/**
 *
 * @author CARLOS ANAYA
 */
public class PerfilUsuario {
    private TipoPerfil tipo;
    private String     instrumento;
 
    public PerfilUsuario(TipoPerfil tipo, String instrumento) {
        this.tipo        = tipo;
        this.instrumento = instrumento;
    }
 
    public TipoPerfil getTipo()        { return tipo; }
    public String     getInstrumento() { return instrumento; }
 
    public void setTipo(TipoPerfil tipo)               { this.tipo = tipo; }
    public void setInstrumento(String instrumento)      { this.instrumento = instrumento; }
 
    @Override
    public String toString() {
        return "PerfilUsuario[" + tipo + ", " + instrumento + "]";
    }
}
