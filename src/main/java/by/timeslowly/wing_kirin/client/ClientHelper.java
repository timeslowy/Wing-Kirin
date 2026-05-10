package by.timeslowly.wing_kirin.client;

import java.util.function.BooleanSupplier;

/**
 * 通用端辅助类，提供客户端状态的安全访问。
 * 服务端使用默认值，客户端在初始化时替换为真实实现。
 */
public class ClientHelper {
    public static BooleanSupplier SHIFT_DOWN = () -> false;
}
