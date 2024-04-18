package com.zyneonstudios.zyndexmanager.frame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.zyneonstudios.Main;
import com.zyneonstudios.nexus.index.Index;
import com.zyneonstudios.nexus.index.ReadableZyndex;
import com.zyneonstudios.zyndexmanager.ZyndexManager;
import live.nerotv.shademebaby.utils.FileUtil;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class FrameConnector {

    private final ZyndexWebFrame frame;

    public FrameConnector(ZyndexWebFrame frame) {
        this.frame = frame;
    }

    public void resolve(String request) {
        if(request.startsWith("sync.")) {
            sync(request.replace("sync.", ""));
        } else if(request.startsWith("refresh.")) {
            refresh(request.replace("refresh.",""));
        } else if(request.startsWith("open.url.")) {
            openUrl(request.replace("open.url.", ""));
        } else if(request.startsWith("index.")) {
            index(request);
        } else {
            Main.getLogger().error("[CONNECTOR] Unknown request: "+request);
        }
    }

    private void refresh(String request) {
        if(request.equals("indexes")) {
            ZyndexManager.loadIndexes();
        }
    }

    private void index(String request) {
        if(request.startsWith("index.open")) {
            SwingUtilities.invokeLater(()->{
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select a zyndex .json file");
                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Zyndex .json files", "json","JSON");
                chooser.addChoosableFileFilter(filter);
                int answer = chooser.showOpenDialog(null);
                if (answer == JFileChooser.APPROVE_OPTION) {
                    try {
                        ReadableZyndex source = new ReadableZyndex(chooser.getSelectedFile());
                        String id = transformString(source.getName());
                        File folder = new File(Main.getDirectoryPath()+"indexes/"+id+"/");
                        if(!folder.exists()) {
                            Main.getLogger().log("Created index folder: "+folder.mkdirs());
                        }
                        if(!new File(Main.getDirectoryPath()+"indexes/"+id+"/index.json").exists()) {
                            copyFile(source.getOrigin(),Main.getDirectoryPath()+"indexes/"+id+"/index.json");
                        } else {
                            frame.executeJavaScript("setMessage('Error: Index path already exists','You already have an index with that id!'); showMessage();");
                        }
                        frame.getBrowser().loadURL(ZyndexManager.getBasePath()+"indexes.html");
                        return;
                    } catch (Exception ignore) {}
                }
                frame.executeJavaScript("setMessage('Error: invalid file','You need to submit a valid file to import a local index!'); showMessage();");
            });
        } else if(request.startsWith("index.download.")) {
            try {
                request = request.replace("index.download.", "");
                ReadableZyndex source = new ReadableZyndex(request);
                String id = transformString(source.getName());
                File folder = new File(Main.getDirectoryPath()+"indexes/"+id+"/");
                if(!folder.exists()) {
                    Main.getLogger().log("Created index folder: "+folder.mkdirs());
                }
                if(!new File(Main.getDirectoryPath()+"indexes/"+id+"/index.json").exists()) {
                    FileUtil.downloadFile(source.getUrl(), Main.getDirectoryPath()+"indexes/"+id+"/index.json");
                } else {
                    frame.executeJavaScript("setMessage('Error: Index path already exists','You already have an index with that id!'); showMessage();");
                }
                frame.getBrowser().loadURL(ZyndexManager.getBasePath()+"indexes.html");
                return;
            } catch (Exception ignore) {}
            frame.executeJavaScript("setMessage('Error: invalid url','You need to submit an valid url to download an index!'); showMessage();");
        } else if(request.startsWith("index.create.")) {
            try {
                request = request.replace("index.create.", "");
                String[] creator = request.split("\\.", 2);
                String name = creator[0].replace("%DOT%", ".");
                String id = transformString(name);
                File folder = new File(Main.getDirectoryPath() + "indexes/" + id + "/");
                if (!folder.exists()) {
                    Main.getLogger().log("Created index folder: " + folder.mkdirs());
                }
                String owner = creator[1].replace("%DOT%", ".");
                JsonArray instances = new JsonArray();
                if (!new File(Main.getDirectoryPath() + "indexes/" + id + "/index.json").exists()) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject json = new JsonObject();
                    json.addProperty("name", name);
                    json.addProperty("url", "");
                    json.addProperty("owner", owner);
                    json.add("instances", instances);
                    File file = new File(Main.getDirectoryPath() + "indexes/" + id + "/index.json");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        String jsonString = gson.toJson(json);
                        writer.write(jsonString);
                    }
                    frame.getBrowser().loadURL(ZyndexManager.getBasePath()+"indexes.html");
                } else {
                    frame.executeJavaScript("setMessage('Error: Index path already exists','You already have an index with that id!'); showMessage();");
                }
            } catch (Exception e) {
                Main.getLogger().error("Couldn't create index: "+e);
                frame.executeJavaScript("setMessage('Error: Couldn\\'t create the index','"+e.getMessage().replace("\"","''").replace("'","\\'")+"'); showMessage();");
                throw new RuntimeException(e);
            }
        }
    }

    private void copyFile(String sourceFile, String destinationFile) throws IOException {
        try (InputStream inputStream = new FileInputStream(sourceFile);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private String transformString(String inputString) {
        return inputString.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }

    private void sync(String request) {
        frame.executeJavaScript("setMode('desktop');");
        if(request.equals("init")) {
            frame.setMessage("Zyndex Manager (Desktop)","synchronizing...",true);
        } else if(request.equals("indexes")) {
            frame.showMessage(false);
            CompletableFuture.runAsync(()-> {
                for(Index index:ZyndexManager.getIndexes()) {
                    addIndexToList(index);
                }
                frame.executeJavaScript("document.getElementById('loader').style.display = 'none';");
            });
        } else if(request.equals("settings")) {
            frame.showMessage(false);
        } else {
            frame.showMessage(false);
        }
    }

    private void addIndexToList(Index index) {
        String name = index.getName().replace("\"", "\\\"").replace("'", "\\'");
        String url;
        if(index.getUrl().isEmpty()) {
            url = index.getOrigin().replace("\\\\","\\").replace("\\","/").replace("\"", "\\\"").replace("'", "\\'");
        } else {
            url = index.getUrl().replace("\"", "\\\"").replace("'", "\\'");
        }
        String owner = index.getOwner().replace("\"", "\\\"").replace("'", "\\'");
        int instances = index.getInstances().size();
        String instances_;
        if(instances==1) {
            instances_ = "1 instance";
        } else {
            instances_ = instances+" instances";
        }
        frame.executeJavaScript("addIndexToList('" + name + "','" + url + "','" + owner + "','" + instances_ + "')");
    }

    private void openUrl(String request) {
        if(!request.startsWith("http://")&&!request.startsWith("https://")&&!request.startsWith("file://")) {
            request = "file://"+request;
        }
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(URI.create(request));
            } catch (Exception e) {
                Main.getLogger().error("Couldn't open url \""+request+"\": "+e.getMessage());
            }
        }
    }
}