package com.gzzm.portal.datacollection;

import java.util.Map;

/**
 * @author ldp
 * @date 2018/4/26
 */
public class MouthDataEnhancer implements DataEnhancer {
    public MouthDataEnhancer() {
    }

    @Override
    public void enhance(Map<String, Object> map) {
        if(map.containsKey("mouth")) {
            String mouth = map.get("mouth").toString();
            if(mouth.length() == 1) {
                mouth = "0" + mouth;
                map.put("mouth", mouth);
            }
        }
    }
}
