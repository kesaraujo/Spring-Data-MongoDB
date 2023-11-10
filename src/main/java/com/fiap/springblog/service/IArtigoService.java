package com.fiap.springblog.service;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IArtigoService {

    //Consultas Padrão do MongoDB
    public List<Artigo> obterTodos();
    public Artigo obterPorCodigo(String codigo);
    public void atualizarArtigo(Artigo artigo);

    //    public Artigo criarArtigo(Artigo artigo);


    //
    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor);



    // Consultas com tratamento de erro
    //public ResponseEntity<?> criar(Artigo artigo);
    public ResponseEntity<?> atualizarArtigo(String codigo, Artigo artigo);


    // Consultas Personalizadas do Desenvolvedor
    public List<Artigo> findByDataGreaterThan(LocalDateTime data);
    public List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status);
    public void deleteById(String codigo);
    public void deleteArtigoById(String codigo);

    public void atualizarArtigo(String codigo, String novaURL);


    // Consultas Personalizadas Complexas
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data);
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate);

    public List<Artigo> encontrarAtrigosComplexos(Integer status, LocalDateTime data, String titulo);


    // Consultas Paginadas
    Page<Artigo> listarArtigos(Pageable pageable);

    public List<Artigo> findByStatusOrderByTituloAsc(Integer status);
    public List<Artigo> obterArtigosPorStatusComOrdenacao(Integer status);


    // Consulta por Índice
    public List<Artigo> findByTexto(String searchText);


    // Contar Artigo por Status
    public List<ArtigoStatusCount> contarArtigosPorStatus();


    // Contar Total de Artigos por Autor
    public List<AutorTotalArtigo> calculartotalArtigosPorAutorNoPeriodo(LocalDate dataInicio, LocalDate dataFim);


    public void excluirArtigoEautor(Artigo artigo);
}














