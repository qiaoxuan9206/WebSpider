
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
 * ���ĵĲ�ѯ��
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

		// ���ж�rule�ı�ҪУ��
		validateRule(rule);

		List<LinkTypeData> datas = new ArrayList<LinkTypeData>();
		LinkTypeData data = null;
		try
		{
			/**
			 * ����rule
			 */
			String url = rule.getUrl();
			String[] params = rule.getParams();
			String[] values = rule.getValues();
			String resultTagName = rule.getResultTagName();
			int type = rule.getType();
			int requestType = rule.getRequestMoethod();

			Connection conn = Jsoup.connect(url);
			// ���ò�ѯ����
			if (params != null&&params.length>1)
			{
				for (int i = 0; i < params.length; i++)
				{
					conn.data(params[i], values[i]);
				}
			}

			// ������������
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

			//����������
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
				//��resultTagNameΪ��ʱĬ��ȥbody��ǩ
				if (TextUtil.isEmpty(resultTagName))
				{
					results = doc.getElementsByTag("body");
				}
			}

			for (int i = 0; i < results.size(); i++) {
				Element result = results.get(i);
				//�������������ô<td>(2017/10/19)</td>����ʱ�䣬�������˴�ѭ�� 
				if(i%2 == 1){     
			        continue;   
			    } 
				//����html�Ĺ����ȡ���Ӻͱ���
				Elements links = result.getElementsByTag("a");
				String linkHref = "";
				String linkText = "";
				for (Element link : links) {
					linkHref = link.attr("href");
					linkText = link.text();
				}
				//ȡ����Ӧ��ʱ��
				Element	result1 = results.get(i+1);
				String datetime = result1.getElementsByTag("td").get(0).text();
				//��html���������ӣ������ʱ��ŵ�������Ѷ���ŵ�������
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
	 * �Դ���Ĳ������б�Ҫ��У��
	 */
	private static void validateRule(Rule rule)
	{
		String url = rule.getUrl();
		if (TextUtil.isEmpty(url))
		{
			throw new RuleException("url����Ϊ�գ�");
		}
		if (!url.startsWith("http"))
		{
			throw new RuleException("url�ĸ�ʽ����ȷ��");
		}

		if (rule.getParams() != null && rule.getValues() != null)
		{
			if (rule.getParams().length != rule.getValues().length)
			{
				throw new RuleException("�����ļ�ֵ�Ը�����ƥ�䣡");
			}
		}

	}


}

