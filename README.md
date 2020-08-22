# Concurrent Programming
Concurrent manager that simulates the process of page markings in memory.

The system simulates a main memory with a total of 30 page frames. Processes running in memory have two allocated startup frames.

To do this, it controls mutual exclusion with **Semaphores** and manages the processes through an **ExecutorService**.

```java
Semaphore exmMemoria = new Semaphore(1);   
Semaphore exmPeticiones = new Semaphore(1); 
Semaphore maxPeticiones = new Semaphore(80);
Semaphore nuevaPeticion = new Semaphore(0); 
```

```java
ExecutorService ejecucion = (ExecutorService) Executors.newFixedThreadPool(8);
```

The program uses the [**java.util.concurrent**](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/package-summary.html) library for concurrency.

# Screenshots of the run result

![alt text](https://i.imgur.com/hLVHBsml.png "Run Result")

![alt text](https://i.imgur.com/Sqi6ObEl.png "Run Result")
