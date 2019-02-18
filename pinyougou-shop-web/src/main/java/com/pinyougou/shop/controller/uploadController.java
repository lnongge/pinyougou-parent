package com.pinyougou.shop.controller;

import com.pinyougou.common.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class uploadController {
	
	//依赖注入fastdfs的服务器地址
	@Value("${File_SERVER_URL}")
	private String File_SERVER_URL;
	

	@RequestMapping("/pic")
	public Result upload(MultipartFile file){
		//取文件扩展名
		String originalFilename = file.getOriginalFilename();
		String extName=originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		//上传
		try {
			//创建FastDFS客户端
			FastDFSClient fastDFSClient=new FastDFSClient("classpath:fdfs_client.conf");
			//上传图片
			String file_id = fastDFSClient.uploadFile(file.getBytes(), extName);
			//拼接图片路径
			String url=File_SERVER_URL+file_id;
			return new Result(true,url);
		}catch (Exception e){
			e.printStackTrace();
			return  new Result(false,"上传失败!");
		}
	}
}
