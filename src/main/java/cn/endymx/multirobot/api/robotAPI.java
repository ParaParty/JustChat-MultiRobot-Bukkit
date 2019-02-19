package cn.endymx.multirobot.api;

import cn.endymx.multirobot.LoadClass;
import cn.endymx.multirobot.socket.SocketClient;

public class robotAPI {

    public static LoadClass api;
    public static SocketClient socket;

    public robotAPI(LoadClass pl){
        api = pl;
        socket = pl.client;
    }

    public static LoadClass getApi(){
        return api;
    }

    public static SocketClient getSocket(){
        return socket;
    }
}
