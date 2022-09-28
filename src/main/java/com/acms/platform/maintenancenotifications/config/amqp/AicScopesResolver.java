package com.acms.platform.maintenancenotifications.config.amqp;

import com.acms.platform.amqpsecurity.interceptor.ScopesResolver;
import org.springframework.amqp.core.Message;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@Primary
public class AicScopesResolver implements ScopesResolver {

    @Override
    public Set<String> getScopes(Message message, String s, String s1) {
        return Collections.emptySet();
    }
}
