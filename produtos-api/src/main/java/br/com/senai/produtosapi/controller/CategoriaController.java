package br.com.senai.produtosapi.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.produtosapi.exception.CategoriaNotFoundException;
import br.com.senai.produtosapi.model.Categoria;
import br.com.senai.produtosapi.repository.CategoriaRepository;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")

public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    @PostMapping
    public Categoria salvar(@Valid @RequestBody Categoria categoria){
        return categoriaRepository.save(categoria);
    }

    @GetMapping
    public List<Categoria> listarTodas(){
        return categoriaRepository.findAll();
    }

    @PutMapping("/{id}")
    public Categoria atualizar(@PathVariable Long id, @Valid @RequestBody Categoria categoriaAtualizada){
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaNotFoundException(id));
        categoria.setNome(categoriaAtualizada.getNome());
        return categoriaRepository.save(categoria);
    }

}
