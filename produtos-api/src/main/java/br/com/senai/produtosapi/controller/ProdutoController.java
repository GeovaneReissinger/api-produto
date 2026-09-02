package br.com.senai.produtosapi.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import br.com.senai.produtosapi.exception.ImagemNotFoundException;
import br.com.senai.produtosapi.model.Produto;
import br.com.senai.produtosapi.service.FileStorageService;
import br.com.senai.produtosapi.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Endpoints REST de produtos (/produtos): CRUD completo, buscas por categoria e
 * por faixa de preço, além de upload/download da imagem do produto. As regras de
 * negócio ficam no {@link br.com.senai.produtosapi.service.ProdutoService} e o
 * armazenamento de arquivos no {@link br.com.senai.produtosapi.service.FileStorageService}.
 */
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

    @Operation(summary = "Listar todos os produtos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @Operation(summary = "Buscar um produto pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@Parameter(description = "Id do produto") @PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @Operation(summary = "Cadastrar um novo produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<Produto> salvar(
            @Parameter(description = "Dados do produto a ser cadastrado") @Valid @RequestBody Produto produto) {
        Produto salvo = produtoService.salvar(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @Operation(summary = "Atualizar um produto existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(
            @Parameter(description = "Id do produto") @PathVariable Long id,
            @Parameter(description = "Novos dados do produto") @Valid @RequestBody Produto produto) {
        return ResponseEntity.ok(produtoService.atualizar(id, produto));
    }

    @Operation(summary = "Remover um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@Parameter(description = "Id do produto") @PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar produtos de uma categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos da categoria retornada com sucesso")
    })
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(
            @Parameter(description = "Id da categoria") @PathVariable Long categoriaId) {
        return ResponseEntity.ok(produtoService.buscarPorCategoria(categoriaId));
    }

    @Operation(summary = "Listar produtos dentro de uma faixa de preço")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos na faixa de preço retornada com sucesso")
    })
    @GetMapping("/faixa-preco")
    public ResponseEntity<List<Produto>> buscarPorFaixaDePreco(
            @Parameter(description = "Preço mínimo") @RequestParam BigDecimal min,
            @Parameter(description = "Preço máximo") @RequestParam BigDecimal max) {
        return ResponseEntity.ok(produtoService.buscarPorFaixaDePreco(min, max));
    }

    // --- Upload e download de imagem (Aula 8) ---

    @Operation(summary = "Enviar a imagem de um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagem enviada e associada ao produto com sucesso"),
            @ApiResponse(responseCode = "400", description = "Arquivo ausente, vazio ou de tipo inválido"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PostMapping("/{id}/imagem")
    public ResponseEntity<Produto> uploadImagem(
            @Parameter(description = "Id do produto") @PathVariable Long id,
            @Parameter(description = "Arquivo de imagem (JPEG, PNG, GIF ou WEBP)") @RequestParam("arquivo") MultipartFile arquivo) {
        // Garante que o produto existe antes de gravar qualquer arquivo em disco.
        produtoService.buscarPorId(id);

        String nomeArquivo = fileStorageService.salvar(arquivo);
        Produto atualizado = produtoService.atualizarImagem(id, nomeArquivo);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(summary = "Baixar a imagem de um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagem retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto ou imagem não encontrados")
    })
    @GetMapping("/{id}/imagem")
    public ResponseEntity<Resource> baixarImagem(
            @Parameter(description = "Id do produto") @PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        if (produto.getImagem() == null) {
            throw new ImagemNotFoundException(id);
        }

        Resource recurso = fileStorageService.carregar(produto.getImagem(), id);
        String contentType = fileStorageService.detectarContentType(produto.getImagem());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }
}
