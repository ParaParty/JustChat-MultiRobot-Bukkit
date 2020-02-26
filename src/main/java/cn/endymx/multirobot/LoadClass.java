package cn.endymx.multirobot;

import cn.endymx.multirobot.api.robotAPI;
import cn.endymx.multirobot.packer.ChatPacker;
import cn.endymx.multirobot.packer.InfoPacker;
import cn.endymx.multirobot.socket.SocketClient;

import cn.endymx.multirobot.util.MessagePackType;
import cn.endymx.multirobot.vexview.VexView;
import org.bukkit.Bukkit;
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

/**
 * @author endymx @ VexRobot Project
 */
public class LoadClass extends JavaPlugin implements Listener{

    public SocketClient client;
    public boolean vv = false;
    public FileConfiguration config;
    public robotAPI api = null;
    private int id = 0;
    private HashMap<Integer, String> url = new HashMap<>();

    public void onLoad(){
        saveDefaultConfig();
        config = getConfig();
    }

    public void onEnable() {
        getLogger().info("加载中...");
        api = new robotAPI(this);
        getServer().getPluginManager().registerEvents(this, this);
        client = new SocketClient(config.getString("serverIP"), config.getInt("serverPort"), this);
        client.run();
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
        //TODO 检查权限 multirobot.forward.chat
        //TODO 检查该用户是否关闭了转发聊天消息功能 (指令开关)
        //TODO 新增 /一个命令 内容 命令来临时发送消息
        client.clientManager.send(new ChatPacker(event));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        //TODO 检查权限 multirobot.forward.network.* 或 multirobot.forward.network.join
        //TODO 检查设置中 转发服务器内置加入退出消息到群 是否开启，若未开启 InfoPacker 第二个参数省略或 null
        client.clientManager.send(new InfoPacker(event.getPlayer().getName(), MessagePackType.INFO_Join, event.getJoinMessage()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        //TODO 检查权限 multirobot.forward.network.* 或 multirobot.forward.network.disconnect
        //TODO 检查设置中 转发服务器内置加入退出消息到群 是否开启，若未开启 InfoPacker 第二个参数省略或 null
        client.clientManager.send(new InfoPacker(event.getPlayer().getName(), MessagePackType.INFO_Quit, event.getQuitMessage()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        //TODO 检查权限 multirobot.forward.network.* 或 multirobot.forward.network.death
        //TODO 检查设置中 转发服务器内置死亡消息到群 是否开启，若未开启 InfoPacker 第二个参数省略或 null
        client.clientManager.send(new InfoPacker(event.getEntity().getPlayer().getName(), MessagePackType.INFO_Death, event.getDeathMessage()));
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName().toLowerCase()){
            case "getimage":
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                if(vv) VexView.sendHUD((Player) sender, getId(args[0]), args[0], 100, 100, x, y, 3, config.getDouble("imageX"), config.getDouble("imageY"), args[3]);
                break;
            case "reload":
                sender.sendMessage(Color.YELLOW + "更新配置文件并重启通讯中...");
                reloadConfig();
                config = getConfig();
                client.clientManager.disconnect();
                client = new SocketClient(config.getString("serverIP"), config.getInt("serverPort"), this);
                client.run();
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