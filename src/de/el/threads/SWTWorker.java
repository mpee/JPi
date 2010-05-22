package de.el.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jonn
 */
public abstract class SWTWorker<T, V> implements RunnableFuture<T> {

    private static final int MAX_WORKER_THREADS = 10;
    private volatile int progress;
    private volatile WorkerState state;
    private FutureTask<T> future;
//    private static ExecutorService executor; // = Executors.newCachedThreadPool();;
    /** the progress listeners */
    private List<ProgressListener> progressListeners;
    /** the status listeners */
    private List<StatusListener> statusListeners;
    private static Display display;
    private AccumulativeRunnable<V> doProcess;
    private AccumulativeRunnable<Runnable> doSubmit;
    private AccumulativeRunnable<Integer> doNotifyProgressChange;
    private static Logger log = LoggerFactory.getLogger(SWTWorker.class);

    public SWTWorker(Display d) {
        display = d;
        progressListeners = new ArrayList<ProgressListener>();
        statusListeners = new ArrayList<StatusListener>();
        doSubmit = new DoSubmitAccumulativeRunnable();

        Callable<T> callable = new Callable<T>() {

            public T call() throws Exception {
//                log.debug("in call");
                return doInBackground();
            }
        };

        future = new FutureTask<T>(callable) {

            @Override
            protected void done() {
//                log.debug("future done");
                doneEDT();
                setState(WorkerState.FINISHED);
//                log.debug("future done done");
            }
        };
		setState(WorkerState.RUNNABLE);
    }

//    public SWTWorker() {
//        this(null);
//    }


    /**
     * Invokes {@code done} on the EDT.
     */
    private void doneEDT() {
//        log.debug("doneEDT");
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                done();
            }
        });
    }

    protected void setState(WorkerState workerState) {
        this.state = workerState;
        fireStatusChanged();
    }

    protected void process(List<V> chunks) {
    }

    protected final void publish(V... chunks) {
        synchronized (this) {
            if (doProcess == null) {
                doProcess = new AccumulativeRunnable<V>() {

                    @Override
                    protected void run(final List<V> args) {
//                        log.debug("in publish run");
                         Display.getDefault().asyncExec(new Runnable() {

                            public void run() {
                                process(args);
                            }
                        });
                    }

                    @Override
                    protected void submit() {
//                        log.debug("in publish submit");
                        doSubmit.add(this);
                    }
                };
            }
        }
        doProcess.add(chunks);
    }

    protected void done() {
    }

    protected abstract T doInBackground() throws Exception;

    public final T get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    public final void execute() {
		setState(WorkerState.RUNNING);
        getWorkersExecutorService().execute(this);
//        executor.execute(this);
    }

    private static synchronized ExecutorService getWorkersExecutorService() {
//        final AppContext appContext = AppContext.getAppContext();
        ExecutorService executorService;
//            (ExecutorService) appContext.get(SwingWorker.class);
//        if (executorService == null) {
        //this creates daemon threads.
        ThreadFactory threadFactory =
                new ThreadFactory() {

                    final ThreadFactory defaultFactory =
                            Executors.defaultThreadFactory();

                    public Thread newThread(final Runnable r) {
                        Thread thread =
                                defaultFactory.newThread(r);
                        thread.setName("SWTWorker-"
                                + thread.getName());
                        thread.setDaemon(true);
                        return thread;
                    }
                };

        executorService =
                new ThreadPoolExecutor(1, MAX_WORKER_THREADS,
                10L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(),
                threadFactory);

//            appContext.put(SwingWorker.class, executorService);

        // Don't use ShutdownHook here as it's not enough. We should track
        // AppContext disposal instead of JVM shutdown, see 6799345 for details
//            final ExecutorService es = executorService;

//            appContext.addPropertyChangeListener(AppContext.DISPOSED_PROPERTY_NAME,
//                new PropertyChangeListener() {
//                    @Override
//                    public void propertyChange(PropertyChangeEvent pce) {
//                        boolean disposed = (Boolean)pce.getNewValue();
//                        if (disposed) {
//                            final WeakReference<ExecutorService> executorServiceRef =
//                                new WeakReference<ExecutorService>(es);
//                            final ExecutorService executorService =
//                                executorServiceRef.get();
//                            if (executorService != null) {
//                                AccessController.doPrivileged(
//                                    new PrivilegedAction<Void>() {
//                                        public Void run() {
//                                            executorService.shutdown();
//                                            return null;
//                                        }
//                                    }
//                                );
//                            }
//                        }
//                    }
//                }
//            );
//        }
        return executorService;
    }

    /**
     * Adds the given StatusListener to the list of listeners which will
     * be notified when this XThread's progress has changed.
     * @param pl - the progress listener
     */
    public void addProgressListener(ProgressListener pl) {
        if (progressListeners.contains(pl)) {
            return;
        }
        this.progressListeners.add(pl);
    }

    /**
     * Adds the given ProgressListener to the list of listeners which will
     * be notified when this XThreads state has changed.
     * @param sl - the status listener
     */
    public void addStatusListener(StatusListener sl) {
        if (statusListeners.contains(sl)) {
            return;
        }
        this.statusListeners.add(sl);
    }

    /**
     * Gets the current progress value.
     * @return progress
     */
    public int getProgress() {
        return progress;
    }

    protected final void setProgress(int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("the value should be from 0 to 100");
        }
        if (this.progress == progress) {
            return;
        }
        int oldProgress = this.progress;
        this.progress = progress;
//        if (! getPropertyChangeSupport().hasListeners("progress")) {
//            return;
//        }
        synchronized (this) {
            if (doNotifyProgressChange == null) {
//                log.debug("new notify");
                doNotifyProgressChange =
                        new AccumulativeRunnable<Integer>() {

                            @Override
                            public void run(List<Integer> args) {
//                                firePropertyChange("progress",
//                                        args.get(0),
//                                        args.get(args.size() - 1));
//                                log.debug("fire progress changed");
                                fireProgressChanged();

                            }

                            @Override
                            protected void submit() {
                                doSubmit.add(this);
                            }
                        };
            }
        }
        doNotifyProgressChange.add(oldProgress, progress);
    }

    private void fireProgressChanged() {
        ProgressChangedEvent e = new ProgressChangedEvent(progress);
        for (ProgressListener p : progressListeners) {
            p.progressChanged(e);
        }
    }

    private void fireStatusChanged() {
        StatusChangedEvent e = new StatusChangedEvent(state);
        for (StatusListener l : statusListeners) {
            l.statusChanged(e);
        }
    }

    // Future methods START
    /**
     * {@inheritDoc}
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isCancelled() {
        return future.isCancelled();
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isDone() {
        return future.isDone();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Please refer to {@link #get} for more details.
     */
    public final T get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }

    /**
     * Sets this {@code Future} to the result of computation unless
     * it has been cancelled.
     */
    public final void run() {
        future.run();
    }

    /**
     * The states a XThread can adopt.
     */
    public enum WorkerState {

        /** indicates that this thread has been initialized but not yet started */
        RUNNABLE,
        /** indicates that this thread is currently running (working) */
        RUNNING,
        /** indicates that this thread is currently waiting	*/
        WAITING,
        /** indicates that this thread is finished */
        FINISHED;
    }

    private static class DoSubmitAccumulativeRunnable
            extends AccumulativeRunnable<Runnable> {

//        private final static int DELAY = (int) (1000 / 30);
        private void doRun() {
            run();
        }

        @Override
        protected void run(List<Runnable> args) {
//            log.debug("submit ar run: " + args);
            for (Runnable runnable : args) {
                runnable.run();
            }
//            log.debug("submit ar done");
        }

        @Override
        protected void submit() {
//            log.debug("submit ar submit");
//            Timer timer = new Timer(DELAY, this);
//            display.timerExec(DELAY, new Runnable() {
//            display.asyncExec(new Runnable() {
//            Display.getDefault().asyncExec(new Runnable() {
                display.asyncExec(new Runnable() {

                public void run() {
//                    log.debug("submit ar display run()");
                    doRun();
                }
            });
//            timer.setRepeats(false);
//            timer.start();
//            log.debug("submit ar done");
        }
    }
}
