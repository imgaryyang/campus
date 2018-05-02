package com.gzzm.im.ios;

import com.gzzm.im.entitys.UserMessage;
import com.gzzm.im.ios.dbay.apns4j.IApnsService;
import com.gzzm.im.ios.dbay.apns4j.impl.ApnsServiceImpl;
import com.gzzm.im.ios.dbay.apns4j.model.ApnsConfig;
import com.gzzm.im.ios.dbay.apns4j.model.Payload;
import net.cyan.commons.util.SystemConfig;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author sjy
 * @date 2017/3/23
 */
public class ApnsUtil {

    private static IApnsService apnsService;

        public static void sendMegToIOS(String token,UserMessage message,Integer badge) throws Exception {
            Payload payload = new Payload();
            if(message!=null){
                String content = message.getContent();
                if (content.length() > 10) {
                    content = content.substring(0, 10) + "...";
                }
                payload.addAlert(message.getSenderUser().getUserName()+":"+content);
                payload.addSender(message.getSender());
            }else {
                payload.addSound(null);
            }
            payload.addBadge(badge);
            getApnsService().sendNotification(token, payload);
        }

        public static void sendMegToIOS(String token, Payload payload) throws Exception {
            getApnsService().sendNotification(token, payload);
            getApnsService().shutdown();
        }

        private static IApnsService getApnsService() throws Exception {
            if (apnsService == null) {
                synchronized (ApnsUtil.class) {
                    if (apnsService == null) {
                        ApnsConfig config = new ApnsConfig();
                        //InputStream is = new FileInputStream("E:\\jar\\im\\Push.p12");
                        String appPath = SystemConfig.getInstance().getAppPath("/WEB-INF/push.p12");
                        InputStream is = new FileInputStream(appPath);
                        config.setKeyStore(is);
                        config.setDevEnv(false);
                        config.setPassword("abc123");
                        config.setPoolSize(3);
                        apnsService = ApnsServiceImpl.createInstance(config);
                    }
                }
            }
            return apnsService;
        }
}
