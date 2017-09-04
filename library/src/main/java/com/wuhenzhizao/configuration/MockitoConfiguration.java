package com.wuhenzhizao.configuration;

import org.mockito.configuration.DefaultMockitoConfiguration;

/**
 * Created by wuhenzhizao on 2017/9/4.
 */
public class MockitoConfiguration extends DefaultMockitoConfiguration {

    @Override
    public boolean enableClassCache() {
        return false;
    }
}