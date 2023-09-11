package com.letsparty.web.form;

import java.util.List;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class PollOptionForm {

	private List<String> items;
}
