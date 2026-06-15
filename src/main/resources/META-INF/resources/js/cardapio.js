const API = 'http://localhost:8080';

function getToken()  { return localStorage.getItem('token'); }
function getPerfil() { return localStorage.getItem('perfil'); }
function getNome()   { return localStorage.getItem('nome'); }

function logout() {
    fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'same-origin' // Necessário para enviar/remover cookies
    })
    .then(() => {
        localStorage.clear();
        window.location.href = '/'; // Redireciona para a página inicial
    })
    .catch((erro) => {
        console.error('Erro ao fazer logout:', erro);
        // Mesmo com erro, limpa o localStorage
        localStorage.clear();
        window.location.href = '/';
    });
}

function irParaDashboard() {
    window.location.href = getPerfil() === 'ADMIN'
        ? '/dashboard' : '/reserva';
}

function irParaReserva() {
    if (!getToken()) {
        localStorage.setItem('redirect', '/reserva');
        window.location.href = '/login';
    } else {
        window.location.href = '/reserva';
    }
}

function atualizarNavbar() {
    const logado = !!getToken();
    document.getElementById('btn-entrar-nav')
        .classList.toggle('hidden', logado);
    document.getElementById('nav-usuario')
        .classList.toggle('hidden', !logado);
    if (logado) {
        document.getElementById('nav-nome').textContent = getNome();
    }
}

async function carregarCardapio() {
    try {
        const res = await fetch(`${API}/api/cardapio`);
        const pratos = await res.json();
        const tbody = document.getElementById('cardapio-body');

        if (pratos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3">Nenhum prato disponível.</td></tr>';
            return;
        }

        tbody.innerHTML = pratos.map(p => `
            <tr>
                <td>${p.nome}</td>
                <td>${p.descricao}</td>
                <td>R$ ${p.preco.toFixed(2).replace('.', ',')}</td>
            </tr>
        `).join('');

    } catch (e) {
        document.getElementById('cardapio-body').innerHTML =
            '<tr><td colspan="3">Erro ao carregar cardápio.</td></tr>';
    }
}

atualizarNavbar();
carregarCardapio();
