package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
	
	@Autowired 
	private TbItemMapper itemMapper;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	public  void importItemData() {

		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");//已审核的商品才查询
		List<TbItem> itemList = itemMapper.selectByExample(example);
		System.out.println("查询开始");
		for (TbItem item : itemList) {
			System.out.println(item.getTitle());
			Map map = JSON.parseObject(item.getSpec());
			item.setSpecMap(map);
		}
		System.out.println("======end 查询");
		//保存到solr
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
	}
	//删除
	public void deleteAll(){
		Query query= new SimpleQuery("*:*");
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
	public static void main(String[] args) {
		ApplicationContext applicationContext=
				new ClassPathXmlApplicationContext("classpath*:applicationContext*.xml");
		SolrUtil solrUtil = (SolrUtil)applicationContext.getBean("solrUtil");
		solrUtil.importItemData();
		//solrUtil.deleteAll();
	}
}
