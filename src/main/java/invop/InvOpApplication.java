package invop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import invop.TestConnection;

import java.time.LocalDate;

@SpringBootApplication
public class InvOpApplication {

	public static void main(String[] args) {


		SpringApplication.run(InvOpApplication.class, args);
		TestConnection.main(args);

		//calcularDemandaHistorica();
	}


}
