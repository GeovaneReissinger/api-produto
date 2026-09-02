// Lógica da tela cadastro.html: carrega e cadastra categorias e produtos
// consumindo a API (autenticada via JWT salvo no localStorage), e redireciona
// para o login caso o token esteja ausente ou a API responda 401.

const API_URL = 'http://localhost:8080';

const formCategoria = document.getElementById('formCategoria');
const formProduto = document.getElementById('formProduto');
const btnSair = document.getElementById('btnSair');

const nomeCategoria = document.getElementById('nomeCategoria');
const nomeProduto = document.getElementById('nomeProduto');
const descricaoProduto = document.getElementById('descricaoProduto');
const precoProduto = document.getElementById('precoProduto');
const categoriaProduto = document.getElementById('categoriaProduto');

const mensagemCategoria = document.getElementById('mensagemCategoria');
const mensagemProduto = document.getElementById('mensagemProduto');
const listaCategorias = document.getElementById('listaCategorias');
const listaProdutos = document.getElementById('listaProdutos');

function obterToken() {
    return localStorage.getItem('token');
}

function headersComToken() {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${obterToken()}`
    };
}

function redirecionarParaLogin() {
    localStorage.removeItem('token');
    window.location.href = 'login.html';
}

function verificarToken() {
    if (!obterToken()) {
        window.location.href = 'login.html';
        return false;
    }

    return true;
}

function tratarRespostaNaoAutorizada(response) {
    if (response.status === 401) {
        redirecionarParaLogin();
        return true;
    }

    return false;
}

function formatarMoeda(valor) {
    return Number(valor).toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    });
}

function escaparHtml(texto) {
    const div = document.createElement('div');
    div.textContent = texto ?? '';
    return div.innerHTML;
}

async function carregarCategorias() {
    try {
        const response = await fetch(`${API_URL}/categorias`, {
            headers: {
                'Authorization': `Bearer ${obterToken()}`
            }
        });

        if (tratarRespostaNaoAutorizada(response)) {
            return [];
        }

        if (!response.ok) {
            throw new Error('Erro ao consultar categorias.');
        }

        const categorias = await response.json();

        categoriaProduto.innerHTML = `
            <option value="">Selecione uma categoria</option>
        `;

        listaCategorias.innerHTML = '';

        if (categorias.length === 0) {
            listaCategorias.innerHTML = '<li>Nenhuma categoria cadastrada.</li>';
            return categorias;
        }

        categorias.forEach(categoria => {
            const option = document.createElement('option');
            option.value = categoria.id;
            option.textContent = categoria.nome;
            categoriaProduto.appendChild(option);

            const item = document.createElement('li');
            item.textContent = categoria.nome;
            listaCategorias.appendChild(item);
        });

        return categorias;
    } catch (error) {
        console.error(error);

        categoriaProduto.innerHTML = `
            <option value="">Erro ao carregar categorias</option>
        `;
        listaCategorias.innerHTML = '<li>Erro ao carregar categorias.</li>';

        return [];
    }
}

async function carregarProdutos() {
    try {
        const response = await fetch(`${API_URL}/produtos`, {
            headers: {
                'Authorization': `Bearer ${obterToken()}`
            }
        });

        if (tratarRespostaNaoAutorizada(response)) {
            return [];
        }

        if (!response.ok) {
            throw new Error('Erro ao consultar produtos.');
        }

        const produtos = await response.json();

        listaProdutos.innerHTML = '';

        if (produtos.length === 0) {
            listaProdutos.innerHTML = '<p>Nenhum produto cadastrado.</p>';
            return produtos;
        }

        produtos.forEach(produto => {
            const item = document.createElement('div');
            item.classList.add('produto');

            const nome = escaparHtml(produto.nome);
            const descricao = escaparHtml(produto.descricao || 'Sem descrição');
            const categoria = escaparHtml(produto.categoria?.nome || 'Não informada');

            const imagemProduto = produto.imagem
                ? `
                    <img
                        class="produto-imagem"
                        src="${API_URL}/produtos/${produto.id}/imagem"
                        alt="Imagem do produto ${nome}"
                    >
                `
                : `
                    <div class="produto-sem-imagem">
                        Sem imagem
                    </div>
                `;

            item.innerHTML = `
                ${imagemProduto}

                <h3>${nome}</h3>

                <p>${descricao}</p>

                <p><strong>${formatarMoeda(produto.preco)}</strong></p>

                <p>Categoria: ${categoria}</p>
            `;

            listaProdutos.appendChild(item);
        });

        return produtos;
    } catch (error) {
        console.error(error);
        listaProdutos.innerHTML = '<p>Erro ao carregar produtos.</p>';

        return [];
    }
}

formCategoria.addEventListener('submit', async function (event) {
    event.preventDefault();

    const nome = nomeCategoria.value.trim();
    mensagemCategoria.textContent = '';

    if (!nome) {
        mensagemCategoria.textContent = 'Informe o nome da categoria.';
        return;
    }

    try {
        const categorias = await carregarCategorias();
        const nomeNormalizado = nome.toLowerCase();

        const existe = categorias.some(
            categoria => categoria.nome.toLowerCase() === nomeNormalizado
        );

        if (existe) {
            mensagemCategoria.textContent = 'Essa categoria já está cadastrada.';
            return;
        }

        const response = await fetch(`${API_URL}/categorias`, {
            method: 'POST',
            headers: headersComToken(),
            body: JSON.stringify({
                nome: nome
            })
        });

        if (tratarRespostaNaoAutorizada(response)) {
            return;
        }

        if (!response.ok) {
            throw new Error('Erro ao cadastrar categoria.');
        }

        mensagemCategoria.textContent = 'Categoria cadastrada com sucesso!';
        formCategoria.reset();

        await carregarCategorias();
    } catch (error) {
        console.error(error);
        mensagemCategoria.textContent = 'Erro ao cadastrar categoria.';
    }
});

formProduto.addEventListener('submit', async function (event) {
    event.preventDefault();

    const nome = nomeProduto.value.trim();
    const descricao = descricaoProduto.value.trim();
    const preco = Number(precoProduto.value);
    const categoriaId = Number(categoriaProduto.value);

    mensagemProduto.textContent = '';

    if (!nome) {
        mensagemProduto.textContent = 'Informe o nome do produto.';
        return;
    }

    if (Number.isNaN(preco) || preco <= 0) {
        mensagemProduto.textContent = 'Informe um preço válido.';
        return;
    }

    if (!categoriaId) {
        mensagemProduto.textContent = 'Selecione uma categoria.';
        return;
    }

    try {
        const produtos = await carregarProdutos();
        const nomeNormalizado = nome.toLowerCase();

        const existe = produtos.some(
            produto => produto.nome.toLowerCase() === nomeNormalizado
        );

        if (existe) {
            mensagemProduto.textContent = 'Esse produto já está cadastrado.';
            return;
        }

        const response = await fetch(`${API_URL}/produtos`, {
            method: 'POST',
            headers: headersComToken(),
            body: JSON.stringify({
                nome: nome,
                descricao: descricao,
                preco: preco,
                categoria: {
                    id: categoriaId
                }
            })
        });

        if (tratarRespostaNaoAutorizada(response)) {
            return;
        }

        if (!response.ok) {
            throw new Error('Erro ao cadastrar produto.');
        }

        mensagemProduto.textContent = 'Produto cadastrado com sucesso!';
        formProduto.reset();

        await carregarProdutos();
    } catch (error) {
        console.error(error);
        mensagemProduto.textContent = 'Erro ao cadastrar produto.';
    }
});

btnSair.addEventListener('click', function () {
    redirecionarParaLogin();
});

if (verificarToken()) {
    carregarCategorias();
    carregarProdutos();
}
