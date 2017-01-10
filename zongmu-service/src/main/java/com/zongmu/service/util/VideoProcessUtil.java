/**
 * 
 *===========================================================================
 *
 *
 * Heelo.java
 *
 *
 * The source code for this program is not published or otherwise divested of
 * its trade secrets, irrespective of what has been deposited with the U.S.
 * Copyright office.
 * ===========================================================================
 * Module Information:
 *
 * DESCRIPTION: 
 * ===========================================================================
 * 
 */

package com.zongmu.service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.UUID;

public class VideoProcessUtil {
	
	public static final int SUCCESS = 0;
    public static final String SUCCESS_MESSAGE = "Video processed successfully!";
    public static final String ERROR_MESSAGE = "Video process failed!";
    public static final String VP_SHELL="/ffmpeg_workspace/vp.ksh";
	/**
	 * 
	 * @param videoCode
	 * @param videoPath
	 * @param bv
	 * @param ba
	 * @return
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static String procesVideo(String videoCode,String videoPath,String bv,String ba) throws IOException, InterruptedException{
		ProcessBuilder pb=new ProcessBuilder(VP_SHELL,videoCode,videoPath,bv,ba);
		Process process=pb.start();
		 // 打印程序输出
        readProcessOutput(process);

        // 等待程序执行结束并输出状态
        int exitCode = process.waitFor();
        if (exitCode == SUCCESS) {
            System.out.println(SUCCESS_MESSAGE);
        } else {
            System.err.println(ERROR_MESSAGE + exitCode);
        }
		return videoCode;
	}
	
	/**
     * 打印进程输出
     *
     * @param process 进程
     */
    private static void readProcessOutput(final Process process) {
        // 将进程的正常输出在 System.out 中打印，进程的错误输出在 System.err 中打印
        read(process.getInputStream(), System.out);
        read(process.getErrorStream(), System.err);
    }
    
	// 读取输入流
    private static void read(InputStream inputStream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	public static void main(String[] args) {
		try {
			String videoCode=UUID.randomUUID().toString();
			VideoProcessUtil.procesVideo(videoCode,"/ffmpeg_workspace/sample/input.mp4", "500k", "128k");
		} catch (Exception e) {
			System.err.println("process video failed!");
			e.printStackTrace();
		}
	}
}
