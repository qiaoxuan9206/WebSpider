
package com.zhy.spider.test;

import java.util.List;

import com.zhy.spider.bean.LinkTypeData;
import com.zhy.spider.core.ExtractService;
import com.zhy.spider.rule.Rule;

public class Test
{
	public static void main(String[] args) {
		Rule rule = new Rule("https://www.sdic.com.cn/cn/zxzx/qydt/A010202index_1.htm",  
			      new String[] { "" }, 
			      new String[] { "" },  
			      "ywyj_title_02",
			      Rule.CLASS, 
			      Rule.GET);
		
		List<LinkTypeData> extracts = ExtractService.extract(rule);
		for (LinkTypeData data : extracts)
		{
			System.out.println(data.getLinkText());
			System.out.println(data.getLinkHref());
			System.out.println(data.getDatetime());
			System.out.println("************************************");
		}
	}

	
}

