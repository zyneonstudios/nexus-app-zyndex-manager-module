package com.zyneonstudios.zyndexmanager.frame;

import com.zyneonstudios.Main;
import live.nerotv.shademebaby.frame.WebFrame;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import java.awt.*;

public class ZyndexWebFrame extends WebFrame {

    private final FrameConnector frameConnector;

    public ZyndexWebFrame(String url, String jcefPath) {
        super(url, jcefPath);
        setBackground(Color.black);
        frameConnector = new FrameConnector(this);
        getClient().addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                if(message.contains("[BRIDGE] ")) {
                    String request = message.replace("[BRIDGE] ","");
                    Main.getLogger().debug("[CONNECTOR] Received request: \""+request+"\"");
                    frameConnector.resolve(request);
                }
                return super.onConsoleMessage(browser, level, message, source, line);
            }
        });
        setMinimumSize(new Dimension(640,360));
        setTitle("Zyndex Manager ("+Main.getOperatingSystem().getType()+"-"+Main.getOperatingSystem().getArchitecture()+")",Color.decode("#0f0f0f"),Color.white);
    }

    public FrameConnector getFrameConnector() {
        return frameConnector;
    }

    public void setTitle(String title, Color background, Color foreground) {
        setTitle(title);
        setTitleBackground(background);
        setTitleForeground(foreground);
    }

    public void setTitleBackground(Color color) {
        setBackground(color);
        getRootPane().putClientProperty("JRootPane.titleBarBackground", color);
    }

    public void setTitleForeground(Color color) {
        getRootPane().putClientProperty("JRootPane.titleBarForeground", color);
    }

    public void executeJavaScript(String command) {
        getBrowser().executeJavaScript(command,getBrowser().getURL(),5);
    }

    public void setMessage(String title, String text, boolean show) {
        setMessage(title,text);
        showMessage(show);
    }

    public void setMessage(String title, String text) {
        executeJavaScript("setMessage('"+title+"','"+text+"');");
    }

    public void showMessage(boolean show) {
        if(show) {
            executeJavaScript("showMessage();");
        } else {
            executeJavaScript("hideMessage();");
        }
    }
}
