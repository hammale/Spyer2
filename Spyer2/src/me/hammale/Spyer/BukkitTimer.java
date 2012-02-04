package me.hammale.Spyer;

import java.util.TimerTask;

/**
* Cool class. That it is :D
*
* @author nickguletskii200
*/
public class BukkitTimer {
	private int id;
	private SpyerMain plugin;
	
	public BukkitTimer(SpyerMain plug) {
		plugin = plug;
	}	
	public void scheduleAtFixedRate(TimerTask tsk, int delay, int step) {
		id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(
		plugin, tsk, delay, step);
	}
	
	public void cancel() {
			plugin.getServer().getScheduler().cancelTask(id);
	}
}

