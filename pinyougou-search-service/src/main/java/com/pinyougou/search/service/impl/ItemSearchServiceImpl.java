package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	
	@Override
	public Map<String, Object> search(Map<String, Object> searchMap) {
		Map<String, Object> map=new HashMap<>();
		//关键字空格处理
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords",keywords.replace(" ",""));
		//1.(高亮)关键字查询
		map.putAll(searchList(searchMap));
		//2.查询分类
		List<String> categoryList=searchCategoryList(searchMap);
		map.put("categoryList",categoryList);
		//3.查询品牌和规格列表
		String categoryName = (String)searchMap.get("category");
		if(!"".equals(categoryName)){//如果有分类名称--按分类名称查询
			map.putAll(searchBrandAndSpecList(categoryName));
		}else {//没有分类名称--按第一个查询
			if(categoryList.size()>0){
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}

		
		return map; 
	}

	/**
	 * 批量导入数据到solr库
	 * @param list
	 */
	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}

	/**
	 * 删除solr库中数据
	 * @param goodsIdList
	 */
	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		System.out.println("删除商品ID:"+goodsIdList);
		Query query=new SimpleQuery();
		Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}

	/**
	 * 高亮显示
	 * @param searchMap
	 * @return
	 */
	private Map searchList(Map searchMap){
		//1.1高亮查询对象
		HighlightQuery query=new SimpleHighlightQuery();
		//1.2高亮选项
		HighlightOptions options=new HighlightOptions().addField("item_title");//1.2.1添加高亮字段
		options.setSimplePrefix("<em style='color:red'>");//1.2.2添加高亮前缀
		options.setSimplePostfix("</em>");//1.2.3.添加高亮后缀
		query.setHighlightOptions(options);//设置高亮选项
		//1.3 条件对象
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);//设置查询条件
		//1.3.2按商品分类过滤
		if(!"".equals(searchMap.get("category"))){
			Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));//过滤查询条件
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);//过滤查询对象
			query.addFilterQuery(filterQuery);//设置过滤查询
		}
		//1.3.3按商品品牌过滤
		if(!"".equals(searchMap.get("brand"))){
			Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));//过滤查询条件
			FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);//过滤查询对象
			query.addFilterQuery(filterQuery);//设置过滤查询
		}
		//1.3.4按商品分类过滤
		if(searchMap.get("spec")!=null){
			Map<String,String> specMap=(Map)searchMap.get("spec");
			for (String key : specMap.keySet()) {
				Criteria filterCriteria=new Criteria("item_spec_"+ key).is(searchMap.get(key));//过滤查询条件
				FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);//过滤查询对象
				query.addFilterQuery(filterQuery);//设置过滤查询
			}

		}
		//1.3.5按价格区间过滤
		if(!"".equals(searchMap.get("price"))){
			String str=(String)searchMap.get("price");
			String[] price= str.split("-");
			if(!price[0].equals("0")){
				Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);//过滤查询条件
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);//过滤查询对象
				query.addFilterQuery(filterQuery);//设置过滤查询
			}
			if(!price[1].equals("*")){
				Criteria filterCriteria=new Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		
		//1.3.6分页查询
		Integer pageNo = (Integer)searchMap.get("pageNo");
		if(pageNo==null){
			pageNo=1;
		}
		Integer pageSize=(Integer)searchMap.get("pageSize");
		if(pageSize==null){
			pageSize=40;
		}
		query.setOffset((pageNo-1)*pageSize);
		query.setRows(pageSize);
		//1.3.7排序查询
		String sortDirection=(String)searchMap.get("sort");
		String sortField=(String)searchMap.get("sortField");
		if(sortDirection!=null&&!"".equals(sortDirection)){
			if(sortDirection.equals("ASC")){
				Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
				query.addSort(sort);
			}
			if(sortDirection.equals("DESC")){
				Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
				query.addSort(sort);
			}
		}
		//****************************获得高亮结果集****************************
		//1.4.高亮查询
			//1.4.1高亮页对象
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
			//1.4.2高亮入口集合---集合的一个元素(高亮入口)就是一条记录
		List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
			//遍历集合--取得每条记录,然后添加高亮样式
		for (HighlightEntry<TbItem> entry : entryList) {
			//1.4.3高亮列表(高亮域的个数)
			List<HighlightEntry.Highlight> highlights = entry.getHighlights();
			/*for (HighlightEntry.Highlight highlight : highlights) {
				//4.4高亮片段(集合)----高亮片段集合就一个高亮域,集合的一个元素就是域中的一个值
				List<String> snipplets = highlight.getSnipplets();
				System.out.println(snipplets);
				
			}*/
			if(highlights.size()>0&&highlights.get(0).getSnipplets().size()>0){
				TbItem item = entry.getEntity();
				item.setTitle(highlights.get(0).getSnipplets().get(0));
			}
		}
		//返回结果
		Map<String,Object> map=new HashMap<>();
		map.put("rows",page.getContent());
		map.put("totalPages",page.getTotalPages());//返回总页数
		map.put("total",page.getTotalElements());//总记录数
		return  map;
	}
	/**
	 * (solr分组)查询商品分类列表
	 */
	private List<String> searchCategoryList(Map searchMap){
		List<String> list=new ArrayList<>();
		//1查询对象
		Query query=new SimpleQuery("*:*");
		//2条件对象
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);//设置条件对象
		//3.分组选项
		GroupOptions options = new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(options);//设置分组选项
		//4.查询
		//4.1分组页对象
		GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
		//4.2分组结果对象
		GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
		//4.3分组入口页对象
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		//4.4分组入口集合
		List<GroupEntry<TbItem>> entryList = groupEntries.getContent();
		//4.5遍历获得分组入口对象
		for (GroupEntry<TbItem> entry : entryList) {
			list.add(entry.getGroupValue());//将分组结果添加到返回值中
			System.out.println(entry.getGroupValue());
		}
		return list;
	}

	/**
	 * 根据商品分类名称 读取缓存中的 品牌列表 和 规格列表
	 */
	private  Map searchBrandAndSpecList(String category){
		Map map=new HashMap();
		//根据商品名称获得模板id
		Long typeId = (Long)redisTemplate.boundHashOps("itemCat").get(category);
		if(typeId!=null){
			//根据模板id查询品牌列表
			List brandList =(List) redisTemplate.boundHashOps("brandList").get(typeId);
			map.put("brandList",brandList);
			//根据模板id查询规格列表
			List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
			map.put("specList",specList);
		}
		return  map;
	}
}
