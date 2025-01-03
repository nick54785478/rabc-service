package com.example.demo.domain.role.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleCommand {

	private Long id;

	private String code; 

	private String name;
	
	private String type;

	private String description;
	
}
