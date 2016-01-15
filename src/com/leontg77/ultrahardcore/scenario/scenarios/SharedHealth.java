package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * SharedHealth scenario class
 * 
 * @author dans1988
 */
public class SharedHealth extends Scenario implements Listener {
	private TeamManager manager = TeamManager.getInstance();
	
	private Map<String, Double> damageBalance;
    private Map<String, Boolean> sharedDamage;
	
	public SharedHealth() {
		super("SharedHealth", "All teammates share their health, does not apply for lava, fire or poison damage");
		
		damageBalance = new HashMap<String, Double>();
        sharedDamage = new HashMap<String, Boolean>();
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}

	@EventHandler
    public void on(PlayerJoinEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
        Player player = event.getPlayer();
        double balance = getPlayersDamageBalance(player.getName());

        if (balance == 0.0D) {
        	return;
        }
        
        double newHealth = (player.getHealth() + balance > 0.0D) ? player.getHealth() + balance : 0.0D;
        
        if (newHealth > player.getMaxHealth()) {
            newHealth = player.getMaxHealth();
        }
        
        player.setHealth(newHealth);
        resetPlayersDamageBalance(player.getName());
    }
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(final EntityDamageEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        setSharedDamage(player.getName(), false);

        if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.POISON) {
        	return;
        }

        final Team team = manager.getTeam(player);

        if (team == null || team.getSize() <= 1) {
            return;
        }

        double damage = event.getFinalDamage();
        double teamSize = team.getSize();
        double sharedDamage = damage / teamSize;

        for (OfflinePlayer teammate : manager.getPlayers(team)) {
            if (!teammate.isOnline()) {
                setPlayersDamageBalance(teammate.getName(), sharedDamage * -1.0D);
                setSharedDamage(teammate.getName(), true);
                continue;
            }
                
            Player onlineTeammate = teammate.getPlayer();
            
            double currentHealth = onlineTeammate.getHealth();
            double finalHealth = currentHealth - sharedDamage;

            if (finalHealth < 0.0D) {
                finalHealth = 0.0D;
            }

            final double finalHealthAsynch = finalHealth;

            if (!onlineTeammate.getUniqueId().equals(player.getUniqueId())) {
                setSharedDamage(onlineTeammate.getName(), true);
                onlineTeammate.damage(0.0D);
                onlineTeammate.setHealth(finalHealthAsynch);
            }
        }

        double currentHealth = player.getHealth();
        double finalHealth = currentHealth - sharedDamage;

        if (finalHealth < 0.0D) {
            finalHealth = 0;
        }

        if (finalHealth == 0) {
        	event.setDamage(event.getDamage() * 1000);
        	return;
        }
        
        final double finalHealthAsynch = finalHealth;
        event.setDamage(0.0D);

        new BukkitRunnable() {
            public void run() {
                player.setHealth(finalHealthAsynch);
            }
        }.runTaskLater(Main.plugin, 1);
    }
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}

        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        Team team = manager.getTeam(player);

        if (team == null || team.getSize() <= 1) {
            return;
        }

        double divider = team.getSize();
        double amount = event.getAmount();
        double gain = amount / divider;

        for (OfflinePlayer offlinePlayer : manager.getPlayers(team)) {
            Player teammate = offlinePlayer.getPlayer();

            if (teammate == null) {
            	setPlayersDamageBalance(offlinePlayer.getName(), gain);
            	continue;
            }
        	
            double teammateHealth = teammate.getHealth();
            double finalHealth = teammateHealth + gain;
            
            if (finalHealth > teammate.getMaxHealth()) {
                finalHealth = teammate.getMaxHealth();
            }
            
            teammate.setHealth(finalHealth);
        }

        event.setCancelled(true);
    }
	
	@EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
        String playerName = event.getEntity().getName();

        Player player = event.getEntity();
        Team team = manager.getTeam(player);
        
        String playerDisplayName = event.getEntity().getName();

        if (team != null) {
        	playerDisplayName = team.getPrefix() + playerDisplayName;
        	team.removeEntry(player.getName());
        }
        
        if (getSharedDamage().get(playerName) != null && getSharedDamage().get(playerName) == true) {
            event.setDeathMessage(playerDisplayName + ChatColor.WHITE + " died from sharing health");
        }
    }
	
    public Double getPlayersDamageBalance(String player) {
        if (damageBalance.containsKey(player)) {
            return damageBalance.get(player);
        } else {
            return 0.0D;
        }
    }

    public void setPlayersDamageBalance(String player, Double balance) {
        if (!damageBalance.containsKey(player)) {
            damageBalance.put(player, balance);
        } else {
            Double previousBalance = damageBalance.get(player);
            damageBalance.put(player, previousBalance + balance);
        }
    }

    public void resetDamageBalance() {
        this.damageBalance = new HashMap<String, Double>();
    }

    public void resetPlayersDamageBalance(String player) {
        if (damageBalance.containsKey(player)) {
            damageBalance.put(player, 0.0D);
        }
    }

    public Map<String, Boolean> getSharedDamage() {
        return sharedDamage;
    }

    public void setSharedDamage(String name, Boolean wasShared) {
        sharedDamage.put(name, wasShared);
    }

    public void resetSharedDamage() {
        sharedDamage = new HashMap<String, Boolean>();
    }
}