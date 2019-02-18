package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
	/**
	 * 按关键字搜索
	 */
	public Map<String,Object> search(Map<String,Object> searchMap);

	/**
	 * 批量导入数据到slor索引库
	 * @param list
	 */
	public void importList(List list);

	/**
	 * 删除solr库中数据
	 * @param goodsIdList
	 */
	public void deleteByGoodsIds(List goodsIdList);
}
