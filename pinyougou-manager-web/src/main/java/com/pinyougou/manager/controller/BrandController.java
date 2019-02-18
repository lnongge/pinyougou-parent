package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
	
	@Reference(timeout = 5000)
	private BrandService brandService;
	
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}

	/**
	 * 分页查询 和搜索方法
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand tbBrand, int pageNum,int pageSize){
		return brandService.findPage(tbBrand,pageNum,pageSize);
	}

	/**
	 * 添加
	 */
	@RequestMapping("/save")
	public Result save(@RequestBody  TbBrand tbBrand){
		try{
			brandService.save(tbBrand);
			return new Result(true,"添加成功");
		}catch (Exception e){
			e.printStackTrace();
			return new Result(false,"添加失败");
		}
	}
	/**
	 * 修改前查找
	 */
	@RequestMapping("/findOne")
	public TbBrand findOne(Long id){
		System.out.println(id);
		return brandService.findOne(id);
	}
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids){
		try{
			brandService.deleteMulti(ids);
			return new Result(true,"删除成功");
		}catch (Exception e){
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	/**
	 * select2的下拉选项
	 */
	@RequestMapping("/findLinkedBrand")
	public List<Map> findLinkedBrand(){
		return  brandService.select2OptionList();
	}
}
