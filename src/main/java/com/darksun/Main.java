package com.darksun;

import com.darksun.controller.ai.CustomerSupportAgent;
import com.darksun.controller.ai.tools.OrderCardTools;
import com.darksun.controller.ai.tools.OrderItemTools;
import com.darksun.controller.ai.tools.ProductTools;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static dev.langchain4j.data.document.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

@SpringBootApplication
public class Main implements CommandLineRunner {

	@Bean
	CustomerSupportAgent customerSupportAgent( ChatLanguageModel chatLanguageModel,
											   OrderCardTools orderCardTools,
											   ProductTools productTools,
											   OrderItemTools orderItemTools,
											   Retriever< TextSegment > retriever ) {
		return AiServices.builder( CustomerSupportAgent.class )
						 .chatLanguageModel( chatLanguageModel )
						 .chatMemory( MessageWindowChatMemory.withMaxMessages( 20 ) )
						 .tools( orderCardTools, productTools, orderItemTools )
						 .retriever( retriever )
						 .build( );
	}

	@Bean
	Retriever< TextSegment > retriever( EmbeddingStore< TextSegment > embeddingStore,
										EmbeddingModel embeddingModel ) {

		int    maxResultsRetrieved = 1;
		double minScore            = 0.9;

		return EmbeddingStoreRetriever.from( embeddingStore, embeddingModel, maxResultsRetrieved,
											 minScore );
	}

	@Bean
	EmbeddingStore< TextSegment > embeddingStore( EmbeddingModel embeddingModel,
												  ResourceLoader resourceLoader ) throws
																				  IOException {

		EmbeddingStore< TextSegment > embeddingStore = new InMemoryEmbeddingStore<>( );

		Resource resource = resourceLoader.getResource(
				"classpath:restaurant-tech-terms-of-use.txt" );
		Document document = loadDocument( resource.getFile( ).toPath( ) );

		DocumentSplitter documentSplitter = DocumentSplitters.recursive( 100, new OpenAiTokenizer(
				GPT_3_5_TURBO ) );
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder( )
																.documentSplitter(
																		documentSplitter )
																.embeddingModel( embeddingModel )
																.embeddingStore( embeddingStore )
																.build( );
		ingestor.ingest( document );

		return embeddingStore;
	}

	public static void main( String[] args ) {
		SpringApplication.run( Main.class, args );
	}

	@Autowired
	ApplicationContext context;

	@Override
	public void run( String... args ) throws Exception {
		BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) );

		CustomerSupportAgent agent = context.getBean( CustomerSupportAgent.class );

		String line = read( reader );
		while ( !line.equals( "" ) ) {
			interact( agent, line );
			line = read( reader );
		}
	}

	private static String read( BufferedReader reader ) throws IOException {
		System.out.println(
				"==========================================================================================" );
		System.out.println( "[User]: " );
		String line = reader.readLine( );
		System.out.println(
				"==========================================================================================" );
		return line;
	}

	private static void interact( CustomerSupportAgent agent, String userMessage ) {
		String agentAnswer = agent.chat( userMessage );
		System.out.println(
				"==========================================================================================" );
		System.out.println( "[Agent]: " + agentAnswer );
	}
}
