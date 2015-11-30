package com.leontg77.uhc.scenario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.scenarios.AchievementParanoia;
import com.leontg77.uhc.scenario.scenarios.Assassins;
import com.leontg77.uhc.scenario.scenarios.AssaultAndBattery;
import com.leontg77.uhc.scenario.scenarios.Astrophobia;
import com.leontg77.uhc.scenario.scenarios.Aurophobia;
import com.leontg77.uhc.scenario.scenarios.Backpacks;
import com.leontg77.uhc.scenario.scenarios.Barebones;
import com.leontg77.uhc.scenario.scenarios.BestBTC;
import com.leontg77.uhc.scenario.scenarios.BestPvE;
import com.leontg77.uhc.scenario.scenarios.BetaZombies;
import com.leontg77.uhc.scenario.scenarios.BigCrack;
import com.leontg77.uhc.scenario.scenarios.BiomeParanoia;
import com.leontg77.uhc.scenario.scenarios.BlockRush;
import com.leontg77.uhc.scenario.scenarios.BloodDiamonds;
import com.leontg77.uhc.scenario.scenarios.BloodEnchants;
import com.leontg77.uhc.scenario.scenarios.BloodLapis;
import com.leontg77.uhc.scenario.scenarios.Captains;
import com.leontg77.uhc.scenario.scenarios.ChunkApocalypse;
import com.leontg77.uhc.scenario.scenarios.Compensation;
import com.leontg77.uhc.scenario.scenarios.Cryophobia;
import com.leontg77.uhc.scenario.scenarios.CutClean;
import com.leontg77.uhc.scenario.scenarios.DamageCycle;
import com.leontg77.uhc.scenario.scenarios.Depths;
import com.leontg77.uhc.scenario.scenarios.Diamondless;
import com.leontg77.uhc.scenario.scenarios.DragonRush;
import com.leontg77.uhc.scenario.scenarios.Eggs;
import com.leontg77.uhc.scenario.scenarios.EnchantParanoia;
import com.leontg77.uhc.scenario.scenarios.EnchantedDeath;
import com.leontg77.uhc.scenario.scenarios.Fallout;
import com.leontg77.uhc.scenario.scenarios.FlowerPower;
import com.leontg77.uhc.scenario.scenarios.Genie;
import com.leontg77.uhc.scenario.scenarios.GoToHell;
import com.leontg77.uhc.scenario.scenarios.GoldRush;
import com.leontg77.uhc.scenario.scenarios.Goldless;
import com.leontg77.uhc.scenario.scenarios.GoneFishing;
import com.leontg77.uhc.scenario.scenarios.Halloween;
import com.leontg77.uhc.scenario.scenarios.HundredHearts;
import com.leontg77.uhc.scenario.scenarios.InfiniteEnchanter;
import com.leontg77.uhc.scenario.scenarios.Inventors;
import com.leontg77.uhc.scenario.scenarios.Kings;
import com.leontg77.uhc.scenario.scenarios.Krenzinator;
import com.leontg77.uhc.scenario.scenarios.LAFS;
import com.leontg77.uhc.scenario.scenarios.Lootcrates;
import com.leontg77.uhc.scenario.scenarios.MeleeFun;
import com.leontg77.uhc.scenario.scenarios.Moles;
import com.leontg77.uhc.scenario.scenarios.MysteryTeams;
import com.leontg77.uhc.scenario.scenarios.NightmareMode;
import com.leontg77.uhc.scenario.scenarios.NoFall;
import com.leontg77.uhc.scenario.scenarios.NoSprint;
import com.leontg77.uhc.scenario.scenarios.Paranoia;
import com.leontg77.uhc.scenario.scenarios.PeriodOfResistance;
import com.leontg77.uhc.scenario.scenarios.Permakill;
import com.leontg77.uhc.scenario.scenarios.PotentialHearts;
import com.leontg77.uhc.scenario.scenarios.PotentialMoles;
import com.leontg77.uhc.scenario.scenarios.PotentialPermanent;
import com.leontg77.uhc.scenario.scenarios.Pyrophobia;
import com.leontg77.uhc.scenario.scenarios.RewardingLongshots;
import com.leontg77.uhc.scenario.scenarios.RewardingLongshotsPlus;
import com.leontg77.uhc.scenario.scenarios.SharedHealth;
import com.leontg77.uhc.scenario.scenarios.SkyClean;
import com.leontg77.uhc.scenario.scenarios.Skyhigh;
import com.leontg77.uhc.scenario.scenarios.SlaveMarket;
import com.leontg77.uhc.scenario.scenarios.SlimyCrack;
import com.leontg77.uhc.scenario.scenarios.Superheroes;
import com.leontg77.uhc.scenario.scenarios.Switcheroo;
import com.leontg77.uhc.scenario.scenarios.TeamHealth;
import com.leontg77.uhc.scenario.scenarios.Timber;
import com.leontg77.uhc.scenario.scenarios.Timebomb;
import com.leontg77.uhc.scenario.scenarios.TrainingRabbits;
import com.leontg77.uhc.scenario.scenarios.TripleOres;
import com.leontg77.uhc.scenario.scenarios.VengefulSpirits;
import com.leontg77.uhc.scenario.scenarios.Voidscape;

/**
 * Scenario management class.
 * 
 * @author LeonTG77
 */
public class ScenarioManager {
	private HashSet<Scenario> scenarios = new HashSet<Scenario>();
	private static ScenarioManager manager = new ScenarioManager();
	
	/**
	 * Get the instance of this class.
	 * 
	 * @return The class instance.
	 */
	public static ScenarioManager getInstance() {
		return manager;
	}
	
	/**
	 * Setup all the scenarios.
	 */
	public void setup() {
		scenarios.add(new AchievementParanoia());
		scenarios.add(new Assassins());
		scenarios.add(new AssaultAndBattery());
		scenarios.add(new Astrophobia());
		scenarios.add(new Aurophobia());
		scenarios.add(new Backpacks());
		scenarios.add(new Barebones());
		scenarios.add(new BestBTC());
		scenarios.add(new BestPvE());
		scenarios.add(new BetaZombies());
		scenarios.add(new BigCrack());
		scenarios.add(new BiomeParanoia());
		scenarios.add(new BlockRush());
		scenarios.add(new BloodDiamonds());
		scenarios.add(new BloodEnchants());
		scenarios.add(new BloodLapis());
		scenarios.add(new Captains());
		scenarios.add(new ChunkApocalypse());
		scenarios.add(new Compensation());
		scenarios.add(new Cryophobia());
		scenarios.add(new CutClean());
		scenarios.add(new DamageCycle());
		scenarios.add(new Depths());
		scenarios.add(new Diamondless());
		scenarios.add(new DragonRush());
		scenarios.add(new Eggs());
		scenarios.add(new EnchantedDeath());
		/* TODO: Enchanted Books and Enchant preview */ scenarios.add(new EnchantParanoia());
		scenarios.add(new Fallout());
		scenarios.add(new FlowerPower());
		scenarios.add(new Genie());
		scenarios.add(new Goldless());
		scenarios.add(new GoldRush());
		scenarios.add(new GoneFishing());
		scenarios.add(new GoToHell());
		scenarios.add(new Halloween());
		scenarios.add(new HundredHearts());
		scenarios.add(new InfiniteEnchanter());
		scenarios.add(new Inventors());
		scenarios.add(new Kings());
		scenarios.add(new Krenzinator());
		scenarios.add(new LAFS());
		scenarios.add(new Lootcrates());
		scenarios.add(new MeleeFun());
		scenarios.add(new Moles());
		scenarios.add(new MysteryTeams());
		scenarios.add(new NightmareMode());
		scenarios.add(new NoFall());
		scenarios.add(new NoSprint());
		scenarios.add(new Paranoia());
		scenarios.add(new PeriodOfResistance());
		scenarios.add(new Permakill());
		scenarios.add(new PotentialHearts());
		scenarios.add(new PotentialMoles());
		scenarios.add(new PotentialPermanent());
		scenarios.add(new Pyrophobia());
		scenarios.add(new RewardingLongshots());
		scenarios.add(new RewardingLongshotsPlus());
		scenarios.add(new SharedHealth());
		scenarios.add(new SkyClean());
		scenarios.add(new Skyhigh());
		scenarios.add(new SlaveMarket());
		scenarios.add(new SlimyCrack());
		scenarios.add(new Superheroes());
		scenarios.add(new Switcheroo());
		scenarios.add(new TeamHealth());
		scenarios.add(new Timber());
		scenarios.add(new Timebomb());
		scenarios.add(new TrainingRabbits());
		scenarios.add(new TripleOres());
		scenarios.add(new VengefulSpirits());
		scenarios.add(new Voidscape());
		
		Main.plugin.getLogger().info("All scenarios has been setup.");
	}
	
	/**
	 * Get a scenario by a name.
	 * 
	 * @param name the name.
	 * @return The scenario, null if not found.
	 */
	public Scenario getScenario(String name) {
		for (Scenario s : scenarios) {
			if (s.getName().equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * Get a scenario by the class.
	 * 
	 * @param scenarioClass The class.
	 * @return The scenario, null if not found.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getScenario(Class<T> scenarioClass) {
		for (Scenario s : scenarios) {
			if (s.getClass().equals(scenarioClass)) {
				return (T) s;
			}
		}
		return null;
	}

	/**
	 * Get a list of all scenarios.
	 * 
	 * @return the list of scenarios.
	 */
	public List<Scenario> getScenarios() {
		return new ArrayList<Scenario>(scenarios);
	}

	/**
	 * Get a list of all enabled scenarios.
	 * 
	 * @return the list of enabled scenarios.
	 */
	public List<Scenario> getEnabledScenarios() {
		List<Scenario> list = new ArrayList<Scenario>();
		
		for (Scenario s : scenarios) {
			if (s.isEnabled()) {
				list.add(s);
			}
		}
		
		return list;
	}

	/**
	 * Get a list of all enabled scenarios.
	 * 
	 * @return the list of enabled scenarios.
	 */
	public List<Scenario> getDisabledScenarios() {
		List<Scenario> list = new ArrayList<Scenario>();
		
		for (Scenario s : scenarios) {
			if (!s.isEnabled()) {
				list.add(s);
			}
		}
		
		return list;
	}
}