# async-with-threadpool

This application demonstrates how to use spring boot's **async** functionality to run multiple calls in parallel and collect the results into a single API response.

Spring boot abstracts away a lot of the details about creating and managing async calls, allowing the user to simply configure a threadpool and annotate a function to run inside a future.
- Main application needs to be annotated with `@EnableAsync`
- A threadpool should be configured by creating an `Executor` as a bean
-     The core pool size is the number of initial threads
-     The max pool size is the maximum number of threads that can run at once
-     Core pool size cannot be larger than max pool size
-     The queue capacity is how many requests will queue up if all threads are busy. Once a thread is free, it will service the next request in the queue
-     The rejected execution handler strategy says how to manage a call if all threads are busy and the queue is full. By default, this strategy is to throw an exception
-     If an executor is not configured, every async call will spawn a new thread, potentially running the JVM out of CPU/memory
- If the code being run asynchronously generates an exception, the call to get/join from the completed future will wrap this exception in a CompletedException instance
- Methods annotated with async must be public, and must be called from outside the class instance.
- One option when waiting on all threads to complete is to pass all the futures to a call to CompletedFuture.allOf() instead of waiting on each individual future's join/get call. However, if any future completes with an exception, this call to CompletedFuture.allOf() will also throw an exception.
- One important thing to note is that the threadpool being configured is separate from the container (jetty, tomcat, etc) threadpool that handles incoming requests.

### Example output:
```
{
  "completedWithErrors": false,
  "totalExecutionTime": 416,
  "results": [
    {
      "workResult": 0,
      "executionTime": 3,
      "executionThreadName": "AsyncDemo-1"
    },
    {
      "workResult": 1,
      "executionTime": 206,
      "executionThreadName": "AsyncDemo-2"
    },
    {
      "workResult": 4,
      "executionTime": 406,
      "executionThreadName": "AsyncDemo-3"
    },
    {
      "workResult": 9,
      "executionTime": 3,
      "executionThreadName": "AsyncDemo-4"
    },
    {
      "workResult": 16,
      "executionTime": 206,
      "executionThreadName": "AsyncDemo-5"
    }
  ]
}
```