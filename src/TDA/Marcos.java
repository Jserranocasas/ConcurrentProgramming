package TDA;

/**
 * @brief     Clase para acceder al número de marcos Físicos 
 *            disponibles de manera estática 
 * @file      Marcos.java
 * @author    Javier Serrano Casas
 * @date      22-03-2018
 */
public class Marcos {
    private static int marcosFisicos; //Marcos Físicos que asigna el gestor
    
    /*================ Constructores de la clase Marcos =================*/
    public Marcos(){
        marcosFisicos = 30;
    }
    
    /*====================== Métodos públicos ===========================*/
    /**
     * @brief  Devuelve la variable de marcos físicos
     * @return  Devuelve la variable de marcos físicos
     */
    public static int getMarcosFisicos(){
        return marcosFisicos;
    }
    
    /**
     * @brief Aumenta el valor de la variable marcos Físicos Disponibles
     * @param marcos int Numero de marcos a aumentar
     */
    public static void sumaMarcosFisicos(int marcos){
        marcosFisicos += marcos;
    }
    
    /**
     * @brief Disminuye el valor de la variable marcos Físicos Disponibles
     * @param marcos int Numero de marcos a disminuir
     */
    public static void restaMarcosFisicos(int marcos){
        marcosFisicos -= marcos;
    }
    
    /*===================================================================*/
}
