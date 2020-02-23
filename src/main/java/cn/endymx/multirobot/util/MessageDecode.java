package cn.endymx.multirobot.util;

import cn.endymx.multirobot.LoadClass;
import cn.endymx.multirobot.packer.CMDPacker;
import cn.endymx.multirobot.packer.Packer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageDecode {
    private String data;
    private LoadClass plugin;

    public  MessageDecode(String data, LoadClass plugin) {
        this.data = data;
        this.plugin = plugin;
    }

    public void decodeData(){
        try{
            JSONObject json = new JSONObject(data);
            if (json.getInt("version") == Packer.PackVersion) {
                switch(json.getInt("type")){
                    case MessagePackType.PING:
                        if(plugin.client.clientManager != null){
                            //喂狗
                            plugin.client.clientManager.getPulseManager().feed();
                        }
                        break;
                    case MessagePackType.INFO:
                        plugin.getServer().broadcastMessage(MessageTools.Base64Decode(json.getString("content")));
                        break;
                    case MessagePackType.CHAT:
                        String world = MessageTools.Base64Decode(json.getString("world_display"));
                        String sender = MessageTools.Base64Decode(json.getString("sender"));
                        TextComponent bc = new TextComponent(plugin.config.getString("messageFormQQ").replace("%world%", world).replace("%player%", sender));
                        JSONArray mjson = json.getJSONArray("content");
                        for (int i = 0; i < mjson.length(); i ++) {
                            JSONObject msg = mjson.getJSONObject(i);
                            switch(msg.getString("type")){
                                case "text":
                                    bc.addExtra(new TextComponent(MessageTools.Base64Decode(msg.getString("content"))));
                                    break;
                                case "cqcode":
                                    switch(msg.getString("function")){
                                        case "CQ:at":
                                            TextComponent at = new TextComponent(MessageTools.Base64Decode(msg.getString("target")));
                                            at.setColor(ChatColor.BLUE);
                                            at.setItalic(true);
                                            bc.addExtra(at);
                                            break;
                                        case "CQ:image":
                                            TextComponent image = new TextComponent(MessageTools.Base64Decode(msg.getString("content")));
                                            //if (plugin.vv) image.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,  "/getimage " + msg.getString("url") + " " + msg.getInt("width") + " " + msg.getInt("height") + " " + msg.getString("extension")));
                                                image.setColor(ChatColor.BLUE);
                                            //if (plugin.vv) image.setUnderlined(true);
                                                bc.addExtra(image);
                                            break;
                                        case "CQ:face":
                                            TextComponent face = new TextComponent("[表情:" + MessageTools.Base64Decode(msg.getString("content")) + "]");
                                            //if (plugin.vv) face.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,  "/getimage [local]face/" + msg.getInt("id") + "." + msg.getString("extension") + " " + 28 + " " + 28 + " " + msg.getString("extension")));
                                            face.setColor(ChatColor.YELLOW);
                                            //if (plugin.vv) face.setUnderlined(true);
                                            bc.addExtra(face);
                                            break;
                                        case "CQ:hb":
                                            TextComponent hb = new TextComponent("[红包:" + MessageTools.Base64Decode(msg.getString("title") + "]"));
                                            hb.setColor(ChatColor.RED);
                                            bc.addExtra(hb);
                                            break;
                                        case "CQ:rich"://TODO: XML信息可能会URL为空
                                            TextComponent rich = new TextComponent(MessageTools.Base64Decode(msg.getString("text")));
                                            rich.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, MessageTools.Base64Decode(msg.getString("url"))));
                                            rich.setColor(ChatColor.BLUE);
                                            rich.setUnderlined(true);
                                            bc.addExtra(rich);
                                            break;
                                        case "CQ:share":
                                            TextComponent share = new TextComponent("[分享] " + MessageTools.Base64Decode(msg.getString("title")));
                                            share.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, MessageTools.Base64Decode(msg.getString("url"))));
                                            share.setColor(ChatColor.BLUE);
                                            share.setUnderlined(true);
                                            bc.addExtra(share);
                                            break;
                                    }
                                    break;
                            }
                        }
                        for(Player player : plugin.getServer().getOnlinePlayers()){
                            player.spigot().sendMessage(bc);
                        }
                        break;
                    case MessagePackType.CMD_List:
                        if(json.getInt("subtype") == 0){
                            plugin.client.clientManager.send(new CMDPacker(plugin.getServer().getMaxPlayers(), plugin.getServer().getOnlinePlayers()));
                        }else{
                            plugin.getLogger().info("收到类型无法识别的消息");
                        }
                        break;
                    default:
                        plugin.getLogger().info("收到类型无法识别的消息");
                        break;
                }
            }else {
                plugin.getLogger().info("收到不同版本的消息");
            }
        }catch(JSONException e) {
            plugin.getLogger().warning("收到无法解析的信息");
        }
    }
}
