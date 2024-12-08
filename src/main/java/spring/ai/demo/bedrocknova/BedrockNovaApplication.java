package spring.ai.demo.bedrocknova;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.model.Media;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class BedrockNovaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BedrockNovaApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ChatClient.Builder clientBuilder) {

		ChatClient chatClient = clientBuilder.build();

		return args -> {

			System.out.println("---------------- Text -----------------------");
			// Text
			System.out.println("Text response: " + chatClient.prompt("Who are you?").call().content());

			System.out.println("---------------- Image ----------------------");
			String imageResponse = chatClient.prompt()
				.user(u -> u.text("Explain what do you see on this picture?")
					.media(Media.Format.IMAGE_PNG, new ClassPathResource("/test.png")))
				.call()
				.content();
			System.out.println("Image response: " + imageResponse);

			System.out.println("---------------- Video ----------------------");
			String videoResponse = chatClient.prompt()
				.user(u -> u.text("Explain what do you see in this video?")
					.media(Media.Format.VIDEO_MP4, new ClassPathResource("/test.video.mp4")))
				.call()
				.content();
			System.out.println("Video response: " + videoResponse);

			System.out.println("---------------- PDF ----------------------");

			String docResponse = chatClient.prompt()
				.user(u -> u.text(
						"You are a very professional document summarization specialist. Please summarize the given document.")
					.media(Media.Format.DOC_PDF, new ClassPathResource("/spring-ai-reference-overview.pdf")))
				.call()
				.content();
			System.out.println("Doc response: " + docResponse);

			System.out.println("---------------- Tools ----------------------");

			record WeatherRequest(String city, String unit) {
			}
			record WeatherResponse(int temperature, String unit) {
			}

			String toolResponse = chatClient.prompt()
				.user("what to wear in Amsterdam today?")
				.functions(FunctionCallback.builder()
					.function("getCurrentWeather", (WeatherRequest request) -> new WeatherResponse(15, request.unit()))
					.description("Gets current weather by city name")
					.inputType(WeatherRequest.class)
					.build())
				.call()
				.content();

			System.out.println("Tool response: " + toolResponse);

		};
	}

}
