package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper tbBrandMapper;
	
	@Override
	public List<TbBrand> findAll() {
		return tbBrandMapper.selectByExample(null);
	}

	/**
	 * 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageResult findPage(TbBrand tbBrand,int pageNum, int pageSize) {
		PageHelper.startPage(pageNum,pageSize);
		//构建查询条件
		TbBrandExample tbBrandExample = new TbBrandExample();
		TbBrandExample.Criteria criteria = tbBrandExample.createCriteria();
		if(tbBrand!=null){
			if(tbBrand.getName()!=null&&tbBrand.getName().length()>0){
				criteria.andNameLike("%"+tbBrand.getName()+"%");
			}
			if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
				criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
			}
		}
		Page<TbBrand> list = (Page<TbBrand>) tbBrandMapper.selectByExample(tbBrandExample);
		return new PageResult(list.getTotal(),list.getResult());
	}
	
	@Override
	public void save(TbBrand tbBrand) {
		if(tbBrand.getId()==null){
			tbBrandMapper.insert(tbBrand);
		}else{
			tbBrandMapper.updateByPrimaryKeySelective(tbBrand);
		}
		
	}

	@Override
	public TbBrand findOne(Long id) {
		return tbBrandMapper.selectByPrimaryKey(id);
	}

	/**
	 * 删除
	 * @param ids
	 */
	@Override
	public void deleteMulti(Long[] ids) {
		for (Long id : ids) {
			tbBrandMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public List<Map> select2OptionList() {
		return tbBrandMapper.select2OptionList();
	}
}
