package com.leontg77.ultrahardcore.feature;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.feature.border.BorderShrinkFeature;
import com.leontg77.ultrahardcore.feature.death.DeathLightningFeature;
import com.leontg77.ultrahardcore.feature.death.DeathMessageFeature;
import com.leontg77.ultrahardcore.feature.death.RespawnFeature;
import com.leontg77.ultrahardcore.feature.enchants.AnvilsFeature;
import com.leontg77.ultrahardcore.feature.enchants.BookshelfFeature;
import com.leontg77.ultrahardcore.feature.enchants.EnchantmentPreviewFeature;
import com.leontg77.ultrahardcore.feature.entity.MobRatesFeature;
import com.leontg77.ultrahardcore.feature.entity.PetFeature;
import com.leontg77.ultrahardcore.feature.entity.WitchHealthPotionFeature;
import com.leontg77.ultrahardcore.feature.food.SaturationFixFeature;
import com.leontg77.ultrahardcore.feature.health.AbsorptionFeature;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.feature.health.HardcoreHeartsFeature;
import com.leontg77.ultrahardcore.feature.health.HealthRegenFeature;
import com.leontg77.ultrahardcore.feature.horses.HorseArmorFeature;
import com.leontg77.ultrahardcore.feature.horses.HorseFeature;
import com.leontg77.ultrahardcore.feature.horses.HorseHealingFeature;
import com.leontg77.ultrahardcore.feature.pearl.PearlDamageFeature;
import com.leontg77.ultrahardcore.feature.portal.EndFeature;
import com.leontg77.ultrahardcore.feature.portal.NetherFeature;
import com.leontg77.ultrahardcore.feature.portal.PortalCampingFeature;
import com.leontg77.ultrahardcore.feature.portal.PortalTrappingFeature;
import com.leontg77.ultrahardcore.feature.portal.PortalTravelSoundFeature;
import com.leontg77.ultrahardcore.feature.potions.NerfedStrengthFeature;
import com.leontg77.ultrahardcore.feature.potions.PotionFuelListener;
import com.leontg77.ultrahardcore.feature.potions.RegenPotionFeature;
import com.leontg77.ultrahardcore.feature.potions.SplashPotionFeature;
import com.leontg77.ultrahardcore.feature.potions.StrengthPotionFeature;
import com.leontg77.ultrahardcore.feature.potions.Tier2PotionFeature;
import com.leontg77.ultrahardcore.feature.pvp.AntiIPvPFeature;
import com.leontg77.ultrahardcore.feature.pvp.CombatLogFeature;
import com.leontg77.ultrahardcore.feature.pvp.LongshotFeature;
import com.leontg77.ultrahardcore.feature.pvp.ShootHealthFeature;
import com.leontg77.ultrahardcore.feature.pvp.StalkingFeature;
import com.leontg77.ultrahardcore.feature.rainbow.RainbowArmorFeature;
import com.leontg77.ultrahardcore.feature.rates.AppleRatesFeature;
import com.leontg77.ultrahardcore.feature.rates.FlintRatesFeature;
import com.leontg77.ultrahardcore.feature.rates.ShearsFeature;
import com.leontg77.ultrahardcore.feature.recipes.GlisteringMelonRecipeFeature;
import com.leontg77.ultrahardcore.feature.recipes.GoldenCarrotRecipeFeature;
import com.leontg77.ultrahardcore.feature.recipes.GoldplateCraftbackFeature;
import com.leontg77.ultrahardcore.feature.recipes.IronplateCraftbackFeature;
import com.leontg77.ultrahardcore.feature.recipes.NotchApplesFeature;
import com.leontg77.ultrahardcore.feature.scoreboard.KillBoardFeature;
import com.leontg77.ultrahardcore.feature.scoreboard.SidebarResetFeature;
import com.leontg77.ultrahardcore.feature.serverlist.ServerMOTDFeature;
import com.leontg77.ultrahardcore.feature.tablist.HeartsOnTabFeature;
import com.leontg77.ultrahardcore.feature.tablist.PercentOnTabFeature;
import com.leontg77.ultrahardcore.feature.tablist.TabHealthColorFeature;
import com.leontg77.ultrahardcore.feature.world.WeatherFeature;
import com.leontg77.ultrahardcore.feature.world.WorldUpdaterFeature;
import com.leontg77.ultrahardcore.feature.xp.NerfedQuartzXPFeature;
import com.leontg77.ultrahardcore.feature.xp.NerfedXPFeature;
import com.leontg77.ultrahardcore.managers.BoardManager;

/**
 * Scenario management class.
 * 
 * @author LeonTG77
 */
public class FeatureManager {
	private final Settings settings;
	private final Main plugin;
	
	public FeatureManager(Main plugin, Settings settings) {
		this.settings = settings;
		this.plugin = plugin;
	}
	
	private final List<Feature> features = new ArrayList<Feature>();
	
	/**
	 * Get a feature by a name.
	 * 
	 * @param name the name.
	 * @return The feature, null if not found.
	 */
	public Feature getFeature(String name) {
		for (Feature feat : features) {
			if (feat.getName().equalsIgnoreCase(name)) {
				return feat;
			}
		}
		
		return null;
	}
	
	/**
	 * Get a feature by the class.
	 * 
	 * @param featureClass The class.
	 * @return The feature, null if not found.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getFeature(Class<T> featureClass) {
		for (Feature feat : features) {
			if (feat.getClass().equals(featureClass)) {
				return (T) feat;
			}
		}
		
		return null;
	}

	/**
	 * Get a list of all features.
	 * 
	 * @return the list of features.
	 */
	public List<Feature> getFeatures() {
		return ImmutableList.copyOf(features);
	}

	/**
	 * Get a list of all toggleable features.
	 * 
	 * @return the list of toggleable.
	 */
	public List<ToggleableFeature> getToggleableFeatures() {
		List<ToggleableFeature> list = new ArrayList<ToggleableFeature>();
		
		for (Feature feat : features) {
			if (!(feat instanceof ToggleableFeature)) {
				continue;
			}
			
			list.add((ToggleableFeature) feat);
		}
		
		return list;
	}
	
	/**
	 * Setup all the feature classes.
	 */
	public void setup(Arena arena, Game game, Timer timer, BoardManager board) {
		final PotionFuelListener listener = new PotionFuelListener();
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	    
		// permanent
		addFeature(new BorderShrinkFeature());
		
		// death
		addFeature(new DeathLightningFeature(arena));
		addFeature(new DeathMessageFeature(arena));
		addFeature(new RespawnFeature(plugin, arena, game));
		
		// enchants
		addFeature(new AnvilsFeature());
		addFeature(new BookshelfFeature());
		addFeature(new EnchantmentPreviewFeature());
		
		// entity
		addFeature(new MobRatesFeature(timer, game));
		addFeature(new PetFeature());
		addFeature(new WitchHealthPotionFeature());
		
		// food
		addFeature(new SaturationFixFeature());
		
		// health
		addFeature(new AbsorptionFeature());
		addFeature(new GoldenHeadsFeature());
		addFeature(new HardcoreHeartsFeature());
		addFeature(new HealthRegenFeature());
		
		// horses
		addFeature(new HorseArmorFeature());
		addFeature(new HorseFeature());
		addFeature(new HorseHealingFeature());
		
		// pearl
		addFeature(new PearlDamageFeature());
		
		// portal
		addFeature(new EndFeature());
		addFeature(new NetherFeature());
		addFeature(new PortalCampingFeature());
		addFeature(new PortalTrappingFeature());
		addFeature(new PortalTravelSoundFeature());
		
		// potions
		addFeature(new NerfedStrengthFeature());
		addFeature(new RegenPotionFeature(listener));
		addFeature(new SplashPotionFeature(listener));
		addFeature(new StrengthPotionFeature(listener));
		addFeature(new Tier2PotionFeature(listener));
		
		// pvp
		addFeature(new AntiIPvPFeature());
		addFeature(new CombatLogFeature());
		addFeature(new LongshotFeature());
		addFeature(new ShootHealthFeature());
		addFeature(new StalkingFeature());
		
		// rainbow
		addFeature(new RainbowArmorFeature(plugin));
		
		// rates
		addFeature(new AppleRatesFeature());
		addFeature(new FlintRatesFeature());
		addFeature(new ShearsFeature());
		
		// recipes.
		addFeature(new GlisteringMelonRecipeFeature());
		addFeature(new GoldenCarrotRecipeFeature());
		addFeature(new GoldplateCraftbackFeature());
		addFeature(new IronplateCraftbackFeature());
		addFeature(new NotchApplesFeature());
		
		// scoreboard
		addFeature(new KillBoardFeature());
		addFeature(new SidebarResetFeature());
		
		// serverlist
		addFeature(new ServerMOTDFeature(game));
		
		// tablist
		addFeature(new HeartsOnTabFeature(board));
		addFeature(new PercentOnTabFeature(plugin, board));
		addFeature(new TabHealthColorFeature(plugin));
		
		// world
		addFeature(new WeatherFeature(game, timer));
		addFeature(new WorldUpdaterFeature(plugin));
		
		// xp
		addFeature(new NerfedQuartzXPFeature());
		addFeature(new NerfedXPFeature());
		
		plugin.getLogger().info("All features has been setup.");
	}
	
	/**
	 * Register a feature and register their listener if there's any.
	 * 
	 * @param feature The feature to register.
	 */
	private void addFeature(Feature feature) {
		features.add(feature);
		
		if (feature instanceof Listener) {
			Bukkit.getPluginManager().registerEvents((Listener) feature, plugin);
		}
		
		if (feature instanceof ToggleableFeature) {
			ToggleableFeature toggle = (ToggleableFeature) feature;
			
			toggle.enabled = settings.getConfig().getBoolean("feature." + feature.getName().toLowerCase() + ".enabled", false);
			
			if (toggle.isEnabled()) {
				toggle.onEnable();
			} else {
				toggle.onDisable();
			}
		}
	}
}