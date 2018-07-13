import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * ###workQueue
 workQueue必须是BlockingQueue阻塞队列。当线程池中的线程数超过它的corePoolSize的时候，线程会进入阻塞队列进行阻塞等待。通过workQueue，线程池实现了阻塞功能
 ####（1）不排队，直接提交
 将任务直接交给线程处理而不保持它们，可使用SynchronousQueue
 如果不存在可用于立即运行任务的线程（即线程池中的线程都在工作），则试图把任务加入缓冲队列将会失败，因此会构造一个新的线程来处理新添加的任务，并将其加入到线程池中（corePoolSize-->maximumPoolSize扩容）
 Executors.newCachedThreadPool()采用的便是这种策略
 ####（2）无界队列
 可以使用LinkedBlockingQueue（基于链表的有界队列，FIFO），理论上是该队列可以对无限多的任务排队
 将导致在所有corePoolSize线程都工作的情况下将新任务加入到队列中。这样，创建的线程就不会超过corePoolSize，也因此，maximumPoolSize的值也就无效了
 ####（3）有界队列
 可以使用ArrayBlockingQueue（基于数组结构的有界队列，FIFO），并指定队列的最大长度
 使用有界队列可以防止资源耗尽，但也会造成超过队列大小和maximumPoolSize后，提交的任务被拒绝的问题，比较难调整和控制。
 *
 */


/**
 * 终止线程池
 *对于关闭线程池主要有两个方法shutdown()和shutdownNow():
 * 详情看文档
 */


public class testExecutors {
    /**
     * newCachedThreadPool
     * 实现 new ThreadPoolExecutor(0,Integer.MAX_VALUE,60L, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
     * 但是最大线程数为Integer.MAX_VALUE，即2147483647，因为创建线程没有上限，一旦高并发场景会导致严重的性能问题（线程上下文切换带来的开销），线程创建占用堆外内存，如果任务对象也不小，它就会使堆外内存和堆内内存其中的一个先耗尽
     * 创建一个可缓存的线程池，调用execute将重用以前构造的线程（如果线程可用）。如果现有线程没有可用的，则创建一个新线   程并添加到池中。终止并从缓存中移除那些已有 60 秒钟未被使用的线程。
     * 一般来说，CachedTheadPool在程序执行过程中通常会创建与所需数量相同的线程，然后在它回收旧线程时停止创建新线程，因此它是合理的Executor的首选，只有当这种方式会引发问题时（比如需要大量长时间面向连接的线程时），才需要考虑用FixedThreadPool。
     */
    public static void newCachePool(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<5;i++){
            executorService.execute(new TestRunnable());
            System.out.println("第" + i );
        }
    }

    /**
     * 创建固定数目线程的线程池
     * new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
     * 其中corePoolSize == maximumPoolSize，使用LinkedBlockingQuene作为阻塞队列，超时时间为0，当线程池没有可执行任务时，也不会释放线程。
     *因为队列LinkedBlockingQueue大小为默认的Integer.MAX_VALUE，可以无限的往里面添加任务，直到内存溢出；。
     * @param count
     */
    public static  void newFixedPool(int count){
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        for (int i=0;i<20;i++){
            executorService.execute(new TestRunnable());
            System.out.println("第"+ i);
        }
        //手动关闭线程池
        executorService.shutdown();
    }

    /**
     * 创建单线程线程池
     * new ThreadPoolExecutor(1, 1,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>())
     * 同newFixedThreadPool线程池一样，队列用的是LinkedBlockingQueue，队列大小为默认的Integer.MAX_VALUE，可以无限的往里面添加任务，直到内存溢出；
     */
    public static void newSingleThread(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for(int i=0;i<20;i++){
            executorService.execute(new TestRunnable());
            System.out.println("第"+ i);
        }
    }

    /**
     * 不建议使用ExecutorService，不要用execute来实现线程池，最好自己用ThreadPoolExecutor来自己实现
     */
    public  static  void main(String[] args){
        //newcachepool()

        newFixedPool(10);

        //newSingleThread();
    }
}


/**
 * runnable类
 */
class TestRunnable implements Runnable{
    public void run(){
        System.out.println(Thread.currentThread().getName() + "线程被调用了");
        //测试线程终止
        try{
            Thread.sleep(5000);
            System.out.println(Thread.currentThread().getName() + "线程被调用了");
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}