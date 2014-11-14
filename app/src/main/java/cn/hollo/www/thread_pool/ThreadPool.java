package cn.hollo.www.thread_pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by orson on 14-11-13.
 * 线程池类
 */
public class ThreadPool {
    private static ThreadPool instance;
    private BlockingQueue queue;
    private ThreadPoolExecutor executor;

    private ThreadPool(){
         queue = new LinkedBlockingQueue();
        /**
         * corePoolSize 指的是保留的线程池大小。
         maximumPoolSize 指的是线程池的最大大小。
         keepAliveTime 指的是空闲线程结束的超时时间。
         unit 是一个枚举，表示 keepAliveTime 的单位。
         workQueue 表示存放任务的队列。
         */
         executor = new ThreadPoolExecutor(3, 10, 5, TimeUnit.MINUTES, queue);
    }

    /**
     * 返回线程池的对象
     * @return
     */
    public static ThreadPool getInstance(){
        if (instance == null)
            instance = new ThreadPool();

        return instance;
    }

    /**
     * 关闭线程池
     */
    public void cancel(){
        try{
            if (executor != null)
                executor.shutdownNow();

            executor = null;
            queue.clear();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加新任务
     * @param task
     */
    public void addTask(Runnable task){
        if (task != null)
            executor.execute(task);
    }
}
