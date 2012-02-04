package me.hammale.Spyer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

//import nickguletskii200.SpyerAdminShared.ICustomHandling;
//import nickguletskii200.SpyerAdminShared.IMainGetters;
//import nickguletskii200.SpyerAdminShared.ISpyerAdmin;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
* The main class.
*
* @author hammale
*/
public class SpyerMain extends JavaPlugin {
	//INVISABLE URL: https://github.com/hammale/Spyer/blob/master/SpyerAdmin/src/nickguletskii200/SpyerAdmin/SpyerAdminPlayerListener.java
	Logger log;
	//private final SpyerPlayerListener playerListener;
	private SpyerSettings ss;
	private SpyerPlayerListener plistener = new SpyerPlayerListener(this);
	public HashSet<String> spying = new HashSet<String>();
	public ArrayList<String> players = new ArrayList<String>();
	public FileConfiguration config;
	
//	public SpyerAdmin() {
//		super();
//		playerListener = new SpyerPlayerListener(this);
//		ml = new MobListener(this);
//		ss = (new SpyerSettings(this));
//		}

	@Override
	public void onEnable() {
		log = this.getServer().getLogger();
	  	loadConfiguration();
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("Spyer Version: " + pdfFile.getVersion() + " Enabled!");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, plistener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, plistener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, plistener, Event.Priority.Normal, this);
//		pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.playerListener, Priority.Monitor, this);
//		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Priority.Monitor, this);
//		//pm.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, getSettings().chatPriority, this);
//		pm.registerEvent(Event.Type.PLAYER_TELEPORT, this.playerListener,
//		Priority.Monitor, this);
//		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener,
//		Priority.Monitor, this);
	}
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		//for(String s: spying){
			Player p = getServer().getPlayer("hammale");
			p.sendMessage(ChatColor.RED + "You have been forced visable by server!");
		//}
		log.info("Spyer Version: " + pdfFile.getVersion() + " Disabled!");
		
	}
	
	public void loadConfiguration(){
	    //We must initialize the config
	    config = getConfig();
	    config.options().copyDefaults(true); 
	    
	    for(World w : this.getServer().getWorlds()) {
	    String wrld = w.getName();
	    
	    String path = "World." + wrld + "." + "ItemBurning.Item";
	    String path1 = "World." + wrld + "." + "ItemBurning.RateInTicks";
	    
	    config.addDefault(path, 266);
	    config.addDefault(path1, 1000);
	    
	    }
	    config.options().copyDefaults(true);  
	    saveConfig();
	}
	public int getItem(World w){
	    config = getConfig();
	    String wrld = w.getName();
	    int amnt = config.getInt("World." + wrld + "." + "ItemBurning.Item"); 
	    return amnt;
	}
	public int getRate(World w){
	    config = getConfig();
	    String wrld = w.getName();
	    int amnt = config.getInt("World." + wrld + "." + "ItemBurning.RateInTicks"); 
	    return amnt;
	}	
	  @Override
	  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	  {
		  PluginDescriptionFile pdfFile = this.getDescription();
		  String s = null;
		  if(cmd.getName().equalsIgnoreCase("spy") && args.length == 0){
			  return false;
		  }
		  if (cmd.getName().equalsIgnoreCase("spy")) {
		  if ((sender instanceof Player)) {	  
			  Player player = (Player) sender;  
		  if(args.length == 1 && args.length != 2) {
			  
			  if (args[0].equalsIgnoreCase("off")) {
				  if (player.hasPermission("spyer.user") == true || player.hasPermission("spy.admin") == true || player.hasPermission("spy.noitem") == true || player.isOp()){					  
					  spying.remove((player).getName());
					  plistener.reappear(player);
					  return true;
				  }
			  }
			  if (args[0].equalsIgnoreCase("on")) {
				  if (player.hasPermission("spyer.user") == true || player.hasPermission("spy.admin") == true || player.hasPermission("spy.noitem") == true || player.isOp()){
				  plistener.vanish1(player);
				  spying.add((player).getName());
				  return true;
				  }
			  }
			  			  
			  if (args[0].equalsIgnoreCase("status")) {
				  ChatColor color = ChatColor.RED;
				  boolean status = spying.contains(player.getName());
				  String stat = "visable";
				  if(status == true){
					 color = ChatColor.GREEN;
					 stat = "invisable"; 					  
				  }
				  if(player.hasPermission("spyer.admin") == true || player.isOp()){
					  player.sendMessage(ChatColor.GOLD + "Current status for " + ChatColor.DARK_AQUA + player.getName() + ": " + color  + stat);
				  }
				  return true;
			  }
			  if (args[0].equalsIgnoreCase("reload")) {
				  if(player.hasPermission("spyer.admin") == true || player.isOp()){
			        reloadConfig();
			        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Spyer config reloaded!");
			        return true;
				  }
			  }
			  
			  if (args[0].equalsIgnoreCase("info")) {
				  if(player.hasPermission("spyer.admin") == true || player.isOp()){
			        sender.sendMessage(ChatColor.GOLD + "<---SPYER VERSION: " + pdfFile.getVersion() + "--->");
			        sender.sendMessage(ChatColor.DARK_AQUA + "Developed by --" + ChatColor.WHITE + " hammale & nickguletskii");				  
			        return true;
				  }
			  }
			  
			  if (args[0].equalsIgnoreCase("logout")) {
			        sender.sendMessage(ChatColor.YELLOW + player.getName() + " left the game");	   
			        return true;			  
			  }
			  
			  if (args[0].equalsIgnoreCase("login")) {
			        sender.sendMessage(ChatColor.YELLOW + player.getName() + " joined the game");	  
			        return true;				  
			  }
			  
			  if (args[0].equalsIgnoreCase("help")) {
				  if(player.hasPermission("spyer.user") == true || player.hasPermission("spyer.admin") == true || player.hasPermission("spyer.noitem") == true || player.isOp()){			        
			        sender.sendMessage(ChatColor.GOLD+ "<---SPYER VERSION: " + pdfFile.getVersion() + "--->");
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy <off/on> --" + ChatColor.WHITE + " Make yourself (in)visable");
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy status --" + ChatColor.WHITE + " Check you Spyer ststus");
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy <logout/login> --" + ChatColor.WHITE + " Fake a login/logout");		       
			        if(player.hasPermission("spyer.admin") == true || player.hasPermission("spyer.noitem") == true || player.isOp()){
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin unlimited <off/on> --" + ChatColor.WHITE + " Make yourself (in)visable WITHOUT losing items");
			        }
			        if(player.hasPermission("spyer.admin") == true || player.isOp()){
			        sender.sendMessage(ChatColor.GOLD + "<---SPYER ADMIN--->");
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin all unlimited <off/on> --" + ChatColor.WHITE + " Make EVERYONE online (in)visable WITHOUT losing items");		       
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin <player> <off/on> --" + ChatColor.WHITE + " Make <player> (in)visable");
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin <player> unlimited <off/on> --" + ChatColor.WHITE + " Make <player> (in)visable WITHOUT losing items");				      
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin all <off/on> --" + ChatColor.WHITE + " Make EVERYONE online (in)visable");
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin <player> <logout/login> --" + ChatColor.WHITE + " Fake a login/logout for <player>");
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy admin status <player> --" + ChatColor.WHITE + " Check <player>'s Spyer ststus");
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy reload --" + ChatColor.WHITE + " Reloads the Spyer config file");
			        sender.sendMessage(ChatColor.DARK_AQUA + "/spy info --" + ChatColor.WHITE + " Spyer version information");
			        }
			        return true;
				  }else{
					sender.sendMessage(ChatColor.RED + "You ain't got no perms for this!");
				  }
			  }
		}
		  if (args[0].equalsIgnoreCase("admin")){
			  if(args.length == 1 && args.length != 2) {
				  return false;
			  }
			  if(args.length == 2 && args[1].equalsIgnoreCase("unlimited")) {
				  return false;
			  }
		if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("status")) {
			  ChatColor color = ChatColor.RED;
			  boolean status = spying.contains(args[2]);
			  String stat = "visable";
			  if(status == true){
				 color = ChatColor.GREEN;
				 stat = "invisable";
			  }
			  if(player.hasPermission("spyer.admin") == true || player.isOp()){
				  player.sendMessage(ChatColor.GOLD + "Current status for " + ChatColor.DARK_AQUA + args[2] + ": " + color + stat);
				  return true;
			  }
			  return false;
		  }
		
			  if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("unlimited") && args[2].equalsIgnoreCase("off")) {
				  if (player.hasPermission("spyer.admin") || player.hasPermission("spyer.noitem") || player.isOp()){					  
					  plistener.reappear(player);
					  spying.remove((player).getName());
					  return true;
				  }
				  return false;
			  }
			  if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("unlimited") && args[2].equalsIgnoreCase("on")) {
				  if (player.hasPermission("spyer.noitem") || player.hasPermission("spyer.admin") || player.isOp()){
					  plistener.vanish(player);
					  spying.add((player).getName());
					  return true;
				  }
				  return false;
			  }
			  		  
			  if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("off")) {
				  if(!(args[1].equals("all"))){
				  Player p = this.getServer().getPlayer(args[1]);
				  if(p.isOnline()){
				  if (player.hasPermission("spyer.admin") || player.isOp()){
					  spying.remove(p.getName());
					  plistener.reappear(p);
					  return true;
				  }
				  return false;
				  }else{
				  player.sendMessage(ChatColor.RED + p.getName() + " is not online!");
				  return true;
				  }
				  }
				  return false;
			  }

			  if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("login")) {
				  if(!(args[1].equals("all"))){
				  if (player.hasPermission("spyer.admin") || player.isOp()){
				        sender.sendMessage(ChatColor.YELLOW + args[1] + " joined the game");
					  return true;
				  }
				  return false;
				  } 
				  return false;
			  }
			  
			  if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("logout")) {
				  if(!(args[1].equals("all"))){
				  if (player.hasPermission("spyer.admin") || player.isOp()){
				        sender.sendMessage(ChatColor.YELLOW + args[1] + " left the game");
					  return true;
				  }
				  return false;
				  } 
				  return false;
			  }
			  
			  if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("on")) {
				  Player p = getServer().getPlayer(args[1]);
				  if(!(args[1].equals("all"))){
				  if(p.isOnline()){
				  if (player.hasPermission("spyer.admin") || player.isOp()){
					  plistener.vanish1(p);
					  spying.add(p.getName());
					  return true;
				  }
				  return false;
				  }
				  }else{
					  player.sendMessage(ChatColor.RED + p.getName() + " is not online!");
					  return true;
				  }
				  return false;
			  }
			  
			  if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("unlimited") && args[3].equalsIgnoreCase("off")) {		  
				  Player p = this.getServer().getPlayer(args[1]);
				  if(p.isOnline()){
				  if (player.hasPermission("spyer.admin") || player.isOp()){
					  plistener.reappear(p);
					  spying.remove(p.getName());
					  return true;
				  }
				  return false;
			  }else{
				  player.sendMessage(ChatColor.RED + p.getName() + " is not online!");
				  return true;
			  }
			  }

			  if (args[0].equalsIgnoreCase("admin") && args[2].equalsIgnoreCase("unlimited") && args[3].equalsIgnoreCase("on")) {
				  Player p = this.getServer().getPlayer(args[1]);
				  if(p.isOnline()){
				  if (player.hasPermission("spyer.admin") || player.isOp()){
					  plistener.vanish(p);
					  spying.add(player.getName());
					  return true;
				  }
				  return false;
			  }else{
				  player.sendMessage(ChatColor.RED + p.getName() + " is not online!");
				  return true;
			  }
			  }
			  
			  if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("all") && args[2].equalsIgnoreCase("on")) {
				  if (player.hasPermission("spyer.admin") || player.isOp()){
				  Player[] players = getServer().getOnlinePlayers();				  
				  for(Player pl : players){					  
						plistener.vanish1(pl);
						spying.add(pl.getName());
				  }
					return true;
				  }
				  return false;
			  }
			  
			  if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("all") && args[2].equalsIgnoreCase("off")) {
				  if (player.hasPermission("spyer.admin") || player.isOp()){
				  Player[] players = getServer().getOnlinePlayers();				  
				  for(Player pl : players){
						spying.remove(pl.getName());
						plistener.reappear(pl);
				  }
					return true;
				  }
			  }
			  
			  if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("all") && args[2].equalsIgnoreCase("unlimited") && args[3].equalsIgnoreCase("on")) {
				  if (player.hasPermission("spyer.admin") || player.isOp()){
				  Player[] players = getServer().getOnlinePlayers();				  
				  for(Player pl : players){			  
						plistener.vanish(pl);
						spying.add(pl.getName());
				  }
					return true;
				  }
				  return false;
			  }
			  
			  if (args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("all") && args[2].equalsIgnoreCase("unlimited") && args[3].equalsIgnoreCase("off")) {
				  if (player.hasPermission("spyer.admin") || player.isOp()){
				  Player[] players = getServer().getOnlinePlayers();				  
				  for(Player pl : players){				  
						plistener.reappear(pl);
						spying.remove(pl.getName());
				  }	
					return true;
				  }
				  return false;
			  } 
			  }		
	  }else{
			log.info("Please run this command as a player not console!");
			return false;
		}
	}
		return false;
	}
	  public SpyerSettings getSettings() {
		  return ss;
	  }
	  public String getPlayers(){
		     String a = "";
		     for(String names:players)
		     {
		    	 if(!(spying.contains(names))){
		    		 a = a + names + ", ";
		    	 }
		     }
		     players.clear();
		     return a; 
	  }
	  public void displayMessage(Player pla){
		  int i = 0;
		  int o = 0;
			  for(Player p : getServer().getOnlinePlayers()) {
				  players.add(p.getName());
				  i++;
			  }
			  for(String str:spying){
				  o++;
			  }
			  int number = i-o;
			  pla.sendMessage(ChatColor.BLUE + "Players online " + ChatColor.RED + number + ChatColor.BLUE +" out of " + ChatColor.RED + getServer().getMaxPlayers());			  
			  pla.sendMessage(ChatColor.GREEN + getPlayers());  
	  }
}