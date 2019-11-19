package TDA;

import java.util.Queue;
import Queue.CircularFifoQueue;

/**
 * @brief     Clase plantilla que representa una lista FIFO para cualquier tipo
 * @param <T> T tipo de objeto del que se compondra la lista FIFO
 * @file      Lista.java
 * @author    Javier Serrano Casas
 * @date      18-03-2018
 */
public class Lista<T> {
    private Queue<T> buffer;   //Estructura FIFO
    
    /*================ Constructores de la clase Lista =================*/
    public Lista(){
        buffer = new CircularFifoQueue<>();
    }
    
    public Lista(int limite){
        buffer = new CircularFifoQueue<>(limite);
    }
    
    /*====================== Métodos públicos ===========================*/
    /**
     * @brief Añade un elemento  al buffer FIFO  
     * @param e Elemento a añadir en la estructura 
     */
    public void add(T e){
        buffer.add(e);
    }
    
    /**
     * @brief Devuelve y elimina el primer elemento del buffer
     * @return Devuelve el primer elemento del buffer
     */
    public T remove(){
        return buffer.poll();
    }
    
    /**
     * @brief Busca un elemento en la estructura para saber si se encuentra
     * @param e Elemento a buscar en el buffer
     * @return Devuelve true si se encuentra el elemento y false en caso contrario
     */
    public boolean find(T e){
        return buffer.contains(e);
    }

    /**
     * @brief Devuelve el tamaño del buffer
     * @return Devuelve el numero de elementos en el buffer
     */
    public int size(){
        return buffer.size();
    }
    
    /*===================================================================*/
}
