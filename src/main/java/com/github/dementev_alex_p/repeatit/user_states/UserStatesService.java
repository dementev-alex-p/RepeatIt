package com.github.dementev_alex_p.repeatit.user_states;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserStatesService {
    private final Map<Long, UserState> userStatesMap = new HashMap<>();

    public UserState getStateByUserId(final long userId) {
        return userStatesMap.get(userId);
    }

    public void addState(final UserState userState) {
        userStatesMap.put(userState.getUserId(), userState);
    }

    public void removeState(Long userId) {
        userStatesMap.remove(userId);
    }
}
