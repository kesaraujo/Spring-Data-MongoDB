package com.fiap.springblog.service;

import com.fiap.springblog.model.Autor;

import java.util.List;

public interface IAutorService {

    public Autor criar(Autor autor);
    public Autor obterPorCodigo(String codigo);
    public List<Autor> listarAutor();

}
