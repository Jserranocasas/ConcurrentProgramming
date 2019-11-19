package Practica1;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * @brief   Clase que encapsula la tarea de finalizar los procesos
 * @file    FinalizarProcesos.java
 * @author  Javier Serrano Casas
 * @date    22-03-2018
 */
public class FinalizarProcesos implements Runnable {
    private final List<Future<?>> listaTareas;
    private final CountDownLatch esperaFinalizacion;

    /*===================== Constructor de la clase ========================*/
    public FinalizarProcesos(List<Future<?>> listaTareas, CountDownLatch espera){
        this.listaTareas = listaTareas;
        this.esperaFinalizacion = espera;
    }

    @Override
    public void run() {
        System.out.println("Ha iniciado la ejecución la Tarea(FINALIZACION)");
        
        // Recorre la lista de tareas para solicitar su finalización
        listaTareas.forEach((tarea) -> { 
            tarea.cancel(true);
        });
        
        System.out.println("Ha finalizado la ejecución la Tarea(FINALIZACION)");
        // El programa principal puede presentar los resultados de las tareas
        esperaFinalizacion.countDown();
    }
}
