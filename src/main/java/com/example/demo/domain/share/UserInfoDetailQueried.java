package com.example.demo.domain.share;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDetailQueried {

	private Long id;

	private String name;

	private String email; // 信箱

	private String username; // 帳號

	private String address;
	
	private String birthday;
	
	private String nationalIdNo;

	private List<UserGroupQueried> groups;

	private List<UserRoleQueried> roles;
	
	private List<FunctionInfoDetailQueried> functions;

}
