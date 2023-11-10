package com.fiap.springblog.repository;

import com.fiap.springblog.model.Autor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IAutorRepository extends MongoRepository<Autor, String> {

}
