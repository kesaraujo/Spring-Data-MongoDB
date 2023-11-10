package com.fiap.springblog.service.implement;

import com.fiap.springblog.model.Autor;
import com.fiap.springblog.repository.IAutorRepository;
import com.fiap.springblog.service.IAutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorServiceImplement implements IAutorService {

    @Autowired
    private IAutorRepository autorRepository;


    @Override
    public Autor criar(Autor autor) {
        return this.autorRepository.save(autor);
    }


    @Override
    public Autor obterPorCodigo(String codigo) {
        return this.autorRepository.findById(codigo).orElseThrow(
                () -> new IllegalArgumentException("Autor não encontrado")
        );
    }

    @Override
    public List<Autor> listarAutor() {
        return this.autorRepository.findAll();
    }
}
