package cn.endymx.multirobot;

import cn.endymx.multirobot.packer.ChatPacker;
import cn.endymx.multirobot.packer.InfoPacker;
import cn.endymx.multirobot.socket.SocketClient;

import lk.vexview.VexView;
import lk.vexview.api.VexViewAPI;
import lk.vexview.hud.VexImageShow;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author endymx @ VexRobot Project
 */
public class LoadClass extends JavaPlugin implements Listener{

    public SocketClient client;
    public boolean vv = false;
    public FileConfiguration config;

    public void onLoad(){
        saveDefaultConfig();
        config = getConfig();
    }

    public void onEnable() {
        getLogger().info("加载中...");
        getServer().getPluginManager().registerEvents(this, this);
        client = new SocketClient(config.getString("serverIP"), config.getInt("serverPort"), this);
        client.run();
        if(config.getBoolean("useVexView") && Bukkit.getPluginManager().getPlugin("VexView") != null && (VexView.getInstance().getVersion().startsWith("1.8") || VexView.getInstance().getVersion().startsWith("1.9"))) {
            vv = true;
            getLogger().info("检测到VexView插件，开启显示图片功能");
        }else{
            getLogger().info("未检测到VexView插件|VexView插件版本过低，已关闭显示图片功能");
        }
        getLogger().info("欢迎使用本插件，当前版本v1.1.0");
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

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName().toLowerCase()){
            case "getimage":
                if(vv) VexViewAPI.sendHUD((Player) sender, new VexImageShow(1, args[0], 80, 100, 80, 100, 50, 50, 3), config.getDouble("imageX"), config.getDouble("imageY"));
                break;
            case "reload":
                reloadConfig();
                config = getConfig();
                break;
        }
        return true;
    }
}
