package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojowrapper.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;
	
	@Autowired
	private TbItemMapper tbItemMapper;
	
	@Autowired
	private TbBrandMapper tbBrandMapper;
	
	@Autowired
	private TbItemCatMapper tbItemCatMapper;
	
	@Autowired
	private TbSellerMapper tbSellerMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//设置商品状态 0新商品(未申请) 1申请中,2审核通过
		goods.getTbGoods().setAuditStatus("0");
		goods.getTbGoods().setIsDelete("0");
		goodsMapper.insert(goods.getTbGoods());	
		//保存good_desc
		goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());
		tbGoodsDescMapper.insert(goods.getTbGoodsDesc());
		//保存sku
		saveItemList(goods);
	}
	//保存sku的方法
	private void saveItemList(Goods goods) {
		if ("1".equals(goods.getTbGoods().getIsEnableSpec())) {
			//保存tb_item
			for (TbItem item : goods.getItemList()) {
				//标题 =sku名+各个规格值
				String title = goods.getTbGoods().getGoodsName();//sku名
				Map<String, Object> specMap = JSON.parseObject(item.getSpec());//规格
				for (String key : specMap.keySet()) {
					title += " " + specMap.get(key);
				}
				item.setTitle(title);
				setItemValues(goods, item);

				tbItemMapper.insert(item);
			}
		}else{//不启用规格--只有一种产品
			TbItem item=new TbItem();
			item.setTitle(goods.getTbGoods().getGoodsName());//商品名称
			item.setPrice(goods.getTbGoods().getPrice());//价格
			item.setStatus("1");//状态
			item.setIsDefault("1");//是否默认
			item.setNum(99999);//库存
			item.setSpec("{}");
			setItemValues(goods,item);
			tbItemMapper.insert(item);
		}
	}

	private void setItemValues(Goods goods, TbItem item) {
		//spu编号
		item.setGoodsId(goods.getTbGoods().getId());
		//商家编号
		item.setSellerId(goods.getTbGoods().getSellerId());
		//商品分类编号(3级)
		item.setCategoryid(goods.getTbGoods().getCategory3Id());
		//创建日期,修改日期
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		//品牌名称
		TbBrand brand = tbBrandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
		item.setBrand(brand.getName());
		//分类名称
		TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
		item.setCategory(tbItemCat.getName());
		//商家名称
		TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
		item.setSeller(tbSeller.getNickName());
		//图片地址(取第一个spu图片地址)
		List<Map> imgList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), Map.class);
		if (imgList.size() > 0) {
			item.setImage((String) imgList.get(0).get("url"));
		}
	}

	@Override
	public void add(TbGoods goods) {
		goodsMapper.insert(goods);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//更新goods
		goods.getTbGoods().setAuditStatus("0");//经过修改的商品需要重新审核.故重新设置状态
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());
		//更新goodsdesc
		tbGoodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
		//更新sku
		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		tbItemMapper.deleteByExample(example);//先删除
		saveItemList(goods);//再添加
	}

	@Override
	public void updateStatus(Long[] ids,String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods=new Goods();
		goods.setTbGoods(goodsMapper.selectByPrimaryKey(id));
		goods.setTbGoodsDesc(tbGoodsDescMapper.selectByPrimaryKey(id));
		//获取sku的值
		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = tbItemMapper.selectByExample(example);
		goods.setItemList(tbItems);
		return goods;
	}

	/**
	 * 批量删除--逻辑删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//goodsMapper.deleteByPrimaryKey(id);
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			/*if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}*/
			criteria.andIsDeleteNotEqualTo("1");//未删除的商品
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void pushOrPull(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsMarketable(status);
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	/**
	 * 根据spu的id和状态查找sku信息
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	@Override
	public List<TbItem> findItemListByGoodsIdAndStatus(Long[] goodsIds, String status) {
		TbItemExample example=new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(goodsIds));
		criteria.andStatusEqualTo(status);
		return tbItemMapper.selectByExample(example);
	}

}
