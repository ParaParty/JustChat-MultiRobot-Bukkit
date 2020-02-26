package cn.endymx.multirobot.packer;

import cn.endymx.multirobot.util.MessagePackType;
import cn.endymx.multirobot.util.MessageTools;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import org.bukkit.ChatColor;
import org.json.JSONObject;

public class InfoPacker extends Packer implements ISendable {

    public InfoPacker(String sender, int event){
        super(getMsg(sender, event, null));
    }

    public InfoPacker(String sender, int event, String message){
        super(getMsg(sender, event, message));
    }

    private static String getMsg(String sender, int event, String message){
        JSONObject infoMessage = new JSONObject();
        infoMessage.put("version", PackVersion);
        infoMessage.put("type", MessagePackType.INFO);
        infoMessage.put("sender", MessageTools.Base64Encode(sender));
        infoMessage.put("event", event);
        if (message != null) infoMessage.put("content", MessageTools.Base64Encode(ChatColor.stripColor(message)));
        return infoMessage.toString();
    }
}