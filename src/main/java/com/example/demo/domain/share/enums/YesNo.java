package com.example.demo.domain.share.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public enum YesNo {

	Y("Y"), N("N");
	
	@Getter
	private final String value;
	
}
