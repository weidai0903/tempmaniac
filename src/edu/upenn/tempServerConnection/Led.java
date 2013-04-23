package edu.upenn.tempServerConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Led {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public Led() {
		
	}
	
	public boolean ledShowTemp() {
		try {
			String request;
			request = "ledShowTemp";
			String r = sendRequest(request);
			if(r.contains("success")) {
				return true;
			}else {
				return false;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Return true if success;
	 * Return false if fail;
	 * @return
	 */
	public boolean openLed() {
		try {
			String request;
			request = "openled";
			String r = sendRequest(request);
			if(r.contains("success")) {
				return true;
			}else {
				return false;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Return true if success;
	 * Return false if fail;
	 * @return
	 */
	public boolean closeLed() {
		try {
			String request;
			request = "closeled";
			String r = sendRequest(request);
			if(r.contains("success")) {
				return true;
			}else {
				return false;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * color could be "red", "blue", "green", "black"
	 * Return true if success;
	 * Return false if fail;
	 * @param text
	 * @param color
	 * @return
	 */
	public boolean showTextOnLed(String text) {
		try {
			String request;
			request = "ledtext=" + text + "/";
			String r = sendRequest(request);
			if(r.contains("success")) {
				return true;
			}else {
				return false;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean showLoveOnLed() {
		try {
			String request;
			request = "love";
			String r = sendRequest(request);
			if(r.contains("success")) {
				return true;
			}else {
				return false;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean showSnakeOnLed(){
		try {
			String request;
			request = "snake";
			String r = sendRequest(request);
			if(r.contains("success")) {
				return true;
			}else {
				return false;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public boolean showNumOnLed(String text) {
		try {
			String request;
			request = "led=" + text;
			String r = sendRequest(request);
			if(r.contains("success")) {
				return true;
			}else {
				return false;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean setColorOnLed(boolean red) {
		try {
			String request;
			if(red) {
				request = "color=0";
			}else {
				request = "color=1";
			}
			String r = sendRequest(request);
			if(r.contains("success")) {
				return true;
			}else {
				return false;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String sendRequest(String request) throws IOException {
		
		String serverIP = Server.getServerIP();
		String addr = serverIP + request;
		URL url = new URL(addr);
		System.out.println("sending request:" + addr + " to server");
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(3*1000);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        System.out.println("receiving data:");
        String inputLine;
        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        	System.out.println(inputLine);
        }
        in.close();
        
        String receive = sb.toString();
        //System.out.println("stringbulder:"+receive);
        return receive;
	}
	
	

}
