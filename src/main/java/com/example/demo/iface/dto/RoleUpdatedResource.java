package com.example.demo.iface.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdatedResource {

	private Long id;
	
	private String code;

	private String name;
	
	private String type;

	private String description; // 敘述

	private String activeFlag; // 是否有效
}
