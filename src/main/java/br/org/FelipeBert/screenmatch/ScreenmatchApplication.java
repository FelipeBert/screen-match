package br.org.FelipeBert.screenmatch;

import br.org.FelipeBert.screenmatch.principal.Principal;
import br.org.FelipeBert.screenmatch.repository.SeriesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

    private SeriesRepository seriesRepository;

    public ScreenmatchApplication(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(seriesRepository);
		principal.exibiMenu();
	}
}
