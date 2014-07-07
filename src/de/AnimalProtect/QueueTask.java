package de.AnimalProtect;

import java.util.concurrent.SynchronousQueue;

import org.bukkit.scheduler.BukkitRunnable;

public class QueueTask extends BukkitRunnable {
	
	private AnimalProtect plugin;
	private SynchronousQueue<String> queries;
	private Boolean running;
	private Integer taskId;
	
	public QueueTask(AnimalProtect plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Fügt eine Query der Queue hinzu.
	 */
	public synchronized void insertQuery(String Query) {
		queries.add(Query);
	}
	
	@SuppressWarnings("deprecation")
	public synchronized void start() {
		if (!running) {
			running = true;
			taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 100, 200);
		}
	}
	
	public synchronized void stop() {
		if (running) {
			running = false;
			plugin.getServer().getScheduler().cancelTask(taskId);
		}
	}
	
	public synchronized boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		synchronized(queries) {
			if (running && !queries.equals("")) {
				//TODO: RUN!!!
			}
		}
	}
}