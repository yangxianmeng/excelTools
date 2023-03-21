package log;

/**
 * 日志缓存
 * @author: Yxm
 **/
public class CacheLog extends AbstractLog {

    @Override
    public void info(String msg) {
        System.out.println(msg);
    }

    @Override
    public void error(String msg) {
        System.err.println(msg);
    }
}
