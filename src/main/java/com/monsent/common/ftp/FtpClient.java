package com.monsent.common.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lj on 2017/7/15.
 */

public class FtpClient {

    public final static int PORT_DEFAULT = 21; //默认端口号
    private FTPClient client = new FTPClient();

    /**
     * 连接服务器
     * @param hostname
     * @param port
     * @return
     */
    public boolean connect(String hostname, int port){
        try{
            FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
            config.setLenientFutureDates(true);
            client.configure(config);
            client.connect(hostname, port);
            int reply = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)){
                return false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 连接服务器
     * @param hostname
     * @return
     */
    public boolean connect(String hostname){
        return connect(hostname, PORT_DEFAULT);
    }

    /**
     * 登录服务器
     * @param user
     * @param password
     * @return
     */
    public boolean login(String user, String password){
        if (!client.isConnected()){
            return false;
        }
        try{
            boolean result = client.login(user, password);
            if (!result){
                return false;
            }
            client.setFileType(FTPClient.FILE_STRUCTURE);
            client.enterLocalPassiveMode();
            client.setFileType(FTPClient.BINARY_FILE_TYPE);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取远程目录下的文件
     * @param remotePath
     * @return
     */
    public FTPFile[] listFiles(String remotePath){
        try{
            return client.listFiles(remotePath);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取远程文件
     * @param remotePath
     * @return
     */
    public FTPFile getFTPFile(String remotePath){
        try {
            return client.mlistFile(remotePath);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取远程文件流
     * @param remotePath
     * @return
     */
    public InputStream getFileStream(String remotePath){
        try {
            return client.retrieveFileStream(remotePath);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 上传文件
     * @param remotePath 远程目录
     * @param filename  远程文件名
     * @param input 输入流
     * @return
     */
    public boolean uploadFile(String remotePath, String filename, InputStream input){
        try {
            client.changeWorkingDirectory(remotePath);
            client.storeFile(filename, input);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 下载文件
     * @param remotePath 远程文件
     * @param localPath 本地文件
     * @return
     */
    public boolean downloadFile(String remotePath, String localPath){
        OutputStream os = null;
        try {
            os = new FileOutputStream(localPath);
            client.retrieveFile(remotePath, os);
            return true;
        }catch (Exception e){
            return false;
        }finally {
            if (os != null){
                try {
                    os.close();
                }catch (Exception e){

                }
            }
        }
    }

    /**
     * 关闭服务
     */
    public void close(){
        if (client.isConnected()){
            try {
                client.logout();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            client.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 处理多个文件时，是否完成
     * @return
     */
    public boolean isCompleted(){
        try {
            return client.completePendingCommand();
        }catch (Exception e){
            return false;
        }
    }

}
