package de.AnimalProtect;

import java.util.HashMap;

import org.bukkit.scheduler.BukkitRunnable;

import craftoplugin.core.CraftoMessenger;
import craftoplugin.core.CraftoPlugin;
import craftoplugin.utility.CraftoFile;

public class QueueTask extends BukkitRunnable {

	private final AnimalProtect plugin;
	private final MySQL database;
	private final Integer tickDelay;
	private String queue;
	private Integer queueSize;
	private boolean running;
	private int taskId;
	private Short failedQueries;

	public QueueTask(final AnimalProtect plugin) {
		this.plugin = plugin;
		this.tickDelay = plugin.getConfig().getInt("settings.queue-tick-delay");

		final HashMap<String, String> map = CraftoPlugin.plugin.getDatenbank().getSQL().getConnectionValues();
		final String hostname = map.get("hostname");
		final String username = map.get("user");
		final String dbname = map.get("database");
		final String password = map.get("password");
		final String port = map.get("port");

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
	public void insertQuery(final String Query) {
		synchronized (this.queue) { this.queue += Query + " "; }
		synchronized (this.queueSize) { this.queueSize++; }
	}

	@SuppressWarnings("deprecation")
	public synchronized void start() {
		if (!this.running) {
			this.taskId = this.plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(this.plugin, this, 100, this.tickDelay); 
			if (this.taskId > 0) {
				this.running = true;

				synchronized (this.failedQueries) { this.failedQueries = 0; }
			}
			else { Messenger.log("Failed to initialize the query-task!"); }
		}
	}

	public synchronized void stop() {
		if (this.running) {
			if (this.taskId > 0) { this.plugin.getServer().getScheduler().cancelTask(this.taskId); }
			this.running = false;
		}
	}

	public boolean isRunning() {
		return this.running;
	}

	public int getSize() {
		synchronized (this.queueSize) { return this.queueSize; }
	}

	@Override
	public void run() {
		boolean success = false;

		synchronized (this.queue) {
			if (!this.queue.isEmpty()) {
				success = this.database.execute(this.queue, true);
				if (success) { this.queue = ""; }
			}
		}

		if (success) {
			synchronized (this.queueSize) { this.queueSize = 0; }
		}
		else {
			this.failedQueries++;

			if (this.failedQueries == 2) {
				this.rescheduleTask(600L);
			}
			else if (this.failedQueries == 3) {
				this.rescheduleTask(1200L);
			}
			else if (this.failedQueries == 4) {
				this.rescheduleTask(6000L);
			}
			else if (this.failedQueries == 5) {
				Messenger.log("Stopped the AnimalProtect-QueueTask for 4 hours because it failed more than 5 queries.");
				CraftoMessenger.warnStaff("Stopped the AnimalProtect-QueueTask.", true);
				this.rescheduleTask(288000L);
			}
			else if (this.failedQueries == 6) {
				Messenger.log("Stopping the AnimalProtect-QueueTask for 4 hours because it failed more than 10 queries.");
				Messenger.log("Saving the queries to plugins/animalprotect/queue.txt");
				CraftoMessenger.warnStaff("Stopped the AnimalProtect-QueueTask.", true);

				final CraftoFile file = new CraftoFile("AnimalProtect", "plugins/AnimalProtect/queue.txt");
				file.writeLine(this.queue);
				try { file.saveFile(); this.queue = ""; this.queueSize = 0; } catch (final Exception e) { Messenger.log("Failed to save queue file."); }
				file.close();

				this.rescheduleTask(288000L);
			}
			else if (this.failedQueries > 6) {
				Messenger.log("Stopped the AnimalProtect-QueueTask for 4 hours because it failed more than 5 queries.");
				CraftoMessenger.warnStaff("Stopped the AnimalProtect-QueueTask.", true);
				this.rescheduleTask(288000L);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public synchronized void rescheduleTask(final Long delay) {
		this.stop();
		this.plugin.getServer().getScheduler().scheduleAsyncDelayedTask(this.plugin, new Runnable() 
		{ @Override
			public void run() { QueueTask.this.plugin.getQueue().start(); } }, delay);
	}

	public synchronized void reloadConnection() {
		this.database.closeConnection();
		this.database.openConnection();
	}
}