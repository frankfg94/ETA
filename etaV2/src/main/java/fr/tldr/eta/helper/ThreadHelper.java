package fr.tldr.eta.helper;

import javafx.application.Platform;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class ThreadHelper
{
	private static final Semaphore mutex = new Semaphore(1); // semaphore = mutex
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	public static void runLater(Runnable runnable)
	{
		if(Platform.isFxApplicationThread())
		{
			runnable.run();
		}
		else
		{
			try
			{
				mutex.acquire();
				Platform.runLater(()->{
					releaseMutex();
					runnable.run();
				});
			} catch (InterruptedException e)
			{
				releaseMutex();
				e.printStackTrace();
			}

		}
	}

	private static void releaseMutex()
	{
		executor.submit((Runnable) mutex::release);
	}

	public static void runLater(Runnable runnable, boolean force)
	{
		if(force)
		{
			Platform.runLater(runnable);
		}
		else
			runLater(runnable);
	}

	public static void start(Runnable runnable)
	{
		Thread thread = new Thread(runnable);
		thread.start();
	}

	public static void latchAwait(CountDownLatch countDownLatch)
	{
		try
		{
			countDownLatch.await();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
