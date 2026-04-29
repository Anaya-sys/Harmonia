/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package harmoniaa;

import java.time.LocalDate;

/**
 *
 * @author CARLOS ANAYA
 */
public class Opinion {
    private int       id;
    private int       idUsuario;
    private int       idProducto;
    private int       calificacion;   // 1 – 5
    private String    comentario;
    private LocalDate fecha;

    public Opinion(int id, int idUsuario, int idProducto,
                   int calificacion, String comentario) {
        this.id           = id;
        this.idUsuario    = idUsuario;
        this.idProducto   = idProducto;
        this.calificacion = calificacion;
        this.comentario   = comentario;
        this.fecha        = LocalDate.now();
    }
  
    public boolean esValida() {
        if (calificacion < 1 || calificacion > 5)          return false;
        if (comentario == null || comentario.trim().isEmpty()) return false;
        if (idUsuario <= 0 || idProducto <= 0)             return false;
        return true;
    }

 
    public int       getId()           { return id; }
    public int       getIdUsuario()    { return idUsuario; }
    public int       getIdProducto()   { return idProducto; }
    public int       getCalificacion() { return calificacion; }
    public String    getComentario()   { return comentario; }
    public LocalDate getFecha()        { return fecha; }

    @Override
    public String toString() {
        return "Opinion[" + id + "] usuario:" + idUsuario
             + " producto:" + idProducto
             + " | " + calificacion + "/5"
             + " | " + fecha
             + " | \"" + comentario + "\"";
    }
}
