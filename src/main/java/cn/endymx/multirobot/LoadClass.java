package cn.endymx.multirobot;

import cn.endymx.multirobot.packer.ChatPacker;
import cn.endymx.multirobot.packer.InfoPacker;
import cn.endymx.multirobot.socket.SocketClient;

import cn.endymx.multirobot.vexview.VexView;
import org.bukkit.Bukkit;
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

/**
 * @author endymx @ VexRobot Project
 */
public class LoadClass extends JavaPlugin implements Listener{

    public SocketClient client;
    public boolean vv = false;
    public FileConfiguration config;
    private int id = 0;
    public HashMap<Integer, String> url = new HashMap<>();

    public void onLoad(){
        saveDefaultConfig();
        config = getConfig();
    }

    public void onEnable() {
        getLogger().info("加载中...");
        getServer().getPluginManager().registerEvents(this, this);
        client = new SocketClient(config.getString("serverIP"), config.getInt("serverPort"), this);
        client.run();
        if(!config.getBoolean("useVexView")){
            getLogger().info("配置文件中已关闭显示图片功能");
        }else if(Bukkit.getPluginManager().getPlugin("VexView") != null && Bukkit.getPluginManager().getPlugin("VexView").isEnabled() && Double.parseDouble(VexView.getVersion().substring(0, 3) + VexView.getVersion().substring(4)) >= 2.0) {
            vv = true;
            //getServer().getPluginManager().registerEvents(new VexView(this), this);
            getLogger().info("检测到VexView插件，开启显示图片功能");
        }else{
            getLogger().info("未检测到VexView插件|VexView插件非最新版，已关闭显示图片功能");
        }
        getLogger().info("欢迎使用本插件，当前版本v1.2.0");
    }

    public void onDisable() {
        getLogger().info("关闭中...");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        client.clientManager.send(new ChatPacker(event));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        client.clientManager.send(new InfoPacker(event.getJoinMessage()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        client.clientManager.send(new InfoPacker(event.getQuitMessage()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        client.clientManager.send(new InfoPacker(event.getDeathMessage()));
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName().toLowerCase()){
            case "getimage":
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                if(vv) VexView.sendHUD((Player) sender, getId(args[0]), args[0], 100, 100, x, y, 3, config.getDouble("imageX"), config.getDouble("imageY"), args[3]);
                break;
            case "reload":
                reloadConfig();
                config = getConfig();
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
