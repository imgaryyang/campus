/*
 * Copyright 2013 DiscoveryBay Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gzzm.im.ios.dbay.apns4j.model;

import com.gzzm.platform.commons.Tools;
import net.cyan.commons.util.json.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author RamosLi
 *         For more details, view the following site:
 *         https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/ApplePushService.html
 */
public class Payload {
    private Msg aps = new Msg();

    private Integer sender;


    public void addBadge(Integer badge) {
        aps.setBadge(badge);
    }

    public void addAlert(String alert) {
        aps.setAlert(alert);
    }

    public void addSound(String sound) {
        aps.setSound(sound);
    }
     public void addSender(Integer sender) {
        setSender(sender);
    }

   public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public Msg getAps() {
        return aps;
    }

    public void setAps(Msg aps) {
        this.aps = aps;
    }

    @Override
    public String toString() {
        JsonSerializer jsonSerializer = new JsonSerializer(){
            protected void serializeNull(String properties, String property, Type type) throws Exception {
                serializeValue("", null, null);
            }

        };
        try {
            return jsonSerializer.serialize(this).toString();
        } catch (Exception e) {
            Tools.log(e);
        }
        return super.toString();
    }
}
