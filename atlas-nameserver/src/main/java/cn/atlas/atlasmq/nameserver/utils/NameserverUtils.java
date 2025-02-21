package cn.atlas.atlasmq.nameserver.utils;

import cn.atlas.atlasmq.nameserver.common.CommonCache;

/**
 * @Author xiaoxin
 * @Create 2025/2/21 下午9:08
 * @Version 1.0
 */
public class NameserverUtils {
    public static boolean isVerify(String user, String password) {
        String rightUser = CommonCache.getNameserverProperties().getNameserverUser();
        String rightPassword = CommonCache.getNameserverProperties().getNameserverPwd();
        if (!rightUser.equals(user) || !rightPassword.equals(password)) {
            return false;
        }
        return true;
    }
}
