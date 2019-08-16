package io.renren.modules.spider.gather.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CallbackReplyMsg
 *
 * @author Gao Shen
 * @version 16/4/22
 */
public class CallbackReplyMsg extends InfoMsg {
    private Logger LOG = LoggerFactory.getLogger(CallbackReplyMsg.class);

    public CallbackReplyMsg(String clientId) {
        super(clientId);
        this.setType(MsgType.CALLBACK_REPLY);
    }
}
