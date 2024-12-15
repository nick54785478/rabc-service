package com.example.demo.iface.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupsAuthQueriedResource {

	private String username;
	
	private String email;

	private List<String> roles;

	private List<String> functions;
}
