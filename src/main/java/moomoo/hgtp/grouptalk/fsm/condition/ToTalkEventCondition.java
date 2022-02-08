package moomoo.hgtp.grouptalk.fsm.condition;

import com.fsm.StateManager;
import com.fsm.event.base.StateEvent;
import com.fsm.module.base.EventCondition;
import com.fsm.unit.StateUnit;
import moomoo.hgtp.grouptalk.fsm.HgtpState;
import moomoo.hgtp.grouptalk.session.base.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ToTalkEventCondition extends EventCondition {

    private static final Logger log = LoggerFactory.getLogger(ToTalkEventCondition.class);

    public ToTalkEventCondition(StateManager stateManager, StateEvent stateEvent) {
        super(stateManager, stateEvent);
    }


    @Override
    public boolean checkCondition() {
        StateUnit hgtpStateUnit = Optional.ofNullable(getCurStateUnit()).orElse(null);
        if (hgtpStateUnit == null) {
            log.error("check stateUnit is null");
            return false;
        }

        UserInfo userInfo = Optional.ofNullable((UserInfo) hgtpStateUnit.getData()).orElse(null);
        if (userInfo != null) {
            log.debug("({}) () () [{}] state [{}]", userInfo.getUserId(), userInfo.getHgtpStateUnitId(), hgtpStateUnit.getCurState());
            return HgtpState.TO_TALK.contains(hgtpStateUnit.getCurState());
        }
        return false;
    }
}
