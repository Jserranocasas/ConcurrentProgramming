package TDA;

/**
 * @brief   Clase que representa una petición de marcos físicos para  un ciclo
 * @file    Peticion.java
 * @author  Javier Serrano Casas
 * @date    18-03-2018
 */
public class Peticion {
    int idProdeso;      //Identificador del proceso que realiza la petición
    EstadoEjecucion tipoPeticion; //Tipo de Petición que realiza el proceso

    /*============ Constructor de la clase Peticion ===============*/
    public Peticion(int id, EstadoEjecucion tipo){
        this.idProdeso = id;
        this.tipoPeticion = tipo;
    }

    /*  
     * @brief Enumerador del estado de ejecución. Representa el tipo de petición 
     * que se le realizará al gestor de memoria, de tipo INICIO, que solicita al
     * gestor 2 marcos de inicio o de tipo EJECUCION, que solicita al gestor 1 
     * marco adicional hasta un máximo de 4 marcos
     */
    public enum EstadoEjecucion {
        INICIO, EJECUCION
    };
    
    /*===================== Métodos getters =======================*/
    public int getID(){
        return this.idProdeso;
    }
    
    public EstadoEjecucion getTipo(){
        return this.tipoPeticion;
    }
    /*=============================================================*/
}
