package com.zhy.spider.util;
/**
 * 
 * @author Jack
 *
 */
public class TextUtil {  
	
    public static boolean isEmpty(String url){  
        if(url.trim().length()>0){  
            return false;  
        }else{  
            return true;  
        }  
    }  
}
