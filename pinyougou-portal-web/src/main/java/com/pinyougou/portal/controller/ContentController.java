package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
	
	@Reference
	private ContentService contentService;
	
	
	
	@RequestMapping("/findByCategoryId")
	public List<TbContent> findByCategoryId(Long categoryId){
		return contentService.findByCategoryId(categoryId);
	}
	
	@RequestMapping("/test")
	public List<String> test1(){
		List<String> list=new ArrayList<>();
		list.add("zhangsna");
		list.add("lisi");
		list.add("wangwu");
		
		return list;
	}
}
