package log;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 日志缓存
 * @author: Yxm
 **/
public class CacheLog extends AbstractLog {
    /** 缓存普通日志 */
    private Queue<String> infoMsgs = new ConcurrentLinkedQueue<>();
    /** 缓存错误日志 */
    private Queue<String> errorMsgs = new ConcurrentLinkedQueue<>();

    @Override
    public void info(String message) {
        infoMsgs.add(message);
    }

    @Override
    public void error(String message) {
        super.error(message);
        errorMsgs.add(message);
    }

    @Override
    public void clean() {
        super.clean();
        infoMsgs.clear();
        errorMsgs.clear();
    }

    public final Collection<String> getInfoMsgs() {
        return infoMsgs;
    }

    public final Collection<String> getErrorMsgs() {
        return errorMsgs;
    }

}
