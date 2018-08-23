package com.uob.dge.cpmbatch.utils;

import com.uobgroup.encrypt.UPwdUtil;
import org.springframework.core.env.ConfigurableEnvironment;

public class PasswordDecoder {

    public String decodePassword(String encodedPassword, ConfigurableEnvironment allProperties) {
        try {
            UPwdUtil pwdUtil = new UPwdUtil(allProperties.getProperty("encrypt.key1"), allProperties.getProperty("encrypt.key2"));
            return pwdUtil.getPassword(allProperties.getProperty("encrypt.key1"), allProperties.getProperty("encrypt.key2"), encodedPassword);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
