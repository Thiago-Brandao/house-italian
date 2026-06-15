function getToken()  { return localStorage.getItem('token'); }
function getPerfil() { return localStorage.getItem('perfil'); }
function getNome()   { return localStorage.getItem('nome'); }

function irParaReserva() {
    if (!getToken()) {
        localStorage.setItem('redirect', '/reserva');
        window.location.href = '/login';
    } else if (getPerfil() === 'ADMIN') {
        window.location.href = '/dashboard';
    } else {
        window.location.href = '/reserva';
    }
}

function logout() {
    fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'same-origin' // Necessário para enviar/remover cookies
    })
    .then(() => {
        localStorage.clear();
        atualizarNavbar();
        window.location.href = '/'; // Redireciona para a página inicial
    })
    .catch((erro) => {
        console.error('Erro ao fazer logout:', erro);
        // Mesmo com erro, limpa o localStorage
        localStorage.clear();
        atualizarNavbar();
        window.location.href = '/';
    });
}

function atualizarNavbar() {
    const itemEntrar = document.getElementById('item4-entrar');
    const itemUsuario = document.getElementById('item4-usuario');
    const nomeSpan = document.getElementById('header-nome-usuario');

    if (getToken()) {
        itemEntrar.style.display = 'none';
        itemUsuario.style.display = 'block';
        nomeSpan.textContent = getNome();
    } else {
        itemEntrar.style.display = 'block';
        itemUsuario.style.display = 'none';
    }
}

atualizarNavbar();
