package com.leontg77.ultrahardcore.feature;

import static com.leontg77.ultrahardcore.Main.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.feature.border.BorderShrinkFeature;
import com.leontg77.ultrahardcore.feature.death.DeathLightningFeature;
import com.leontg77.ultrahardcore.feature.enchants.AnvilsFeature;
import com.leontg77.ultrahardcore.feature.enchants.BookshelfFeature;
import com.leontg77.ultrahardcore.feature.enchants.EnchantmentPreviewFeature;
import com.leontg77.ultrahardcore.feature.food.SaturationFixFeature;
import com.leontg77.ultrahardcore.feature.health.AbsorptionFeature;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.feature.health.HardcoreHeartsFeature;
import com.leontg77.ultrahardcore.feature.horses.HorseArmorFeature;
import com.leontg77.ultrahardcore.feature.horses.HorseFeature;
import com.leontg77.ultrahardcore.feature.horses.HorseHealingFeature;
import com.leontg77.ultrahardcore.feature.pearl.PearlDamageFeature;
import com.leontg77.ultrahardcore.feature.portal.EndFeature;
import com.leontg77.ultrahardcore.feature.portal.NetherFeature;
import com.leontg77.ultrahardcore.feature.portal.PortalTravelSoundFeature;
import com.leontg77.ultrahardcore.feature.potions.NerfedStrengthFeature;
import com.leontg77.ultrahardcore.feature.potions.PotionFuelListener;
import com.leontg77.ultrahardcore.feature.potions.RegenPotionFeature;
import com.leontg77.ultrahardcore.feature.potions.SplashPotionFeature;
import com.leontg77.ultrahardcore.feature.potions.StrengthPotionFeature;
import com.leontg77.ultrahardcore.feature.potions.Tier2PotionFeature;
import com.leontg77.ultrahardcore.feature.rates.AppleRatesFeature;
import com.leontg77.ultrahardcore.feature.rates.FlintRatesFeature;
import com.leontg77.ultrahardcore.feature.rates.ShearsFeature;
import com.leontg77.ultrahardcore.feature.recipes.GlisteringMelonRecipeFeature;
import com.leontg77.ultrahardcore.feature.recipes.GoldenCarrotRecipeFeature;
import com.leontg77.ultrahardcore.feature.recipes.NotchApplesFeature;
import com.leontg77.ultrahardcore.feature.scoreboard.KillBoardFeature;
import com.leontg77.ultrahardcore.feature.scoreboard.SidebarResetFeature;
import com.leontg77.ultrahardcore.feature.tablist.HeartsOnTabFeature;
import com.leontg77.ultrahardcore.feature.tablist.TabHealthColorFeature;
import com.leontg77.ultrahardcore.feature.world.AntiStripmineFeature;
import com.leontg77.ultrahardcore.feature.world.NewStoneFeature;
import com.leontg77.ultrahardcore.feature.world.OreLimiterFeature;
import com.leontg77.ultrahardcore.feature.xp.NerfedQuartzXPFeature;
import com.leontg77.ultrahardcore.feature.xp.NerfedXPFeature;

/**
 * Scenario management class.
 * 
 * @author LeonTG77
 */
public class FeatureManager {
	private static final FeatureManager INSTANCE = new FeatureManager();
	private final List<Feature> features = new ArrayList<Feature>();
	
	/**
	 * Get the instance of this class.
	 * 
	 * @return The class instance.
	 */
	public static FeatureManager getInstance() {
		return INSTANCE;
	}
	
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
	public void setup() {
		final PotionFuelListener listener = new PotionFuelListener();
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	    
		// permanent
		addFeature(new BorderShrinkFeature());
		
		// death
		addFeature(new DeathLightningFeature());
		
		// enchants
		addFeature(new AnvilsFeature());
		addFeature(new BookshelfFeature());
		addFeature(new EnchantmentPreviewFeature());
		
		// food
		addFeature(new SaturationFixFeature());
		
		// health
		addFeature(new AbsorptionFeature());
		addFeature(new GoldenHeadsFeature());
		addFeature(new HardcoreHeartsFeature());
		
		// horses
		addFeature(new HorseArmorFeature());
		addFeature(new HorseFeature());
		addFeature(new HorseHealingFeature());
		
		// pearl
		addFeature(new PearlDamageFeature());
		
		// portal
		addFeature(new EndFeature());
		addFeature(new NetherFeature());
		addFeature(new PortalTravelSoundFeature());
		
		// potions
		addFeature(new NerfedStrengthFeature());
		addFeature(new RegenPotionFeature(listener));
		addFeature(new SplashPotionFeature(listener));
		addFeature(new StrengthPotionFeature(listener));
		addFeature(new Tier2PotionFeature(listener));
		
		// rates
		addFeature(new AppleRatesFeature());
		addFeature(new FlintRatesFeature());
		addFeature(new ShearsFeature());
		
		// recipes.
		addFeature(new GlisteringMelonRecipeFeature());
		addFeature(new GoldenCarrotRecipeFeature());
		addFeature(new NotchApplesFeature());
		
		// scoreboard
		addFeature(new KillBoardFeature());
		addFeature(new SidebarResetFeature());
		
		// tablist
		addFeature(new HeartsOnTabFeature());
		addFeature(new TabHealthColorFeature());
		
		// world
		addFeature(new AntiStripmineFeature());
		addFeature(new NewStoneFeature());
		addFeature(new OreLimiterFeature());
		
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
			
			if (toggle.isEnabled()) {
				toggle.onEnable();
			} else {
				toggle.onDisable();
			}
		}
	}
}