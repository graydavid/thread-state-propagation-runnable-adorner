# Thread-State-Propagation-Runnable-Adorner

Thread-State-Propagation-Runnable-Adorner is a simple java library that integrates the `runnable-decorating-executor` project with the `thread-state-propagation` project to define a safe process for propagating thread state while running multi-threaded code using an Executor. (`runnable-decorating-executor` is a simple library that allows the safe decoration of Runnables passed to Executors such that the original Runnable is guaranteed to be run.`thread-state-propagation` provides helpful interfaces for propagating state from one Thread to another.)

## Adding this project to your build

This project follows [semantic versioning](https://semver.org/). It's currently available via the Maven snapshot repository only as version 0.0.1-SNAPSHOT. The "0.0.1" part is because it's still in development, waiting for feedback or sufficient usage until its first official release. The "SNAPSHOT" part is because no one has requested a stable, non-SNAPSHOT development version. If you need a non-SNAPSHOT development version, feel free to reach out, and I can build this project into the Maven central repository.

## Usage

This project requires JDK version 11 or higher.

The below examples show how to use this ThreadStatePropagationRunnableAdorner to define a safe way to propagate thread state while using an Executor.

```java
ThreadStateManager<Integer> stateManager = ...; //How this is done is up to clients
ThreadStatePropagationStarter starter = ThreadStatePropagationStarter
        .simplyGettingAndSettingState(stateManager);
RunnableAdorner adorner = ThreadStatePropagationRunnableAdorner.from(starter);
Executor threadExecutor = ...; //How this is done is up to clients
Executor safePropagatingExecutor = RunnableDecoratingExecutor.from(threadExecutor, adorner);
```

Now, whenever `safePropagatingExecutor` is used to run a command, thread state will be propagated safely from the origin to the destination Thread. (Although this example is a simple starter process, Thread-State-Propagation also provides a way to define composite ThreadStatePropagationStarters based on a list of ThreadStatePropagationStarters (i.e. the composite design pattern) as well as fault-tolerant ThreadStatePropagationStarters (which can suppress failures). Runnable-Decorating-Executor also provides the same for RunnableAdorner, but using Thread-State-Propagation is probably more efficient in this case.)

## Contributions

Contributions are welcome! See the [graydavid-parent](https://github.com/graydavid/graydavid-parent) project for details.