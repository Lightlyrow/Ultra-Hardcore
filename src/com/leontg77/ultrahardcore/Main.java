package com.leontg77.ultrahardcore;

import java.util.TimeZone;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.leontg77.ultrahardcore.commands.CommandHandler;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.feature.portal.NetherFeature;
import com.leontg77.ultrahardcore.feature.pvp.StalkingFeature;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.listeners.ChatListener;
import com.leontg77.ultrahardcore.listeners.LoginListener;
import com.leontg77.ultrahardcore.listeners.LogoutListener;
import com.leontg77.ultrahardcore.listeners.PlayerListener;
import com.leontg77.ultrahardcore.listeners.ProtectionListener;
import com.leontg77.ultrahardcore.listeners.SpectatorListener;
import com.leontg77.ultrahardcore.listeners.StatsListener;
import com.leontg77.ultrahardcore.listeners.WorldListener;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.FireworkManager;
import com.leontg77.ultrahardcore.managers.HOFManager;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.protocol.EnchantPreview;
import com.leontg77.ultrahardcore.protocol.HardcoreHearts;
import com.leontg77.ultrahardcore.protocol.OnlineCount;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.ubl.UBL;
import com.leontg77.ultrahardcore.ubl.UBLListener;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.world.WorldManager;
import com.leontg77.ultrahardcore.world.antistripmine.AntiStripmine;
import com.leontg77.ultrahardcore.world.biomeswap.BiomeSwap;

/**
 * Main class of the UHC plugin.
 * <p>
 * This class contains methods for prefixes, adding recipes, enabling and disabling.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("unused")
public class Main extends JavaPlugin {
	private final Settings settings;
	private final Data data;

	private final AntiStripmine antiSM;
	private final BiomeSwap swap;
	
	private final WorldManager worlds;
	private final UBL ubl;
	
	private final BoardManager board;
	private final TeamManager teams;
	
	private final ScatterManager scatter;
	private final SpecManager spec;
	
	private final PermissionsManager perm;
	private final FireworkManager firework;
	
	private final HOFManager HOF;
	private final GUIManager gui;

	private final Game game;
	private final Arena arena;
	
	private final Announcer announcer;
	private final Parkour parkour;

	private final EnchantPreview enchPreview;
	private final HardcoreHearts hardHearts;
	private final OnlineCount counter;

	private final CommandHandler cmd;
	private final ScenarioManager scen;
	private final FeatureManager feat;
	
	private final Timer timer;
	
	public Main() {
		settings = new Settings(this);
		data = new Data(this, settings);	
		
		worlds = new WorldManager(settings);	
		ubl = new UBL(this);
		
		antiSM = new AntiStripmine(this);
		swap = new BiomeSwap(this, settings);

		board = new BoardManager(this);	
		teams = new TeamManager(this, board);
		
		perm = new PermissionsManager(this);	
		firework = new FireworkManager(this);
		
		HOF = new HOFManager(this);	
		gui = new GUIManager(this);
		
		parkour = new Parkour(this, settings);
		
		spec = new SpecManager(this, teams);
		game = new Game(settings, gui, board, spec);
		
		scatter = new ScatterManager(this, teams, game);	

		arena = new Arena(this, game, board, scatter, worlds);
		announcer = new Announcer(this, game);

		enchPreview = new EnchantPreview(this);
		hardHearts = new HardcoreHearts(this);
		counter = new OnlineCount(this);

		scen = new ScenarioManager(this);
		feat = new FeatureManager(this, settings);
		
		cmd = new CommandHandler(this);
		
		timer = new Timer(this, game, gui, scen, feat.getFeature(StalkingFeature.class), board, spec);
	}
	
	public static final String NO_PERMISSION_MESSAGE = "§cYou don't have permission.";
 
	public static final String BORDER_PREFIX = "§aBorder §8» §7";
	public static final String ALERT_PREFIX = "§6Alert §8» §7";
	public static final String STAFF_PREFIX = "§cStaff §8» §7";
	public static final String SPEC_PREFIX = "§5Spec §8» §7";
	public static final String INFO_PREFIX = "§bInfo §8» §7";
	public static final String SCEN_PREFIX = "§9Scenario §8» §7";
	public static final String PREFIX = "§4UHC §8» §7";
	
	@Override
	public void onDisable() {
		PluginDescriptionFile file = getDescription();
		getLogger().info(file.getName() + " is now disabled.");
		
		swap.resetBiomes();
		data.store(teams, scen);
		
		if (!game.teamManagement()) {
			return;
		}
		
		game.setTeamManagement(false);

		if (!game.pregameBoard()) {
			return;
		}
		
		board.resetScore("§e ");
		board.resetScore("§8» §cTeam:");
		board.resetScore("§8» §7/team");
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile file = getDescription();
		getLogger().info(file.getName() + " v" + file.getVersion() + " is now enabled.");
		getLogger().info("The plugin was created by LeonTG77.");

		game.setTimer(timer);
		
		State.setSettings(settings);
//		User.setupInstances(this, game, gui, perm);
		
//		counter.enable();
		
		settings.setup();
		ubl.reload();
		
		board.setup(game);
		teams.setup();

		scen.registerScenarios(arena, game, timer, teams, spec, settings, feat, scatter, board);
		feat.registerFeatures(arena, game, timer, board, teams, spec, enchPreview, hardHearts, scen);
		cmd.registerCommands(game, arena, parkour, settings, gui, board, spec, feat, scen, worlds, timer, teams, firework);
	    
		worlds.loadWorlds();
		swap.setup();

		announcer.setup();
		arena.setup();
		parkour.setup();
		
		FileUtils.updateUserFiles(this);
		
		gui.registerGUIs(game, timer, settings, feat, scen);
		data.restore(teams, scen);
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		PluginManager manager = Bukkit.getPluginManager(); 
		
		// register all listeners.
		manager.registerEvents(new ChatListener(game, teams, spec), this);
		manager.registerEvents(new LoginListener(this, game, settings, spec, scatter, perm), this);
		manager.registerEvents(new LogoutListener(this, game, gui, spec, perm), this);
		manager.registerEvents(new PlayerListener(spec), this);
		manager.registerEvents(new ProtectionListener(game), this);
		manager.registerEvents(new SpectatorListener(game, spec, gui, feat.getFeature(NetherFeature.class)), this);
		manager.registerEvents(new StatsListener(this, arena, game, board, teams, feat.getFeature(GoldenHeadsFeature.class)), this);
		manager.registerEvents(new WorldListener(arena), this);
		manager.registerEvents(new UBLListener(ubl), this);
		
		for (Player online : Bukkit.getOnlinePlayers()) {	
			perm.addPermissions(online);
		}
		
		switch (State.getState()) {
		case NOT_RUNNING:
			FileUtils.deletePlayerDataAndStats(this);
			Bukkit.setIdleTimeout(60);
			break;
		case INGAME:
			manager.registerEvents(spec.getSpecInfo(), this);
			Bukkit.setIdleTimeout(10);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Gets the servers tps.
	 * 
	 * @return The servers tps.
	 */
	public double getTps() {
		double tps = MinecraftServer.getServer().recentTps[0];
		String converted = NumberUtils.formatDouble(tps);
		
		return Double.parseDouble(converted);
	}
	
	/**
	 * Get the amount of online players minus the spectators.
	 * 
	 * @return The online count.
	 */
	public int getOnlineCount() {
		int players = Bukkit.getOnlinePlayers().size();
		int specs = 0;
		
		for (String spec : spec.getSpectators()) {
			if (Bukkit.getPlayer(spec) != null) {
				specs++;
			}
		}
		
		players = players - specs;
		
		return players;
	}
	
	/**
	 * Get the spawnpoint of the lobby.
	 * 
	 * @return The lobby spawnpoint.
	 */
	public Location getSpawn() {
		final FileConfiguration data = settings.getData();
		
		World world = Bukkit.getWorld(data.getString("spawn.world", "lobby"));
		
		if (world == null) {
			world = Bukkit.getWorlds().get(0);
		}
		
		double x = data.getDouble("spawn.x", 0.5);
		double y = data.getDouble("spawn.y", 33.0);
		double z = data.getDouble("spawn.z", 0.5);
		float yaw = (float) data.getDouble("spawn.yaw", 0);
		float pitch = (float) data.getDouble("spawn.pitch", 0);
		
		Location loc = new Location(world, x, y, z, yaw, pitch);
		return loc;
	}
}