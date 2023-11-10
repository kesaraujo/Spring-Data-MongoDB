package com.fiap.springblog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Artigo {

    @Id
    private String codigo;

    @NotBlank(message = "Informe o título do artigo.")
    private String titulo;

    @NotNull(message = "Informe a data do artigo.")
    private LocalDateTime data;

    @TextIndexed
    @NotBlank(message = "Informe o texto do artigo.")
    private String texto;

    @NotBlank(message = "Informe a url do artigo.")
    private String url;

    @NotNull(message = "Informe o status do artigo.")
    private Integer status;   // 0- Inativo 1-Ativo

    @DBRef
    private Autor autor;

    @Version
    private Long version;

}
