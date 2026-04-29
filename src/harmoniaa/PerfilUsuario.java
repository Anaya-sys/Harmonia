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
    private String instrumento;
    private double presupuesto;

    public PerfilUsuario(TipoPerfil tipo, String instrumento, double presupuesto) {
        this.tipo        = tipo;
        this.instrumento = instrumento;
        this.presupuesto = presupuesto;
    }

    public TipoPerfil getTipo()        { return tipo; }
    public String     getInstrumento() { return instrumento; }
    public double     getPresupuesto() { return presupuesto; }

    @Override
    public String toString() {
        return "Perfil[" + tipo + "] instrumento: " + instrumento
             + " | presupuesto: $" + presupuesto;
    }
}
