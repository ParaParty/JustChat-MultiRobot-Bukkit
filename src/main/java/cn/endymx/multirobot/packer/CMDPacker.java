package cn.endymx.multirobot.packer;

import cn.endymx.multirobot.util.MessagePackType;
import cn.endymx.multirobot.util.MessageTools;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.Collection;

public class CMDPacker extends Packer implements ISendable {

    public CMDPacker(int count, Collection<? extends Player> players){
        super(getMsg(count, players));
    }

    private static String getMsg(int count, Collection<? extends Player> players){
        JSONObject CMDMessage = new JSONObject();
        CMDMessage.put("version", PackVersion);
        CMDMessage.put("type", MessagePackType.CMD_List);
        CMDMessage.put("subtype", 1);
        CMDMessage.put("count", count);
        String[] msg = new String[players.size()];
        int i = 0;
        for (Player player : players) {
            msg[i] = MessageTools.Base64Encode(player.getName());
        }
        CMDMessage.put("content", msg);
        return CMDMessage.toString();
    }
}