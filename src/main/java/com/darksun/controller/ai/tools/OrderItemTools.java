package com.darksun.controller.ai.tools;

import com.darksun.model.OrderItem;
import com.darksun.service.OrderItemService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemTools {

	@Autowired
	OrderItemService service;

	@Tool
	public List< OrderItem > readByOrderCardId( Long id ) {
		return service.readByOrderCardId( id );
	}

	@Tool
	public void addItem( Long productId, Long orderCardId, Integer quantity ) {
		for ( int i = 0; i < quantity; i++ ) {
			service.addItem( productId, orderCardId );
		}
	}

	@Tool
	public void removeItem( Long productId, Long orderCardId, Integer quantity ) {
		for ( int i = 0; i < quantity; i++ ) {
			service.removeItem( productId, orderCardId );
		}
	}
}
