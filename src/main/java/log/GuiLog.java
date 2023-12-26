package log;

import javax.swing.*;

/**
 * @author Yxm
 **/
public class GuiLog extends AbstractLog {

    /** 实际显示控件 */
    private JTextArea taNormal;
    /** 错误日志 */
    private JTextArea taError;

    public GuiLog(JTextArea taNormal, JTextArea taError) {
        this.taNormal = taNormal;
        this.taError = taError;
    }

    @Override
    public void info(String msg) {
        SwingUtilities.invokeLater(() -> {
            taNormal.append(msg + '\n');
            taNormal.setCaretPosition(taNormal.getText().length());
        });
    }

    @Override
    public void error(String msg) {
        super.error(msg);
        SwingUtilities.invokeLater(() -> {
            taError.append("☢ " + msg + '\n');
            taError.setCaretPosition(taError.getText().length());
        });
    }
}
