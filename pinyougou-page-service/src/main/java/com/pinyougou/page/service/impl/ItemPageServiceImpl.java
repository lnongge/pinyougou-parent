package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
	
	@Value("${pageDir}")
	private String pageDir;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	/**
	 * 用freemaker生成商品详情页
	 */
	@Override
	public boolean genItemHtml(Long goodsId) {
		try{
			//配置对象
			Configuration configuration=freeMarkerConfigurer.getConfiguration();
			//模板
			Template template = configuration.getTemplate("item.ftl");
			//数据
			Map<String,Object> map=new HashMap<>();
			TbGoods tbGood = goodsMapper.selectByPrimaryKey(goodsId);//商品表数据
			map.put("goods",tbGood);
			TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);//扩展表数据
			map.put("goodsDesc",tbGoodsDesc);
			String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGood.getCategory1Id()).getName();//商品分类数据
			String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGood.getCategory2Id()).getName();
			String itemCat3 =itemCatMapper.selectByPrimaryKey(tbGood.getCategory3Id()).getName();
			map.put("itemCat1",itemCat1);
			map.put("itemCat2",itemCat2);
			map.put("itemCat3",itemCat3);
			TbItemExample example=new TbItemExample();//sku数据
			TbItemExample.Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId);
			criteria.andStatusEqualTo("1");
			example.setOrderByClause("is_default desc");//按是否默认排序降序排序,确保第一个
			List<TbItem> itemList = itemMapper.selectByExample(example);
			map.put("itemList",itemList);
			//生成静态页面
			template.process(map,new FileWriter(pageDir+goodsId+".html"));
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
		
	}
	
}
