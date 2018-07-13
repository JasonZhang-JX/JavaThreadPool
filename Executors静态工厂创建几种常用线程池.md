#Executor框架简介

 在Java 5之后，并发编程引入了一堆新的启动、调度和管理线程的API。Executor框架便是Java 5中引入的，其内部使用了线程池机制，
 它在java.util.cocurrent 包下，通过该框架来控制线程的启动、执行和关闭，可以简化并发编程的操作。因此，在Java 5之后，通过Executor来启动线程比使用Thread的start方法更好，
 除了更易管理，效率更好（用线程池实现，节约开销）外，还有关键的一点：有助于避免this逃逸问题——如果我们在构造器中启动一个线程，
 因为另一个任务可能会在构造器结束之前开始执行，此时可能会访问到初始化了一半的对象用Executor在构造器中。
 
 ###Executors提供了一系列工厂方法用于创先线程池，返回的线程池都实现了ExecutorService接口。   
 
     1. public static ExecutorService newFixedThreadPool(int nThreads)
 
     创建固定数目线程的线程池。
 
     2. public static ExecutorService newCachedThreadPool()
 
     创建一个可缓存的线程池，调用execute将重用以前构造的线程（如果线程可用）。如果现有线程没有可用的，则创建一个新线   程并添加到池中。终止并从缓存中移除那些已有 60 秒钟未被使用的线程。
 
     3. public static ExecutorService newSingleThreadExecutor()
 
     创建一个单线程化的Executor。
 
     4. public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize)
 
     创建一个支持定时及周期性的任务执行的线程池，多数情况下可用来替代Timer类。
     
### 一般来说，CachedTheadPool在程序执行过程中通常会创建与所需数量相同的线程，然后在它回收旧线程时停止创建新线程，因此它是合理的Executor的首选，只有当这种方式会引发问题时（比如需要大量长时间面向连接的线程时），才需要考虑用FixedThreadPool。