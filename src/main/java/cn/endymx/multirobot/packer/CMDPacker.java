package cn.endymx.multirobot.packer;

import cn.endymx.multirobot.util.MessagePackType;
import cn.endymx.multirobot.util.MessageTools;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.Collection;

public class CMDPacker extends Packer implements ISendable {

    public CMDPacker(String sender, String world, String world_display, int max, Collection<? extends Player> players){
        super(getMsg(sender, world, world_display, max, players));
    }

    private static String getMsg(String sender, String world, String world_display, int max, Collection<? extends Player> players){
        JSONObject CMDMessage = new JSONObject();
        CMDMessage.put("version", PackVersion);
        CMDMessage.put("type", MessagePackType.CMD_List);
        CMDMessage.put("subtype", 1);
        CMDMessage.put("max", max);
        CMDMessage.put("count", players.size());
        String[] msg = new String[players.size()];
        int i = 0;
        for (Player player : players) {
            msg[i] = MessageTools.Base64Encode(player.getName());
        }
        CMDMessage.put("player_list", msg);
        CMDMessage.put("sender", sender);
        CMDMessage.put("world", world);
        CMDMessage.put("world_display", world_display);
        return CMDMessage.toString();
    }
}