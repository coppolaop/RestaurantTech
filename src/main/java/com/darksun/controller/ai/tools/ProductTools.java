package com.darksun.controller.ai.tools;

import com.darksun.model.Product;
import com.darksun.service.ProductService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductTools {

	@Autowired
	ProductService service;

	@Tool
	public List< Product > readAllProducts( ) {
		return service.readAll( );
	}

	@Tool
	public Product readProductById( Long id ) {
		return service.readById( id );
	}

	@Tool
	public List< Product > readAllByNameContaining( String name ) {
		return service.readAllByNameContaining( name );
	}
}
