package de.AnimalProtect;

import org.bukkit.scheduler.BukkitRunnable;

import craftoplugin.core.CraftoMessenger;

public class QueueTask extends BukkitRunnable {
	
	private final AnimalProtect plugin;
	private final MySQL database;
	private String queue;
	private Boolean running;
	private Integer taskId;
	private Integer failedQueries;
	
	public QueueTask(AnimalProtect plugin, MySQL database) {
		this.plugin = plugin;
		this.database = database;
		this.failedQueries = 0;
		this.running = false;
	}
	
	/**
	 * Fügt eine Query der Queue hinzu.
	 */
	public synchronized void insertQuery(String Query) {
		this.queue += Query + " ";
	}
	
	@SuppressWarnings("deprecation")
	public synchronized void start() {
		if (!running) {
			this.running = true;
			this.failedQueries = 0;
			this.taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 100, 200);
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

	@SuppressWarnings("deprecation")
	@Override
	public synchronized void run() {
		synchronized (queue) {
			if (running && !queue.isEmpty()) {
				boolean success = false;
				synchronized (database) { success = database.execute(queue, true); }
				
				if (!success) { this.failedQueries += 1; }
				
				if (failedQueries == 2) {
					this.stop();
					this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
						public void run() { plugin.getQueue().start(); }
					}, 600L);
				}
				else if (failedQueries == 3) {
					this.stop();
					this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
						public void run() { plugin.getQueue().start(); }
					}, 1200L);
				}
				else if (failedQueries == 4) {
					this.stop();
					this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
						public void run() { plugin.getQueue().start(); }
					}, 6000L);
				}
				else if (failedQueries > 5) {
					CraftoMessenger.log("Stopped the AnimalProtect-QueueTask because it failed more than 12 Queries");
					CraftoMessenger.messageStaff("Stopped the AnimalProtect-QueueTask", true);
					this.stop();
				}
			}
		}
	}
	
	/* Alte Queries:
	 * UPDATE ap_entities SET owner=1, last_x=5, last_y=64, last_z=250, alive=1, nametag='Peter', deathcause='NONE', color='BLUE', armor='NONE' WHERE id=1;
	 * UPDATE test
	 * SET test_order = case test_id
	 *     when 1 then 2
	 *     when 2 then 3
	 *     when 3 then 1 end
	 * WHERE test_id in(1,2,3)
	 */
}