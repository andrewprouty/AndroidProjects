package com.prouty.leagueusa.sdsolschedule;

// http://stackoverflow.com/questions/9868363/semaphore-simple-sample
public class Semaphore {
	private boolean signal = false;

	public synchronized void take() {
		this.signal = true;
		this.notify();
	}

	public synchronized void release() throws InterruptedException{
		while(!this.signal) wait();
		this.signal = false;
	}
}