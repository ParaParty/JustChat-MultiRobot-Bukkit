package cn.endymx.multirobot;

import cn.endymx.multirobot.api.robotAPI;
import cn.endymx.multirobot.packer.ChatPacker;
import cn.endymx.multirobot.packer.InfoPacker;
import cn.endymx.multirobot.socket.SocketClient;

import cn.endymx.multirobot.util.MessagePackType;
import cn.endymx.multirobot.vexview.VexView;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @author endymx @ VexRobot Project
 */
public class LoadClass extends JavaPlugin implements Listener{

    public SocketClient client;
    public boolean vv = false;
    public FileConfiguration config;
    public robotAPI api = null;
    public HashMap<String, Boolean> qq = new HashMap<>();
    private int id = 0;
    private final HashMap<Integer, String> url = new HashMap<>();

    public void onLoad(){
        saveDefaultConfig();
        config = getConfig();
    }

    public void onEnable() {
        getLogger().info("加载中...");
        api = new robotAPI(this);
        getServer().getPluginManager().registerEvents(this, this);
        client = new SocketClient(config.getString("serverIP"), config.getInt("serverPort"), this);
        client.start();
        if(!config.getBoolean("useVexView")){
            getLogger().info("配置文件中已关闭显示图片功能");
        }/*else if(Bukkit.getPluginManager().getPlugin("VexView") != null && Bukkit.getPluginManager().getPlugin("VexView").isEnabled() && Double.parseDouble(VexView.getVersion().substring(0, 3) + VexView.getVersion().substring(4)) >= 2.0) {
            vv = true;
            //getServer().getPluginManager().registerEvents(new VexView(this), this);
            getLogger().info("检测到VexView插件，开启显示图片功能");
        }else{
            getLogger().info("未检测到VexView插件|VexView插件非最新版，已关闭显示图片功能");
        }*/
        getLogger().info("欢迎使用本插件，当前版本v1.2.0");
    }

    public void onDisable() {
        getLogger().info("关闭中...");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        if (!event.getPlayer().hasPermission("multirobot.forward.chat")) return;
        client.clientManager.send(new ChatPacker(event));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if ( (!event.getPlayer().hasPermission("multirobot.forward.network.*")) &&
                (!event.getPlayer().hasPermission("multirobot.forward.network.join"))) return;

        if (config.getBoolean("forwardPlayersJoinAndDisconnectionMessages", true))
            client.clientManager.send(new InfoPacker(event.getPlayer().getName(), MessagePackType.INFO_Join, event.getJoinMessage()));
        else
            client.clientManager.send(new InfoPacker(event.getPlayer().getName(), MessagePackType.INFO_Join, null));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if ( (!event.getPlayer().hasPermission("multirobot.forward.network.*")) &&
                (!event.getPlayer().hasPermission("multirobot.forward.network.disconnect"))) return;

        if (config.getBoolean("forwardPlayersJoinAndQuitMessages", true))
            client.clientManager.send(new InfoPacker(event.getPlayer().getName(), MessagePackType.INFO_Quit, event.getQuitMessage()));
        else
            client.clientManager.send(new InfoPacker(event.getPlayer().getName(), MessagePackType.INFO_Quit, null));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if (!event.getEntity().getPlayer().hasPermission("multirobot.forward.death")) return;

        if (config.getBoolean("forwardPlayersDeadOriginalMessages", true))
            client.clientManager.send(new InfoPacker(event.getEntity().getPlayer().getName(), MessagePackType.INFO_Death, event.getDeathMessage()));
        else
            client.clientManager.send(new InfoPacker(event.getEntity().getPlayer().getName(), MessagePackType.INFO_Death, null));
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName().toLowerCase()){
            case "getimage":
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                if(vv) VexView.sendHUD((Player) sender, getId(args[0]), args[0], 100, 100, x, y, 3, config.getDouble("imageX"), config.getDouble("imageY"), args[3]);
                break;
            case "robot":
                if(sender.hasPermission("multirobot.admin")){
                    if(args[0].equals("on")){
                        if(client.clientManager.isConnect()){
                            sender.sendMessage(Color.RED + "QQ聊天处于连接状态");
                            return true;
                        }
                        client.start();
                        sender.sendMessage(Color.YELLOW + "打开QQ聊天连接");
                    }else{
                        if(!client.clientManager.isConnect()){
                            sender.sendMessage(Color.RED + "QQ聊天处于未连接状态");
                            return true;
                        }
                        client.clientManager.disconnect();
                        sender.sendMessage(Color.YELLOW + "关闭QQ聊天连接");
                    }
                }else{
                    sender.sendMessage(Color.RED + "无使用权限");
                }
                break;
            case "info":
                if(!client.clientManager.isConnect()){
                    sender.sendMessage(Color.RED + "连接未打开");
                    return true;
                }
                if(sender.hasPermission("multirobot.admin")){
                    Set<Player> p = new HashSet<>();
                    client.clientManager.send(new ChatPacker(new AsyncPlayerChatEvent(true, (Player) sender, args[0], p)));
                }else{
                    sender.sendMessage(Color.RED + "无使用权限");
                }
                break;
            case "qq":
                if(sender instanceof Player){
                    String name = ((Player)sender).getName();

                    if(!qq.get(name)){
                        qq.put(name, true);
                        sender.sendMessage(Color.YELLOW + "解除QQ聊天屏蔽");
                    }else if(qq.get(name) || qq.get(name) == null){
                        qq.put(name, false);
                        sender.sendMessage(Color.YELLOW + "打开QQ聊天屏蔽");
                    }
                }
                break;
            case "reload":
                sender.sendMessage(Color.YELLOW + "更新配置文件并重启通讯中...");
                reloadConfig();
                config = getConfig();
                if(client.clientManager.isConnect()){
                    client.clientManager.disconnect();
                }
                client = new SocketClient(config.getString("serverIP"), config.getInt("serverPort"), this);
                client.start();
                sender.sendMessage(Color.GREEN + "重启完毕，等待连接到服务器");
                break;
        }
        return true;
    }

    private int getId(String url){
        id ++;
        this.url.put(id, url);
        if(id > 100){
            id = 1;
        }
        return id;
    }
}