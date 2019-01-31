package cn.endymx.multirobot.packer;

import cn.endymx.multirobot.util.MessagePackType;
import cn.endymx.multirobot.util.MessageTools;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import org.bukkit.ChatColor;
import org.json.JSONObject;

public class InfoPacker extends Packer implements ISendable {

    public InfoPacker(String message){
        super(getMsg(message));
    }

    private static String getMsg(String message){
        JSONObject pingMessage = new JSONObject();
        pingMessage.put("version", PackVersion);
        pingMessage.put("type", MessagePackType.INFO);
        pingMessage.put("content", MessageTools.Base64Encode(ChatColor.stripColor(message)));
        return pingMessage.toString();
    }
}