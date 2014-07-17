package de.AnimalProtect;

import java.util.HashMap;

import org.bukkit.scheduler.BukkitRunnable;

import craftoplugin.core.CraftoMessenger;
import craftoplugin.core.CraftoPlugin;

public class QueueTask extends BukkitRunnable {
	
	private final AnimalProtect plugin;
	private final MySQL database;
	private final Integer tickDelay;
	private String queue;
	private Integer queueSize;
	private Boolean running;
	private Integer taskId;
	private Integer failedQueries;
	
	public QueueTask(AnimalProtect plugin) {
		this.plugin = plugin;
		this.tickDelay = plugin.getConfig().getInt("settings.queue-tick-delay");
		
		HashMap<String, String> map = CraftoPlugin.plugin.getDatenbank().getSQL().getConnectionValues();
		String hostname = map.get("hostname");
		String username = map.get("user");
		String dbname = map.get("database");
		String password = map.get("password");
		String port = map.get("port");
		
		this.database = new MySQL(plugin, hostname, port, dbname, username, password, plugin.isDebugging());
		this.failedQueries = 0;
		this.running = false;
		this.database.openConnection();
		this.taskId = -1;
		this.queue = "";
		this.queueSize = 0;
	}
	
	/**
	 * Fügt eine Query der Queue hinzu.
	 */
	public synchronized void insertQuery(String Query) {
		synchronized(queue) { this.queue += Query + " "; }
		synchronized (queueSize) { this.queueSize++; }
	}
	
	@SuppressWarnings("deprecation")
	public synchronized void start() {
		synchronized(running) {
			if (!running) {
				synchronized (taskId) {
					this.taskId = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, this, 100, tickDelay); 
					if (taskId > 0) {
						synchronized (failedQueries) { this.failedQueries = 0; }
						this.running = true;
					}
					else { Messenger.log("Failed to initialize the query-task!"); }
				}
			}
		}
	}
	
	public synchronized void stop() {
		synchronized (running) {
			if (running) {
				synchronized (taskId) {
					if (taskId > 0) {
						plugin.getServer().getScheduler().cancelTask(taskId);
					}
					running = false;
				}
			}
		}
	}
	
	public synchronized boolean isRunning() {
		synchronized(running) {
			return running;
		}
	}
	
	public synchronized int getSize() {
		synchronized (queueSize) {
			return queueSize;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public synchronized void run() {
		synchronized (queue) {
			if (running && !queue.isEmpty()) {
				boolean success = false;
				synchronized (database) { success = database.execute(queue, true); }
				
				synchronized (failedQueries) {
					if (!success) { this.failedQueries += 1; }
					else { 
						queue = "";
						synchronized(queueSize) { queueSize = 0; }
					}
					
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
						Messenger.log("Stopped the AnimalProtect-QueueTask for 4 hours because it failed more than 5 queries.");
						CraftoMessenger.messageStaff("Stopped the AnimalProtect-QueueTask", true);
						this.stop();
						this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
							public void run() { plugin.getQueue().start(); }
						}, 288000L);
					}
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