package log;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author: Yxm
 **/
public abstract class AbstractLog {
    /** 整个导表过程中是否有错误 */
    private volatile boolean failed = false;
    /** 出错的Excel文件列表 */
    private Queue<String> errorFiles = new ConcurrentLinkedQueue<>();

    /**
     * 普通日志
     * @param message
     */
    public abstract void info(String message);

    /**
     * 错误日志
     * @param message
     */
    public void error(String message) {
        this.failed = true;
    }

    /**
     * 清除日志
     */
    public void clean() {
        failed = false;
        errorFiles.clear();
    }

    /**
     * 从{@link CacheLog}中拷贝全部日志
     * @param log
     */
    synchronized public final void copy(CacheLog log) {
        for (String msg : log.getInfoMsgs()) {
            info(msg);
        }

        for (String msg : log.getErrorMsgs()) {
            error(msg);
        }

        for (String file : log.getErrorFiles()) {
            addErrorFile(file);
        }
    }

    /**
     * 整个导表过程中是否有错误
     * @return
     */
    public final boolean isFailed() {
        return failed;
    }

    /**
     * 增加出错的文件
     * @param path
     */
    public final void addErrorFile(String path) {
        errorFiles.add(path);
    }

    public final Collection<String> getErrorFiles() {
        return errorFiles;
    }

}
