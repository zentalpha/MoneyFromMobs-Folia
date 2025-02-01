package me.chocolf.moneyfrommobs.runnables;

import me.chocolf.moneyfrommobs.MoneyFromMobs;
import me.chocolf.moneyfrommobs.managers.MultipliersManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tjdev.util.tjpluginutil.spigot.FoliaUtil;
import org.tjdev.util.tjpluginutil.spigot.scheduler.universalscheduler.UniversalRunnable;
import org.tjdev.util.tjpluginutil.spigot.scheduler.universalscheduler.scheduling.tasks.MyScheduledTask;


public class RepeatingMultiplierEvent extends UniversalRunnable {

	private final MoneyFromMobs plugin;

	public RepeatingMultiplierEvent(MoneyFromMobs plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		MultipliersManager multipliersManager = plugin.getMultipliersManager();
		if (multipliersManager.getCurrentMultiplierEvent() == null){
			multipliersManager.setEventMultiplier(multipliersManager.getRepeatingMultiplier());

			// send multiplier event started message
			Bukkit.getConsoleSender().sendMessage(multipliersManager.getRepeatingStartMessage());
			for (Player p : Bukkit.getServer().getOnlinePlayers()){
				if (!p.hasMetadata("MfmMuteMessages")) {
					p.sendMessage(multipliersManager.getRepeatingStartMessage());
				}
			}

			// run task later to set multiplier back to 0 and send message to players
			MyScheduledTask task = FoliaUtil.scheduler.runTaskLater(() -> {
				multipliersManager.setEventMultiplier(0);
				multipliersManager.setCurrentMultiplierEvent(null, 0);

				Bukkit.getConsoleSender().sendMessage(multipliersManager.getRepeatingEndMessage());
				for (Player p : Bukkit.getServer().getOnlinePlayers()){
					if (!p.hasMetadata("MfmMuteMessages"))
						p.sendMessage(multipliersManager.getRepeatingEndMessage());
				}
			}, multipliersManager.getRepeatingDuration() * 20L);
			multipliersManager.setCurrentMultiplierEvent(task, (long) multipliersManager.getRepeatingDuration());
		}
	}
}
