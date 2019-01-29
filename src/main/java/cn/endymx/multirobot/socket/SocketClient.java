package cn.endymx.multirobot.socket;

import cn.endymx.multirobot.LoadClass;

import cn.endymx.multirobot.packer.Packer;
import cn.endymx.multirobot.packer.PingPacker;
import cn.endymx.multirobot.packer.UidPacker;
import cn.endymx.multirobot.util.MessageDecode;
import cn.endymx.multirobot.util.MessageTools;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.core.protocol.IReaderProtocol;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.UUID;

public class SocketClient extends Thread{
    private final static byte[] MessageHeader = {0x11, 0x45, 0x14};

    private int port;
    private String host;
    private LoadClass plugin;
    public ConnectionInfo client;
    public IConnectionManager clientManager;
    private PingPacker mPulseData = new PingPacker();

    public SocketClient(String host, int port, LoadClass plugin) {
        this.port = port;
        this.host = host;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        client = new ConnectionInfo(host, port);
        clientManager = OkSocket.open(client);
        plugin.getLogger().info("正在连接到服务器");
        OkSocketOptions.Builder okOptionsBuilder = new OkSocketOptions.Builder();
        okOptionsBuilder.setPulseFrequency(plugin.config.getInt("serverPulse") * 1000);
        okOptionsBuilder.setReaderProtocol(new IReaderProtocol() {
            @Override
            public int getHeaderLength(){
                /*
                 * 返回不能为零或负数的报文头长度(字节数)。
                 * 您返回的值应符合服务器文档中的报文头的固定长度值(字节数),可能需要与后台同学商定
                 */
                return MessageHeader.length + 4 /*固定报文头的长度(字节数)*/;
            }

            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                /*
                 * 体长也称为有效载荷长度，
                 * 该值应从作为函数输入参数的header中读取。
                 * 从报文头数据header中解析有效负载长度时，最好注意参数中的byteOrder。
                 * 我们强烈建议您使用java.nio.ByteBuffer来做到这一点。
                 * 你需要返回有效载荷的长度,并且返回的长度中不应该包含报文头的固定长度
                 */
                if (Packer.isMessage(header)){
                    return (header[3]&0xff)*(2<<23)+
                            (header[4]&0xff)*(2<<15)+
                            (header[5]&0xff)*(2<<7)+
                            (header[6]&0xff); /*有效负载长度(字节数)，固定报文头长度(字节数)除外*/
                }
                return 0;
            }
        });
        //将新的修改后的参配设置给连接管理器
        clientManager.option(okOptionsBuilder.build());
        //注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
        clientManager.registerReceiver(new SocketActionAdapter(){
            @Override
            public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
                plugin.getLogger().info("已连接到服务器");
                clientManager.send(new UidPacker(UUID.randomUUID().toString(), plugin.config.getString("serverName")));
                OkSocket.open(info)
                        .getPulseManager()
                        .setPulseSendable(mPulseData)//只需要设置一次,下一次可以直接调用pulse()
                        .pulse();//开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
            }

            @Override
            public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
                //遵循以上规则,这个回调才可以正常收到服务器返回的数据,数据在OriginalData中,为byte[]数组,该数组数据已经处理过字节序问题,直接放入ByteBuffer中即可使用
                if (Packer.isMessage(data.getHeadBytes())){
                    String str = new String (data.getBodyBytes(), Charset.forName(MessageTools.getEncode()));
                    new MessageDecode(str, plugin).decodeData();
                }
            }
        });
        clientManager.connect();
    }
}
