package com.example.demo.iface.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOptionQueriedResource {

	private Long id;

	private String name;

	private String username; // 帳號
}
