package com.gzzm.platform.opinion;

import net.cyan.commons.util.MutableInteger;
import net.cyan.commons.util.StringUtils;
import net.cyan.nest.annotation.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author camel
 * @date 2015/2/10
 */
public class OpinionService {
    @Inject
    private OpinionDao dao;

    public OpinionService() {
    }

    public OpinionDao getDao() {
        return dao;
    }

    public void addContent(String content, Integer userId, boolean split) throws Exception {
        if (content != null) {
            if (split) {
                String[] ss = content.split("[,.?;!:'\"，。？：！；\\s‘’“”\r\n]");

                Map<String, MutableInteger> frequencys = new HashMap<String, MutableInteger>();
                for (String s : ss) {
                    if (!StringUtils.isBlank(s)) {
                        MutableInteger frequency = frequencys.get(s);
                        if (frequency == null)
                            frequencys.put(s, new MutableInteger(1));
                        else
                            frequency.getAndIncrement();
                    }
                }

                for (Map.Entry<String, MutableInteger> entry : frequencys.entrySet()) {
                    String s = entry.getKey();
                    MutableInteger frequency = entry.getValue();

                    if (dao.updateFrequency(userId, s, frequency.intValue()) == 0) {
                        Opinion opinion = new Opinion();
                        opinion.setUserId(userId);
                        opinion.setTitle(s);
                        opinion.setContent(s);
                        opinion.setFrequency(0);
                        opinion.setOrderId(40);

                        dao.add(opinion);
                    }
                }
            } else {
                if (dao.updateFrequency(userId, content, 1) == 0) {
                    Opinion opinion = new Opinion();
                    opinion.setUserId(userId);
                    opinion.setTitle(content);
                    opinion.setContent(content);
                    opinion.setFrequency(0);
                    opinion.setOrderId(40);
                    dao.add(opinion);
                }
            }
        }
    }
}
