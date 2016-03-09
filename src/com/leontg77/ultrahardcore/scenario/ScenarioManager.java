package com.leontg77.ultrahardcore.scenario;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.scenarios.AchievementParanoia;
import com.leontg77.ultrahardcore.scenario.scenarios.Armageddon;
import com.leontg77.ultrahardcore.scenario.scenarios.Assassins;
import com.leontg77.ultrahardcore.scenario.scenarios.AssaultAndBattery;
import com.leontg77.ultrahardcore.scenario.scenarios.Astrophobia;
import com.leontg77.ultrahardcore.scenario.scenarios.Aurophobia;
import com.leontg77.ultrahardcore.scenario.scenarios.Backpacks;
import com.leontg77.ultrahardcore.scenario.scenarios.Balance;
import com.leontg77.ultrahardcore.scenario.scenarios.BaldChicken;
import com.leontg77.ultrahardcore.scenario.scenarios.Barebones;
import com.leontg77.ultrahardcore.scenario.scenarios.BedBombs;
import com.leontg77.ultrahardcore.scenario.scenarios.BenchBlitz;
import com.leontg77.ultrahardcore.scenario.scenarios.BestBTC;
import com.leontg77.ultrahardcore.scenario.scenarios.BestPvE;
import com.leontg77.ultrahardcore.scenario.scenarios.BetaZombies;
import com.leontg77.ultrahardcore.scenario.scenarios.BigCrack;
import com.leontg77.ultrahardcore.scenario.scenarios.BiomeParanoia;
import com.leontg77.ultrahardcore.scenario.scenarios.Birds;
import com.leontg77.ultrahardcore.scenario.scenarios.Blitz;
import com.leontg77.ultrahardcore.scenario.scenarios.BlockRush;
import com.leontg77.ultrahardcore.scenario.scenarios.Blocked;
import com.leontg77.ultrahardcore.scenario.scenarios.BloodCycle;
import com.leontg77.ultrahardcore.scenario.scenarios.BloodDiamonds;
import com.leontg77.ultrahardcore.scenario.scenarios.BloodEnchants;
import com.leontg77.ultrahardcore.scenario.scenarios.BloodLapis;
import com.leontg77.ultrahardcore.scenario.scenarios.Bombers;
import com.leontg77.ultrahardcore.scenario.scenarios.BowFighters;
import com.leontg77.ultrahardcore.scenario.scenarios.Captains;
import com.leontg77.ultrahardcore.scenario.scenarios.CarrotCombo;
import com.leontg77.ultrahardcore.scenario.scenarios.CatsEyes;
import com.leontg77.ultrahardcore.scenario.scenarios.Chicken;
import com.leontg77.ultrahardcore.scenario.scenarios.ChunkApocalypse;
import com.leontg77.ultrahardcore.scenario.scenarios.Cloud9;
import com.leontg77.ultrahardcore.scenario.scenarios.Cobblehaters;
import com.leontg77.ultrahardcore.scenario.scenarios.Coco;
import com.leontg77.ultrahardcore.scenario.scenarios.Compensation;
import com.leontg77.ultrahardcore.scenario.scenarios.Cripple;
import com.leontg77.ultrahardcore.scenario.scenarios.Cryophobia;
import com.leontg77.ultrahardcore.scenario.scenarios.CutClean;
import com.leontg77.ultrahardcore.scenario.scenarios.DamageCycle;
import com.leontg77.ultrahardcore.scenario.scenarios.Depths;
import com.leontg77.ultrahardcore.scenario.scenarios.Diamondless;
import com.leontg77.ultrahardcore.scenario.scenarios.DragonRush;
import com.leontg77.ultrahardcore.scenario.scenarios.Eggs;
import com.leontg77.ultrahardcore.scenario.scenarios.EightLeggedFreaks;
import com.leontg77.ultrahardcore.scenario.scenarios.EnchantedDeath;
import com.leontg77.ultrahardcore.scenario.scenarios.Entropy;
import com.leontg77.ultrahardcore.scenario.scenarios.Fallout;
import com.leontg77.ultrahardcore.scenario.scenarios.FlowerPower;
import com.leontg77.ultrahardcore.scenario.scenarios.Genie;
import com.leontg77.ultrahardcore.scenario.scenarios.GoToHell;
import com.leontg77.ultrahardcore.scenario.scenarios.GoldRush;
import com.leontg77.ultrahardcore.scenario.scenarios.Goldless;
import com.leontg77.ultrahardcore.scenario.scenarios.GoneFishing;
import com.leontg77.ultrahardcore.scenario.scenarios.Halloween;
import com.leontg77.ultrahardcore.scenario.scenarios.HundredHearts;
import com.leontg77.ultrahardcore.scenario.scenarios.InfiniteEnchanter;
import com.leontg77.ultrahardcore.scenario.scenarios.Inventors;
import com.leontg77.ultrahardcore.scenario.scenarios.Kings;
import com.leontg77.ultrahardcore.scenario.scenarios.Krenzinator;
import com.leontg77.ultrahardcore.scenario.scenarios.LAFS;
import com.leontg77.ultrahardcore.scenario.scenarios.Landmines;
import com.leontg77.ultrahardcore.scenario.scenarios.Lootcrates;
import com.leontg77.ultrahardcore.scenario.scenarios.MeleeFun;
import com.leontg77.ultrahardcore.scenario.scenarios.Moles;
import com.leontg77.ultrahardcore.scenario.scenarios.MonstersInc;
import com.leontg77.ultrahardcore.scenario.scenarios.MysteryTeams;
import com.leontg77.ultrahardcore.scenario.scenarios.NightmareMode;
import com.leontg77.ultrahardcore.scenario.scenarios.NoFall;
import com.leontg77.ultrahardcore.scenario.scenarios.NoSprint;
import com.leontg77.ultrahardcore.scenario.scenarios.Paranoia;
import com.leontg77.ultrahardcore.scenario.scenarios.PeriodOfResistance;
import com.leontg77.ultrahardcore.scenario.scenarios.Permakill;
import com.leontg77.ultrahardcore.scenario.scenarios.PotentialHearts;
import com.leontg77.ultrahardcore.scenario.scenarios.PotentialMoles;
import com.leontg77.ultrahardcore.scenario.scenarios.PotentialPermanent;
import com.leontg77.ultrahardcore.scenario.scenarios.Pyrophobia;
import com.leontg77.ultrahardcore.scenario.scenarios.RewardingLongshots;
import com.leontg77.ultrahardcore.scenario.scenarios.RewardingLongshotsPlus;
import com.leontg77.ultrahardcore.scenario.scenarios.SharedHealth;
import com.leontg77.ultrahardcore.scenario.scenarios.SkyClean;
import com.leontg77.ultrahardcore.scenario.scenarios.Skyhigh;
import com.leontg77.ultrahardcore.scenario.scenarios.SlaveMarket;
import com.leontg77.ultrahardcore.scenario.scenarios.SlimyCrack;
import com.leontg77.ultrahardcore.scenario.scenarios.Snowday;
import com.leontg77.ultrahardcore.scenario.scenarios.Superheroes;
import com.leontg77.ultrahardcore.scenario.scenarios.Switcheroo;
import com.leontg77.ultrahardcore.scenario.scenarios.TeamHealth;
import com.leontg77.ultrahardcore.scenario.scenarios.Timber;
import com.leontg77.ultrahardcore.scenario.scenarios.Timebomb;
import com.leontg77.ultrahardcore.scenario.scenarios.TrainingRabbits;
import com.leontg77.ultrahardcore.scenario.scenarios.TripleArrows;
import com.leontg77.ultrahardcore.scenario.scenarios.TripleOres;
import com.leontg77.ultrahardcore.scenario.scenarios.VengefulSpirits;
import com.leontg77.ultrahardcore.scenario.scenarios.Voidscape;

/**
 * Scenario management class.
 * 
 * @author LeonTG77
 */
public class ScenarioManager {
	private final Main plugin;
	
	public ScenarioManager(Main plugin) {
		this.plugin = plugin;
	}

	private List<Scenario> scenarios = new ArrayList<Scenario>();
	
	/**
	 * Get a scenario by a name.
	 * 
	 * @param name the name.
	 * @return The scenario, null if not found.
	 */
	public Scenario getScenario(String name) {
		for (Scenario scen : scenarios) {
			if (scen.getName().equalsIgnoreCase(name)) {
				return scen;
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
		return ImmutableList.copyOf(scenarios);
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
	
	/**
	 * Setup all the scenario classes.
	 */
	public void registerScenarios(Arena arena, Game game, Timer timer, TeamManager teams, SpecManager spec, Settings settings, FeatureManager feat) {
		CutClean cc = new CutClean(this);
		
		scenarios.add(new AchievementParanoia(game));
		scenarios.add(new Armageddon(plugin, game));
		scenarios.add(new Assassins(timer, game));
		scenarios.add(new AssaultAndBattery(game, teams));
		scenarios.add(new Astrophobia(plugin, timer, game));
		scenarios.add(new Aurophobia(game));
		scenarios.add(new Backpacks(plugin, game));
		scenarios.add(new Balance(game));
		scenarios.add(new BaldChicken());
		scenarios.add(new Barebones(game, cc));
		scenarios.add(new BedBombs(game));
		scenarios.add(new BenchBlitz(game));
		scenarios.add(new BestBTC(plugin, timer));
		scenarios.add(new BestPvE(plugin, timer));
		scenarios.add(new BetaZombies());
		scenarios.add(new BigCrack(plugin));
		scenarios.add(new BiomeParanoia(plugin, spec));
		scenarios.add(new Birds());
		scenarios.add(new Blitz(timer, game));
		scenarios.add(new Blocked());
		scenarios.add(new BlockRush());
		scenarios.add(new BloodCycle(plugin));
		scenarios.add(new BloodDiamonds());
		scenarios.add(new BloodEnchants());
		scenarios.add(new BloodLapis());
		scenarios.add(new Bombers());
		scenarios.add(new BowFighters());
		scenarios.add(new Captains(plugin, teams, spec));
		scenarios.add(new CarrotCombo());
		scenarios.add(new CatsEyes());
		scenarios.add(new Chicken());
		scenarios.add(new ChunkApocalypse(plugin));
		scenarios.add(new Cloud9(plugin, game));
		scenarios.add(new Cobblehaters());
		scenarios.add(new Coco(plugin));
		scenarios.add(new Compensation(arena, teams));
		scenarios.add(new Cripple());
		scenarios.add(new Cryophobia(plugin, game));
		scenarios.add(cc);
		scenarios.add(new DamageCycle(plugin));
		scenarios.add(new Depths());
		scenarios.add(new Diamondless(cc));
		scenarios.add(new DragonRush(spec));
		scenarios.add(new Eggs());
		scenarios.add(new EightLeggedFreaks(game));
		scenarios.add(new EnchantedDeath());
//		/* TODO: Enchanted Books */ scenarios.add(new EnchantParanoia());
		scenarios.add(new Entropy(plugin, game));
		scenarios.add(new Fallout(plugin, game));
		scenarios.add(new FlowerPower());
		scenarios.add(new Genie());
		scenarios.add(new Goldless(cc));
		scenarios.add(new GoldRush());
		scenarios.add(new GoneFishing());
		scenarios.add(new GoToHell(plugin, settings, feat));
		scenarios.add(new Halloween(plugin, game));
		scenarios.add(new HundredHearts());
		scenarios.add(new InfiniteEnchanter());
		scenarios.add(new Inventors());
		scenarios.add(new Kings());
		scenarios.add(new Krenzinator());
		scenarios.add(new LAFS());
		scenarios.add(new Landmines());
		scenarios.add(new Lootcrates());
		scenarios.add(new MeleeFun());
		scenarios.add(new Moles());
		scenarios.add(new MonstersInc());
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
		scenarios.add(new SharedHealth(plugin, null));
		scenarios.add(new SkyClean());
		scenarios.add(new Skyhigh());
		scenarios.add(new SlaveMarket());
		scenarios.add(new SlimyCrack());
		scenarios.add(new Snowday());
		scenarios.add(new Superheroes());
		scenarios.add(new Switcheroo());
		scenarios.add(new TeamHealth());
		scenarios.add(new Timber());
		scenarios.add(new Timebomb());
		scenarios.add(new TrainingRabbits());
		scenarios.add(new TripleArrows());
		scenarios.add(new TripleOres());
		scenarios.add(new VengefulSpirits());
		scenarios.add(new Voidscape());
		
		plugin.getLogger().info("All scenarios has been setup.");
	}
}