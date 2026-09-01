package br.com.senai.produtosapi.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.senai.produtosapi.model.Produto;
import br.com.senai.produtosapi.service.FileStorageService;
import br.com.senai.produtosapi.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Produtos", description = "Operações de cadastro e consulta de produtos")
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final FileStorageService fileStorageService;

    public ProdutoController(ProdutoService produtoService, FileStorageService fileStorageService) {
        this.produtoService = produtoService;
        this.fileStorageService = fileStorageService;
    }

    // GET /produtos -> lista todos os produtos cadastrados

    @Operation(summary = "Listar todos os produtos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    })
    @GetMapping
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    // GET /produtos/{id} -> busca um produto específico

    @Operation(summary = "Buscar um produto pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public Produto buscarPorId(@Parameter(description = "Id do produto") @PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    // POST /produtos -> Cria um produto

    @Operation(summary = "Cadastrar um novo produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public Produto salvar(
            @Parameter(description = "Dados do produto a ser cadastrado") @Valid @RequestBody Produto produto) {
        return produtoService.salvar(produto);
    }

    // PUT /produtos/{id} -> atualiza um produto existente

    @Operation(summary = "Atualizar um novo produto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    public Produto atualizar(@Parameter(description = "Id do produto") @PathVariable Long id,
            @Parameter(description = "Novos dados do produto") @Valid @RequestBody Produto produto) {
        return produtoService.atualizar(id, produto);
    }

    // DELETE /produtos/{id} -> Remove um produto pelo id

    @Operation(summary = "Remover um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "Id do produto") @PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Listar produtos por categoria

    @Operation(summary = "Listar produtos de uma categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    })
    @GetMapping("/categoria/{categoriaId}")
    public List<Produto> buscarPorCategoria(@Parameter(description = "Id do produto") @PathVariable Long categoriaId) {
        return produtoService.buscarPorCategoria(categoriaId);
    }
    // Listar produtos por faixa de preco

    @Operation(summary = "Listar produtos dentro de uma faixa de preço")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos na faixa de preço retornada com sucesso")
    })
    @GetMapping("/faixa-preco")
    public List<Produto> buscarPorFaixaDePreco(
            @Parameter(description = "Preço mínimo") @RequestParam BigDecimal min,
            @Parameter(description = "Preço máximo") @RequestParam BigDecimal max) {
        return produtoService.buscarPorFaixaDePreco(min, max);
    }



    // Upload e Download de imagem

    @Operation(summary = "Enviar a imagem de um produto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagem enviada e associada ao produto com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PostMapping("/{id}/imagem")
    public Produto uploadImagem(
        @Parameter(description = "Id do produto") @PathVariable Long id,
        @Parameter(description = "Arquivo de imagem") 
        @RequestParam("arquivo") MultipartFile arquivo) throws IOException{
            
            String nomeArquivo = fileStorageService.salvar(arquivo);
            return produtoService.atualizarImagem(id, nomeArquivo);            
        }


    @Operation(summary = "Baixar a imagem de um produto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagem retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })  
    @GetMapping("/{id}/imagem")
    public ResponseEntity<Resource> baixarImagem(
        @Parameter(description = "Id do produto") @PathVariable Long id) throws IOException{
            Produto produto = produtoService.buscarPorId(id);
            Path caminho = Paths.get("uploads").resolve(produto.getImagem());

            Resource recurso = new UrlResource(caminho.toUri());
            MediaType tipoconteudo = MediaTypeFactory.getMediaType(recurso)
            .orElse(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.ok()
            .contentType(tipoconteudo)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"").body(recurso);
        }

}
