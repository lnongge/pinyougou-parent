package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface BrandService {
   public List<TbBrand> findAll();
   /**
    * 品牌分页查询
    */
   public PageResult findPage(TbBrand tbBrand,int pageNum,int pageSize);
   /**添加
    */
   public void save(TbBrand tbBrand);
	/**
	 * 修改
	 */
	public TbBrand findOne(Long id);
	/**
	 * 删除
	 */
	public void deleteMulti(Long[] ids);

	/**
	 * 模板管理中的select2的关联品牌的下拉选项
	 */
	public List<Map> select2OptionList();
}
