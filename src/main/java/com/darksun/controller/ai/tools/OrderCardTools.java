package com.darksun.controller.ai.tools;

import com.darksun.model.OrderCard;
import com.darksun.service.OrderCardService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderCardTools {

	@Autowired
	OrderCardService service;

	@Tool
	public OrderCard create( ) {
		return service.create( null );
	}
}
