package com.example.a10483.httptest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.RandomAccess;

/**
 * Created by 10483 on 2017/10/31.
 */
//实现断点续传功能
public class DownLoad {

    public static final String PATH="http://soft3.xzstatic.com/2015/10/hsjj2ghgzh.rar";
    public static int threadCount=0;
    public static void main(String[] args){
        try{
            URL url=new URL(PATH);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            String[] str=PATH.split("/");
            String fileName=str[5];
            int fileLength=conn.getContentLength();
            RandomAccessFile raf=new RandomAccessFile(fileName,"rwd");
            raf.setLength(fileLength);
            threadCount=3;
            int blockSize=fileLength/threadCount;//每条线程下载的大小

            for(int threadId=1;threadId<=threadCount;threadId++){
                int startPos=(threadId-1)*blockSize;//开始下载的位置
                int endPos=(threadId*blockSize)-1;
                if(threadCount==threadId) {
                    endPos = fileLength;
                }
                new Thread(new DownLoadThread(threadId,startPos,endPos,PATH)).start();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    static class DownLoadThread implements Runnable{
        private int threadId;
        private int startPos;
        private int endPos;
        private String PATH;

        public DownLoadThread(int threadId,int startPos,int endPos,String PATH){
            super();
            this.threadId=threadId;
            this.startPos=startPos;
            this.endPos=endPos;
            this.PATH=PATH;
        }
        @Override
        public void run() {
            try{
                URL url=new URL(PATH);
                String[] str=PATH.split("/");
                String fileName=str[5];
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                File file=new File(threadId+".txt");//线程标记
                if(file.exists()&&file.length()>0){
                    //读取file文件
                    BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String saveStartPos=br.readLine();//获得保存开始点
                    if(saveStartPos!=null&&saveStartPos.length()>0){
                        startPos=Integer.parseInt(saveStartPos);//把保存开始点设置为初始点
                    }
                }
                conn.setRequestProperty("Range","bytes="+startPos+"-"+endPos);//设置获取范围从开始点到结尾点
                RandomAccessFile raf=new RandomAccessFile(fileName,"rwd");
                raf.seek(startPos);//设置开始下载的位置
                InputStream is=conn.getInputStream();
                byte[] b=new byte[1024*1024*10];
                int len=-1;
                int newPos=startPos;
                while((len=is.read(b))!=-1){
                    RandomAccessFile rr=new RandomAccessFile(file,"rwd");
                    raf.write(b,0,len);
                    String savePoint=String.valueOf(newPos+=len);
                    rr.write(savePoint.getBytes());
                    rr.close();
                }
                is.close();
                raf.close();


            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
