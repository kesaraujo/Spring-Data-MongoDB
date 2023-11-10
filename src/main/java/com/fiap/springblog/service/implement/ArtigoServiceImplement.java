package com.fiap.springblog.service.implement;

import com.fiap.springblog.model.Artigo;
import com.fiap.springblog.model.ArtigoStatusCount;
import com.fiap.springblog.model.Autor;
import com.fiap.springblog.model.AutorTotalArtigo;
import com.fiap.springblog.repository.IArtigoRepository;
import com.fiap.springblog.repository.IAutorRepository;
import com.fiap.springblog.service.IArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoServiceImplement implements IArtigoService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    private MongoTransactionManager transactionManager;

    @Autowired
    private IArtigoRepository artigoRepository;

    @Autowired
    private IAutorRepository autorRepository;


    public ArtigoServiceImplement(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public List<Artigo> obterTodos() {
        return this.artigoRepository.findAll();
    }


    @Override
    @Transactional(readOnly = true)
    public Artigo obterPorCodigo(String codigo) {
        return this.artigoRepository.findById(codigo).orElseThrow(
                () -> new IllegalArgumentException("Artigo inexistente.")
        );
    }


//    @Override
//    @Transactional
//    public Artigo criarArtigo(Artigo artigo) {
//
//        // Se autor existe
//        if (artigo.getAutor().getCodigo() != null) {
//
//            // Recuperar autor
//            Autor autor = this.autorRepository
//                    .findById(artigo.getAutor().getCodigo())
//                    .orElseThrow(() -> new IllegalArgumentException("Autor inexistente."));
//
//            artigo.setAutor(autor);
//        } else {
//
//            // Caso contrário gravo artigo sem autor
//            artigo.setAutor(null);
//        }
//
//        try {
//
//            // Salvo artigo com autor
//            return this.artigoRepository.save(artigo);
//
//        } catch (OptimisticLockingFailureException ex) {
//
//            // desenvolver estratégia
//            // 1. Recuperar documento (Artigo) mais recente
//            Artigo atualizado = artigoRepository.findAllById(artigo.getCodigo()).orElse(null);
//
//            if (atualizado != null) {
//
//                // 2. Atualizar campos desejados
//                atualizado.setTitulo(artigo.getTitulo());
//                atualizado.setTexto(artigo.getTexto());
//                atualizado.setStatus(artigo.getStatus());
//
//                // 3. Incrementar a versão do documento
//                atualizado.setVersion(artigo.getVersion() + 1);
//
//                // 4. Salvar novamente
//                return this.artigoRepository.save(artigo);
//            } else {
//
//                // Documento não encontrado. Tratar de forma adequada
//                throw new RuntimeException(
//                        "Artigo não encontrado" + artigo.getCodigo()
//                );
//            }
//
//        }
//
//    }



//    // Com Tratamento de erro
//    @Override
//    public ResponseEntity<?> criar(Artigo artigo) {
//
//        if (artigo.getAutor().getCodigo() != null) {
//
//            // Recuperar autor
//            Autor autor = this.autorRepository
//                    .findById(artigo.getAutor().getCodigo())
//                    .orElseThrow(
//                            () -> new IllegalArgumentException("Autor inexistente.")
//                    );
//
//            artigo.setAutor(autor);
//        } else {
//
//            // Caso contrário gravo artigo sem autor
//            artigo.setAutor(null);
//        }
//
//        try {
//
//            // Salvo/retorna o artigo com autor
//            this.artigoRepository.save(artigo);
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//
//        } catch (DuplicateKeyException e) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("Artigo existente na coleção");
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Erro ao criar artigo: " + e.getMessage());
//        }
//    }

    @Override
    public ResponseEntity<?> atualizarArtigo(String codigo, Artigo artigo) {
        try {
            Artigo exiteArtigo = this.artigoRepository.findById(codigo).orElse(null);

            if (exiteArtigo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Artigo inexistente na coleção.");
            }

            // Atualizar Artigo
            exiteArtigo.setTitulo(artigo.getTitulo());
            exiteArtigo.setTexto(artigo.getTexto());
            exiteArtigo.setData(artigo.getData());

            // Salvando o Artigo
            this.artigoRepository.save(exiteArtigo);

            // Retornando status da alteração
            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar artigo: " + e.getMessage());
        }
    }


    @Override
    @Transactional
    public void atualizarArtigo(Artigo artigo) {
        this.artigoRepository.save(artigo);
    }




    // Segunda forma transacionada ( Não adiciona @Transaction )
    @Override
    public ResponseEntity<?> criarArtigoComAutor(Artigo artigo, Autor autor) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            try {

                // Iniciar Transação
                autorRepository.save(autor);
                artigo.setData(LocalDateTime.now());
                artigo.setAutor(autor);
                artigoRepository.save(artigo);

            } catch(Exception e) {

                // Tratar erro e lançar exceção transação
                status.setRollbackOnly();
                throw new RuntimeException("erro ao criar artigo com autor" + e.getMessage());
            }
            return null;

        });

        return null;
    }




    @Override
    public List<Artigo> findByDataGreaterThan(LocalDateTime data) {
            Query query = new Query(Criteria
                    .where("data").gt(data));

            return mongoTemplate.find(query, Artigo.class);
    }


    @Override
    public List<Artigo> findByDataAndStatus(LocalDateTime data, Integer status) {

        Query query = new Query(Criteria.
                where("data").is(data).
                and("Status").is(status));

        return mongoTemplate.find(query, Artigo.class);

    }


    @Override
    @Transactional
    public void atualizarArtigo(String codigo, String novaURL) {
        // Critério de busca por id
        Query query = new Query(Criteria.where("codigo").is(codigo));

        // Definindo campos a serem atualizados
        Update update = new Update().set("url", novaURL);

        // Executando a atualização
        this.mongoTemplate.updateFirst(query, update, Artigo.class);
    }


    @Override
    @Transactional
    public void deleteById(String codigo) {
        this.artigoRepository.deleteById(codigo);
    }


    @Override
    @Transactional
    public void deleteArtigoById(String codigo) {
        Query query = new Query(Criteria.where("codigo").is(codigo));
        mongoTemplate.remove(query, Artigo.class);
    }


    @Override
    public List<Artigo> findByStatusAndDataGreaterThan(Integer status, LocalDateTime data) {
        return this.artigoRepository.findByStatusAndDataGreaterThan(status, data);
    }

    @Override
    public List<Artigo> obterArtigoPorDataHora(LocalDateTime de, LocalDateTime ate) {
        return this.artigoRepository.obterArtigoPorDataHora(de, ate);
    }


    @Override
    public List<Artigo> encontrarAtrigosComplexos(Integer status, LocalDateTime data, String titulo) {

        // Filtrar artigos com data menor ou igual a data informada
        Criteria criteria = new Criteria();
        criteria.and("data").lte(data);

        // Filtrar artigos com status informado
        if (status != null) {
            criteria.and("status").is(status);
        }

        // Filtrar artigos com título informado
        if (titulo != null && titulo.isEmpty()) {
            criteria.and("titulo").regex(titulo, "i");
        }

        // Executo a consulta
        Query query = new Query(criteria);

        // retorna a lista de artigos
        return mongoTemplate.find(query, Artigo.class);
    }


    @Override
    public Page<Artigo> listarArtigos(Pageable pageable) {
        Sort sort = Sort.by("titulo").ascending();
        Pageable paginacao = PageRequest.of(
                pageable.getPageNumber(), pageable.getPageSize(), sort);
        return this.artigoRepository.findAll(paginacao);
    }


    @Override
    public List<Artigo> findByStatusOrderByTituloAsc(Integer status) {
        return this.artigoRepository.findByStatusOrderByTituloAsc(status);
    }


    @Override
    public List<Artigo> obterArtigosPorStatusComOrdenacao(Integer status) {
        return this.artigoRepository.obterArtigosPorStatusComOrdenacao(status);
    }


    @Override
    public List<Artigo> findByTexto(String searchText) {
        TextCriteria criteria =
                TextCriteria.forDefaultLanguage().matchingPhrase(searchText);
        Query query = TextQuery.queryText(criteria).sortByScore();
        return mongoTemplate.find(query, Artigo.class);
    }


    @Override
    public List<ArtigoStatusCount> contarArtigosPorStatus() {
        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.group("status").count().as("quantidade"),
                        Aggregation.project("quantidade").and("status").
                                previousOperation());

        AggregationResults<ArtigoStatusCount> result =
                mongoTemplate.aggregate(aggregation, ArtigoStatusCount.class);

        return result.getMappedResults();
    }


    @Override
    public List<AutorTotalArtigo> calculartotalArtigosPorAutorNoPeriodo(LocalDate dataInicio, LocalDate dataFim) {

        TypedAggregation<Artigo> aggregation =
                Aggregation.newAggregation(
                        Artigo.class,
                        Aggregation.match(Criteria.where("data").gte(dataInicio.atStartOfDay()).lt(dataFim.plusDays(1).atStartOfDay())),
                        Aggregation.group("autor").count().as("totalArtigos"), Aggregation.project("totalArtigos").and("autor").previousOperation()
                );

        AggregationResults<AutorTotalArtigo> results =
                mongoTemplate.aggregate(aggregation, AutorTotalArtigo.class);

        return results.getMappedResults();
    }


    @Override
    public void excluirArtigoEautor(Artigo artigo) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {

            try {

                artigoRepository.delete(artigo);
                Autor autor = artigo.getAutor();
                autorRepository.delete(autor);

            } catch(Exception e) {

                // Tratar erro e lançar transação de volta caso exceção
                status.setRollbackOnly();
                throw new RuntimeException("erro ao criar artigo com autor" + e.getMessage());
            }
            return null;
        });
    }
}
