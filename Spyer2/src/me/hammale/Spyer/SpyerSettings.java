package me.hammale.Spyer;

import java.util.HashMap;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class SpyerSettings extends HashMap<String, Object>
{
  private HashMap<String, booleanAndInt> seeAllCache = new HashMap();
  public final SpyerMain plugin;
  private long step = 8000L;

  public SpyerSettings(SpyerMain plugin)
  {
    this.plugin = plugin;
  }

  public boolean hasPerms(Player player, String name) {
    if (player == null) {
      return false;
    }
    boolean flag = player.hasPermission(new Permission(name, PermissionDefault.OP));
    return flag;
  }

  public boolean isSeeAll(String name)
  {
    boolean ret = false;
    if (this.seeAllCache.containsKey(name)) {
      booleanAndInt b = (booleanAndInt)this.seeAllCache.get(name);
      if (System.currentTimeMillis() - b.time <= this.step) {
        ret = b.result;
      } else {
        ret = this.plugin.getServer().getPlayer(name).hasPermission("spyer.admin");
        ((booleanAndInt)this.seeAllCache.get(name)).time = System.currentTimeMillis();
        ((booleanAndInt)this.seeAllCache.get(name)).result = ret;
      }
    } else {
      ret = this.plugin.getServer().getPlayer(name).hasPermission("spyer.admin");
      booleanAndInt tmp = new booleanAndInt();
      tmp.time = System.currentTimeMillis();
      tmp.result = ret;
      this.seeAllCache.put(name, tmp);
    }
    return ret;
  }

  class booleanAndInt
  {
    long time;
    boolean result;
    booleanAndInt()
    {
    }
  }
}