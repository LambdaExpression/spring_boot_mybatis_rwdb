package org.tcat.parent.mybaits.dataSource;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 使用ThreadLocal技术来记录当前线程中的数据源的key
 */
public class DynamicDataSourceHolder {

    /**
     * 写库对应的数据源key
     */

    private static final String MASTER = "master";

    /**
     * 读库对应的数据源key
     */
    private static List<String> salveList = new ArrayList<>();

    /**
     * 使用ThreadLocal记录当前线程的数据源key
     */
    private static final ThreadLocal<String> holder = new ThreadLocal<String>();

    /**
     * 添加 从库对应的数据源key
     *
     * @param salveName
     * @return
     */
    public void addSalve(String salveName) {
        salveList.add(salveName);
    }

    /**
     * 设置数据源key
     *
     * @param key
     */
    public static void putDataSourceKey(String key) {
        holder.set(key);
    }

    /**
     * 获取数据源key
     *
     * @return
     */
    public static String getDataSourceKey() {
        return holder.get();
    }

    /**
     * 标记写库
     */
    public static void markMaster() {
        putDataSourceKey(MASTER);
    }

    /**
     * 标记读库
     */
    public static void markSlave() {
        if (salveList.size() == 0) {
            markMaster();
        }
        if (salveList.size() == 1) {
            putDataSourceKey(salveList.get(0));
        }
        Integer randow = new Random().nextInt(salveList.size());
        putDataSourceKey(salveList.get(randow));
    }

}