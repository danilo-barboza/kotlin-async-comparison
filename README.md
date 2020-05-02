# kotlin-async-comparison
Comparison of coroutines vs callback async programming techniques in kotlin

## Running

Make sure to have at least `jdk13` in order to run the application. Then you can run it with `gradlew`:

```
./gradlew bootRun
```

Then you can access the code with `http://localhost:8080`. It contains the following endpoints:

- Parallel flow
  - [Using CompletableFuture](http://localhost:8080/parallel/completable-future)
  - [Using Project Ractor Mono](http://localhost:8080/parallel/reactor)
  - [Using Coroutines](http://localhost:8080/parallel/coroutines)
 
- Sequential flow
  - [Using CompletableFuture](http://localhost:8080/sequential/completable-future)
  - [Using Project Ractor Mono](http://localhost:8080/sequential/reactor)
  - [Using Coroutines](http://localhost:8080/sequential/coroutines)
  
## The comparison

The main comparison can be seen in the following classes:

- [ParallelComparisonController](src/main/kotlin/org/kotlinlang/example/ParallelComparisonController.kt)
- [SequentialComparisonController](src/main/kotlin/org/kotlinlang/example/SequentialComparisonController.kt)