package com.alura.literaluraOracle;

import com.alura.literaluraOracle.main.Principal;
import com.alura.literaluraOracle.repository.AutorRepository;
import com.alura.literaluraOracle.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraOracleApplication implements CommandLineRunner {

    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutorRepository autorRepository;

    public static void main(String[] args) {
        SpringApplication.run(LiteraluraOracleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal(libroRepository, autorRepository);
        principal.menu();
    }
}
