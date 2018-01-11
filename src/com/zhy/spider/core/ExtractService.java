
package com.zhy.spider.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.zhy.spider.bean.LinkTypeData;
import com.zhy.spider.rule.Rule;
import com.zhy.spider.rule.RuleException;
import com.zhy.spider.util.TextUtil;

/**
 * 核心的查询类
 * @author Jack
 * 
 */
public class ExtractService
{
	/**
	 * @param rule
	 * @return
	 */
	public static List<LinkTypeData> extract(Rule rule)
	{

		// 进行对rule的必要校验
		validateRule(rule);

		List<LinkTypeData> datas = new ArrayList<LinkTypeData>();
		LinkTypeData data = null;
		try
		{
			/**
			 * 解析rule
			 */
			String url = rule.getUrl();
			String[] params = rule.getParams();
			String[] values = rule.getValues();
			String resultTagName = rule.getResultTagName();
			int type = rule.getType();
			int requestType = rule.getRequestMoethod();

			Connection conn = Jsoup.connect(url);
			// 设置查询参数
			if (params != null&&params.length>1)
			{
				for (int i = 0; i < params.length; i++)
				{
					conn.data(params[i], values[i]);
				}
			}

			// 设置请求类型
			Document doc = null;
			switch (requestType)
			{
			case Rule.GET:
				doc = conn.timeout(100000).get();
				break;
			case Rule.POST:
				doc = conn.timeout(100000).post();
				break;
			}

			//处理返回数据
			Elements results = new Elements();
			switch (type)
			{
			case Rule.CLASS:
				results = doc.getElementsByClass(resultTagName);
				break;
			case Rule.ID:
				Element result = doc.getElementById(resultTagName);
				results.add(result);
				break;
			case Rule.SELECTION:
				results = doc.select(resultTagName);
				break;
			default:
				//当resultTagName为空时默认去body标签
				if (TextUtil.isEmpty(resultTagName))
				{
					results = doc.getElementsByTag("body");
				}
			}

			for (int i = 0; i < results.size(); i++) {
				Element result = results.get(i);
				//如果是奇数，那么<td>(2017/10/19)</td>里是时间，则跳出此次循环 
				if(i%2 == 1){     
			        continue;   
			    } 
				//根据html的规则截取链接和标题
				Elements links = result.getElementsByTag("a");
				String linkHref = "";
				String linkText = "";
				for (Element link : links) {
					linkHref = link.attr("href");
					linkText = link.text();
				}
				//取出对应的时间
				Element	result1 = results.get(i+1);
				String datetime = result1.getElementsByTag("td").get(0).text();
				//把html解析得链接，标题和时间放到对象里，把对象放到集合中
				data = new LinkTypeData();
				data.setLinkHref(linkHref);
				data.setLinkText(linkText);
				data.setDatetime(datetime);
				datas.add(data);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return datas;
	}

	/**
	 * 对传入的参数进行必要的校验
	 */
	private static void validateRule(Rule rule)
	{
		String url = rule.getUrl();
		if (TextUtil.isEmpty(url))
		{
			throw new RuleException("url不能为空！");
		}
		if (!url.startsWith("http"))
		{
			throw new RuleException("url的格式不正确！");
		}

		if (rule.getParams() != null && rule.getValues() != null)
		{
			if (rule.getParams().length != rule.getValues().length)
			{
				throw new RuleException("参数的键值对个数不匹配！");
			}
		}

	}


}

