package me.hammale.Spyer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet29DestroyEntity;

import me.hammale.Spyer.PacketHand;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.getspout.spoutapi.SpoutManager;

public class SpyerPlayerListener extends PlayerListener {

	  public SpyerMain plugin;
	  public FileConfiguration config;
	  public HashMap<String, ArrayList<String>> playerHideTree = new HashMap<String, ArrayList<String>>();
	  public HashSet<String> commonPlayers = new HashSet<String>(); // Possibly make into ArrayList...
	  public final HashMap<String, Integer> schedulers = new HashMap<String, Integer>();	  
	  public HashMap<String, BukkitTimer> timers = new HashMap<String, BukkitTimer>();
	  public ArrayList<String> players = new ArrayList<String>();
//	  
//	  public SpyerPlayerListener(SpyerMain plugin)
//	  {
//	    this.plugin = plugin;
//	  }
	  
	  public SpyerPlayerListener(SpyerMain _plug) {
		  this.plugin = _plug;
		  SpoutManager.getPacketManager().addListener(5, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(17, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(18, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(19, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(20, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(28, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(30, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(31, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(32, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(33, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(34, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(38, new PacketHand(this));
		  SpoutManager.getPacketManager().addListener(39, new PacketHand(this));
		  }
	  

		public void onPlayerPickupItem(PlayerPickupItemEvent e)
		{
			Player p = e.getPlayer();
			if(plugin.spying.contains(p.getName())){
				e.setCancelled(true);
			}
		}

	  public void onPlayerJoin(PlayerJoinEvent e) {
		  Player p = e.getPlayer();
		  if ((plugin.spying.contains(p.getName()))) {
			  e.setJoinMessage(null);
			  p.sendMessage(ChatColor.GOLD + "Your current Spyer status is: " + ChatColor.GREEN + "invisable");
		  }else{
			  p.sendMessage(ChatColor.GOLD + "Your current Spyer status is: " + ChatColor.RED + "visable");
		  }
 }
	  
	  
	  	public boolean continueSend(Player player, String name) {
	  	if (player == null) {
	  		return true;
	  	}
	  		boolean canSee = plugin.getSettings().isSeeAll(player.getName());
	  		boolean isHidden = commonPlayers.contains(name);
	  		//return (canSee || !isHidden);
	  		return true;
	  	}
	  
	  public boolean outsideSight(Location loc1, Location loc2) {
		  World w1 = loc1.getWorld();
		  World w2 = loc2.getWorld();
		  if (!w1.getName().equals(w2.getName())) {
		  // We don't need to hide people from different worlds! Woohoo, multiworld friendly!
		  return false;
		  }
		  Chunk chG = w2.getChunkAt(loc2.getBlock());
		  Chunk ch = w1.getChunkAt(loc1.getBlock());
		  int maxX = chG.getX() + 16; // Just making sure nobody will still be
		  // visible
		  int minX = chG.getX() - 16; // TODO: tweak the numbers
		  int maxZ = chG.getZ() + 16;
		  int minZ = chG.getZ() - 16;
		  if ((ch.getX() <= maxX || ch.getX() >= minX)
		  || (ch.getZ() <= maxZ || ch.getZ() >= minZ)) {
		  return false;
		  } else {
		  return true;
		  }
		  }
	  
	  public void invisible(Player p1, Player p2) {
		  if (outsideSight(p1.getLocation(), p2.getLocation())) {
		  return;
		  }
		  CraftPlayer hide = (CraftPlayer) p1;
		  CraftPlayer hideFrom = (CraftPlayer) p2;

		  if (!playerHideTree.containsKey(p1.getName())) {
		  playerHideTree.put(p1.getName(), new ArrayList<String>());
		  }
		  //if ((!playerHideTree.get(p1.getName()).contains(p2.getName()) || force)){
		  //.&& !plugin.getSettings().isSeeAll(p2.getName())) {
		  if (p1 != p2) {
		  try {
		  hideFrom.getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(hide.getEntityId()));
		  playerHideTree.get(p1.getName()).add(p2.getName());
		  } catch (Exception e) {
		  // Why would I care about some networking exceptions? Ha ha ha...
		  //}
		  }
		  }

		  }

		  private void uninvisible(Player p1, Player p2) {
		  CraftPlayer unHide = (CraftPlayer) p1;
		  CraftPlayer unHideFrom = (CraftPlayer) p2;
		  if (p1 != p2 && playerHideTree.containsKey(p1.getName())) {
		  if (playerHideTree.get(p1.getName()).contains(p2.getName())) {
		  unHideFrom.getHandle().netServerHandler
		  .sendPacket(new Packet20NamedEntitySpawn(unHide
		  .getHandle()));
		  playerHideTree.get(p1.getName()).remove(p2.getName());
		  }
		  }
		  }


		  public void cleanuptimers(Player player) {
		  //if (timers.containsKey(player.getName())) {
			//timers.get(player.getName()).cancel();
		  	//timers.remove(player.getName());
		  //}
		  if (schedulers.containsKey(player.getName())) {
			  plugin.getServer().getScheduler()
		  	.cancelTask(schedulers.get(player.getName()));
		  	schedulers.remove(player.getName());
		  }
		  }
		  
		  public void reappear(Player player) {
			  if (!commonPlayers.contains(player.getName())) {
				  return;
			  }
			  commonPlayers.remove(player.getName());
			  cleanuptimers(player);
			  Player[] playerList = plugin.getServer().getOnlinePlayers();
			  for (Player p : playerList) {
			  if (!p.getName().equals(player.getName())) {
			  uninvisible(player, p);
			  }
			  }
			  playerHideTree.remove(player.getName());
			  player.sendMessage(ChatColor.RED + player.getName() + " is now visible!");
//			  try {
//				  ind.indicate(player, false);
//			  } catch (FileNotFoundException e) {
//				  e.printStackTrace();
//			  } catch (IOException e) {
//				  e.printStackTrace();
//			  }
		}
		  
		  public void vanish(final Player player) {
		  final String name = player.getName();
		  if (commonPlayers.contains(player.getName())) {
		  return;
		  }
		  commonPlayers.add(player.getName());
		  schedulers.put(player.getName(), plugin.getServer().getScheduler()
		  .scheduleAsyncRepeatingTask(plugin, new Runnable() {
		  @Override
		  public void run() {
		  try {
		  if (!player.isOnline()) {
		  plugin.getServer().getScheduler()
		  .cancelTask(schedulers.get(name));
		  schedulers.remove(name);
		  commonPlayers.remove(player.getName());
		  playerHideTree.remove(player.getName());
		  return;
		  }
		  //try {
		  //ind.indicate(player, true);
		  //} catch (FileNotFoundException e) {
		  // TODO Auto-generated catch block
		  //e.printStackTrace();
		  //} 
//		  catch (IOException e) {
//		  // TODO Auto-generated catch block
//		  e.printStackTrace();
//		  }
		  } catch (Exception e) {
		  }
		  }
		  }, 0, plugin.getRate(player.getWorld()))); //TODO: REPLACE 1000 W/ REFRESH RATE!
		  // }
		  Player[] playerList = plugin.getServer().getOnlinePlayers();
		  for (Player p : playerList) {
		  invisible(player, p);
		  }
		  player.sendMessage(ChatColor.RED + player.getName() + " is now invisible (burning no items)!");
		  }


		  public void vanish1(final Player player) {		  	  
		  
		  commonPlayers.add(player.getName());
		  final BukkitTimer plt = new BukkitTimer(plugin);
		  TimerTask tsk = new TimerTask() {
		  @Override
		  public void run() {

		  try {
//		  System.out.println(player.getItemInHand().getAmount());
		  if (!player.isOnline()) {
			  plt.cancel();
			  timers.remove(player.getName());
			  commonPlayers.remove(player.getName());
			  playerHideTree.remove(player.getName());
			  return;
		  }
		  
		  Player[] playerList = plugin.getServer().getOnlinePlayers();
		  for (Player p : playerList) {
			  invisible(player, p);
		  }
		  if (!(hasItem(player)) || (!(plugin.spying.contains(player.getName())))) {
			  reappear(player);
			  commonPlayers.remove(player.getName());
			  playerHideTree.remove(player.getName());
			  timers.get(player.getName()).cancel();
			  timers.remove(player.getName());
			  plt.cancel();
			  plugin.spying.remove((player).getName());
			  return;
		  }

		  if (hasItem(player)) {
			  removeItem(player);
		  }

		  } catch (Exception e) {
		  }
		  }
		  };
		  plt.scheduleAtFixedRate(tsk, 0, plugin.getRate(player.getWorld()));
		  timers.put(player.getName(), plt);
		  Player[] playerList = plugin.getServer().getOnlinePlayers();
		  for (Player p : playerList) {
			  invisible(player, p);
		  }
		  World w = player.getWorld();
		  player.sendMessage(ChatColor.RED + player.getName() + " is now invisible burning: " + ChatColor.DARK_AQUA + plugin.getItem(w));
	  }
		  
		  public boolean hasItem(Player player) {
			  World w = player.getWorld();
			  final int id = plugin.getItem(w);
			  Inventory inv = player.getInventory();
			  if(inv.contains(id, 2)){
				  return true;
			  }
			  if(inv.contains(id)){
				  int sec = plugin.getRate(w)/20;
				  player.sendMessage(ChatColor.RED + "WARNING! You have " + sec + " seconds of invisibility left!");
				  return true;
			  }else{
				  return false;
			  }
		  }
		  
		  public void removeItem(Player player) {
			  World w = player.getWorld();
			  final int id = plugin.getItem(w);
			  Inventory inv = player.getInventory();
			  Material m = Material.getMaterial(id);
			  inv.removeItem(new ItemStack (m, 1));
		  }
		  
		  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		    	String[] split = event.getMessage().split(" ");
		    	if (split.length < 1) return;
		    	Player pla = event.getPlayer();
		    	String cmd = split[0].trim().substring(1).toLowerCase();
				  if (cmd.equalsIgnoreCase("list") || cmd.equalsIgnoreCase("playerlist") || cmd.equalsIgnoreCase("who") || cmd.equalsIgnoreCase("online") || cmd.equalsIgnoreCase("players")) {
					  event.setCancelled(true);
					  plugin.displayMessage(pla);
				  }  
				  
		    }
		  public String getPlayers(){
			     String a = "";
			     for(String names:players)
			     {
			      a = a + names + ", ";
			     }
			     players.clear();
			     return a; 
		  }
		  
}
