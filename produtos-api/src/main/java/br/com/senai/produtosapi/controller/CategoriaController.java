package br.com.senai.produtosapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.senai.produtosapi.exception.CategoriaNotFoundException;
import br.com.senai.produtosapi.model.Categoria;
import br.com.senai.produtosapi.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Categorias", description = "Operações de cadastro e consulta de categorias")
@RestController
@RequestMapping("/categorias")

public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Cadastrar uma nova categoria")
    @PostMapping
    public Categoria salvar(@Valid @RequestBody Categoria categoria) {
        return categoriaService.salvar(categoria);
    }

    @Operation(summary = "Listar todas as categorias")
    @GetMapping
    public List<Categoria> listarTodas() {
        return categoriaService.listarTodas();
    }

    @Operation(summary = "Buscar uma categoria por id")
    @GetMapping("/{id}")
    public Categoria buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id);
    }

    @Operation(summary = "Atualizar uma categoria existente")
    @PutMapping("/{id}")
    public Categoria atualizar(@PathVariable Long id, @Valid @RequestBody Categoria categoria) {        
                
        return categoriaService.atualizar(id, categoria);
    }

    @Operation(summary = "Remover uma categoria")
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id){
        categoriaService.deletar(id);
    }

}
