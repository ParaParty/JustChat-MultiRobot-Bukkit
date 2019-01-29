package cn.endymx.multirobot.packer;

import cn.endymx.multirobot.util.MessagePackType;
import cn.endymx.multirobot.util.MessageTools;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import org.json.JSONObject;

public class UidPacker extends Packer implements ISendable {

    public UidPacker(String id, String name){
        super(getMsg(id, name));
    }

    private static String getMsg(String id, String name){
        JSONObject uidMessage = new JSONObject();
        uidMessage.put("version", PackVersion);
        uidMessage.put("type", MessagePackType.UID);
        uidMessage.put("identity", 0);
        uidMessage.put("id", id);
        uidMessage.put("name", MessageTools.Base64Encode(name));
        return uidMessage.toString();
    }
}
