package com.fiap.springblog.controller;

import com.fiap.springblog.model.*;
import com.fiap.springblog.service.IArtigoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/artigos")
public class ArtigoController {

    @Autowired
    private IArtigoService artigoService;


    // Consuoltas Padrão do MongoDB
//    @PostMapping
//    public Artigo criarArtigo(@Valid @RequestBody Artigo artigo) {
//        return this.artigoService.criarArtigo(artigo);
//    }


    @PostMapping
    public ResponseEntity<?> criarArtigoComAutor(@RequestBody ArtigoComAutorRequest request) {

        Artigo artigo = request.getArtigo();
        Autor autor = request.getAutor();

        return this.artigoService.criarArtigoComAutor(artigo, autor);
    }


//    @PostMapping
//    public ResponseEntity<?> criar(@Valid @RequestBody Artigo artigo) {
//        return this.artigoService.criar(artigo);
//    }


    @PutMapping("/atualizar-artigo/{codigo}")
    public ResponseEntity<?> atualziarArtigo(@PathVariable("codigo") String codigo,
                                             @Valid @RequestBody Artigo artigo) {
        return this.artigoService.atualizarArtigo(codigo, artigo);
    }


    @GetMapping
    public List<Artigo> obterTodos() {
        return this.artigoService.obterTodos();
    }


    @GetMapping("/{codigo}")
    public Artigo obterPorCodigo(@PathVariable  String codigo) {
        return this.artigoService.obterPorCodigo(codigo);
    }


    @PutMapping
    public void atualizarArtigo(@Valid @RequestBody Artigo artigo) {
        this.artigoService.atualizarArtigo(artigo);
    }


    @PutMapping("/{codigo}")
    public void atualizarArtigo(@PathVariable("codigo") String codigo, @RequestBody String novaURL) {
        this.artigoService.atualizarArtigo(codigo, novaURL);
    }



    // Consultas do Devenvolvedor
    @GetMapping("maiordata")
    public  List<Artigo> findByDataGreaterThan(@RequestParam("data") LocalDateTime data) {
        return this.artigoService.findByDataGreaterThan(data);
    }


    @GetMapping("datastatus")
    public List<Artigo> searchDataAndState(@RequestParam("data") LocalDateTime data,
                                           @RequestParam("status") Integer status) {
        return this.artigoService.findByDataAndStatus(data, status);
    }


    @DeleteMapping("/{codigo}")
    public void deleteArtigo(@PathVariable String codigo) {
        this.artigoService.deleteById(codigo);
    }


    @DeleteMapping("/delete")
    public void deleteArtigoById(@RequestParam("codigo") String codigo) {
        this.artigoService.deleteArtigoById(codigo);
    }


    @GetMapping("/status-maiordata")
    public List<Artigo> findByStatusAndDataGreaterThan(
            @RequestParam("status") Integer status,
            @RequestParam("data") LocalDateTime data) {
        return this.artigoService.findByStatusAndDataGreaterThan(status, data);
    }


    @GetMapping("/periodo")
    public List<Artigo> obterArtigoPorDataHora(@RequestParam("de") LocalDateTime de,
                                               @RequestParam("ate") LocalDateTime ate) {
        return this.artigoService.obterArtigoPorDataHora(de, ate);
    }


    @GetMapping("/artigo-complexo")
    public List<Artigo> encontrarAtrigosComplexos(
            @RequestParam("status") Integer status,
            @RequestParam("data") LocalDateTime data,
            @RequestParam("titulo") String titulo) {
        return this.artigoService.encontrarAtrigosComplexos(status, data, titulo);
    }


    @GetMapping("/pagina-artigos")
    public ResponseEntity<Page<Artigo>> listarArtigosPaginados(Pageable pageable) {
        Page<Artigo> artigos = this.artigoService.listarArtigos(pageable);
        return ResponseEntity.ok(artigos);
    }


    @GetMapping("/status-ordenado")
    public List<Artigo> findByStatusOrderByTituloAsc(@RequestParam("status") Integer status) {
        return this.artigoService.findByStatusOrderByTituloAsc(status);
    }


    @GetMapping("/status-query-ordenado")
    public List<Artigo> obterArtigosPorStatusComOrdenacao(@RequestParam("status") Integer status) {
        return this.artigoService.obterArtigosPorStatusComOrdenacao(status);
    }


    @GetMapping("buscatexto")
    public List<Artigo> findByTexto(@RequestParam("searchText") String searchText) {
        return  this.artigoService.findByTexto(searchText);
    }


    @GetMapping("/contar-artigos")
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        return this.artigoService.contarArtigosPorStatus();
    }


    @GetMapping("/contar-artigos-autor")
    public List<AutorTotalArtigo> contarArtigosPorAutor(
            @RequestParam("dataInicio") LocalDate dataInicio,
            @RequestParam("dataFim") LocalDate dataFim) {
        return this.artigoService.calculartotalArtigosPorAutorNoPeriodo(dataInicio, dataFim);
    }





    // Controle de Transação
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingFailureException(OptimisticLockingFailureException exception) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                "Erro de concorrência: O 'Artigo' foi atualizado por outro usuário. Tente novamente!"
        );

    }

    @DeleteMapping("/delete-artigo-autor")
    public void deleteArtigoComAutor(@RequestBody Artigo artigo) {
        this.artigoService.excluirArtigoEautor(artigo);
    }
}
