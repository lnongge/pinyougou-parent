package com.pinyougou.service;

import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	
	private SellerService sellerService;

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//角色
		List<GrantedAuthority> grantedAuthorities=new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		//到数据库去查询
		TbSeller seller = sellerService.findOne(username);
		if(seller!=null && seller.getStatus().equals("1")){
			System.out.println(111);
			//只有用户存在且通过审核才能登陆
			return new User(username,seller.getPassword(),grantedAuthorities);
		}else{
			System.out.println(222);
			return null;
		}

		
	}
}
