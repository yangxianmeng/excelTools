package log;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author: Yxm
 **/
public abstract class AbstractLog {

    /** 出错的文件 */
    private Queue<String> errorFiles = new ConcurrentLinkedQueue<>();

    public abstract void info(String msg);
    public abstract void error(String msg);

    public final void addErrorFile(String path) {
        errorFiles.add(path);
    }
}
