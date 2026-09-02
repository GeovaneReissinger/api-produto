// Lógica da tela login.html: envia usuário/senha para POST /login e, se a
// autenticação for bem-sucedida, salva o token JWT no localStorage e
// redireciona para cadastro.html.

const API_URL = 'http://localhost:8080';

const formLogin = document.getElementById('formLogin');
const mensagemLogin = document.getElementById('mensagemLogin');

formLogin.addEventListener('submit', async function (event) {
    event.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    mensagemLogin.textContent = '';

    try {
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (response.status === 401) {
            mensagemLogin.textContent = 'Usuário ou senha inválidos.';
            return;
        }

        if (!response.ok) {
            mensagemLogin.textContent = 'Não foi possível realizar o login.';
            return;
        }

        const token = await response.text();

        localStorage.setItem('token', token);
        mensagemLogin.textContent = 'Login realizado com sucesso!';

        window.location.href = 'cadastro.html';
    } catch (error) {
        console.error(error);
        mensagemLogin.textContent = 'Erro de comunicação com a API.';
    }
});
